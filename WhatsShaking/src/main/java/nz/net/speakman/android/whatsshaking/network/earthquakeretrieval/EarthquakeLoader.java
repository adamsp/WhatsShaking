package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import com.j256.ormlite.dao.Dao;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs.UsgsRetrieval;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;
import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Adam on 30/12/13.
 */
public class EarthquakeLoader extends AsyncTaskLoader<Boolean> {

    public EarthquakeLoader(Context context) {
        super(context);
    }



    @Override
    public Boolean loadInBackground() {
        LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getContext());
        broadcastManager.sendBroadcast(new Intent(Earthquake.DATA_RETRIEVAL_STARTED));
        boolean success = false;
        try {
            EarthquakeRetrieval earthquakeRetrieval = new UsgsRetrieval();
            List<Earthquake> earthquakes = earthquakeRetrieval.getEarthquakes(getContext());
            if (earthquakes != null) {
                success = storeEarthquakesInDb(earthquakes);
                // If we succeeded in downloading & storing quakes, update our "last checked" date to now.
                if (success) {
                    new Preferences(getContext()).setLastCheckedDate(new DateTime());
                }
            }
        } catch (Exception e) {
            // If we get some unhandled exception here, then our data retrieval or storage probably failed.
            success = false;
        } finally {
            Intent intent = success ? new Intent(Earthquake.DATA_RETRIEVAL_SUCCESSFUL) : new Intent(Earthquake.DATA_RETRIEVAL_FAILED);
            broadcastManager.sendBroadcast(intent);
            return success;
        }
    }

    private boolean storeEarthquakesInDb(final List<Earthquake> earthquakes) {
        boolean success;
        DBHelper helper = DBHelper.getInstance(getContext());
        try {
            final Dao<Earthquake, Integer> earthquakeDao = Earthquake.getDao(helper.getConnectionSource());
            earthquakeDao.callBatchTasks(new Callable<Void>() {
                @Override
                public Void call() throws SQLException {
                    // TODO Check each quake that is older than last-checked; if it doesn't exist, check for existence of other IDs.
                    for (Earthquake eq : earthquakes) {
                        earthquakeDao.createOrUpdate(eq);
                    }
                    return null;
                }
            });
            success = true;
        } catch (SQLException e) {
            success = false;
        } catch (Exception e) {
            success = false;
        } finally {
            DBHelper.releaseHelper();
        }
        return success;
    }
}
