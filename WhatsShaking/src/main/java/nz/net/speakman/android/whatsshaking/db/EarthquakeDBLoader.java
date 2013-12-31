package nz.net.speakman.android.whatsshaking.db;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.j256.ormlite.dao.Dao;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 31/12/13.
 */
public class EarthquakeDBLoader extends AsyncTaskLoader<List<Earthquake>> {

    // TODO Implement filtering!
    public EarthquakeDBLoader(Context context) {
        super(context);
    }

    @Override
    public List<Earthquake> loadInBackground() {
        DBHelper helper = DBHelper.getInstance(getContext());
        List<Earthquake> earthquakes = null;
        try {
            Dao<Earthquake,Integer> dao = Earthquake.getDao(helper.getConnectionSource());
            earthquakes = dao.queryBuilder().orderBy("eventTime", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.releaseHelper();
        }
        return earthquakes;
    }
}
