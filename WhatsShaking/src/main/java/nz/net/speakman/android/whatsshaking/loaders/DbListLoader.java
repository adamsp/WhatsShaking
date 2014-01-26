package nz.net.speakman.android.whatsshaking.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 26/01/14.
 */
public class DbListLoader extends AsyncTaskLoader<List<Earthquake>> {
    private static final long NO_COUNT = -1;

    private final Context mContext;
    private long mCount;

    public DbListLoader(Context ctx) {
        this(ctx, NO_COUNT);
    }

    public DbListLoader(Context ctx, long count) {
        super(ctx.getApplicationContext());
        mContext = ctx.getApplicationContext();
        mCount = count;
    }
    @Override
    public List<Earthquake> loadInBackground() {
        try {
            DBHelper dbHelper = DBHelper.getInstance(mContext);
            Dao<Earthquake, Integer> earthquakeDao = Earthquake.getDao(dbHelper.getConnectionSource());
            QueryBuilder<Earthquake,Integer> queryBuilder = earthquakeDao.queryBuilder();
            if (mCount > 0) {
                queryBuilder.limit(mCount);
            }
            return queryBuilder.where().raw(DBHelper.buildWhereClauseFromFilter(new Preferences(mContext))).query();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.releaseHelper();
        }
        return null;
    }
}
