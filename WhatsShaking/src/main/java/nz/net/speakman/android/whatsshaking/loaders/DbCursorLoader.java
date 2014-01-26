package nz.net.speakman.android.whatsshaking.loaders;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import com.j256.ormlite.dao.Dao;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.db.EarthquakeDbContract;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 31/12/13.
 */
public class DbCursorLoader extends AsyncTaskLoader<Cursor> {

    private static final String[] mListProjection = {
            EarthquakeDbContract.Columns.Id,
            EarthquakeDbContract.Columns.Place,
            EarthquakeDbContract.Columns.Magnitude,
            EarthquakeDbContract.Columns.EventTime,
            EarthquakeDbContract.Columns.CalculatedIntensity
    };

    private String mWhereClause;

    private static final String mListSortOrder = EarthquakeDbContract.Columns.EventTime + " DESC";

    // TODO Loading a Cursor is good (better than 1000+ quakes), but Android can do this for us if we implement a ContentProvider.
    public DbCursorLoader(Context context, String whereClause) {
        super(context);
        mWhereClause = whereClause;
    }

    @Override
    public Cursor loadInBackground() {
        DBHelper helper = DBHelper.getInstance(getContext());
        return helper.getReadableDatabase().query(EarthquakeDbContract.TableName,
                mListProjection,
                mWhereClause,
                null,
                null,
                null,
                mListSortOrder);
    }
}
