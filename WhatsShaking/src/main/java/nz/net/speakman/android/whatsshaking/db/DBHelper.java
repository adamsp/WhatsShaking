package nz.net.speakman.android.whatsshaking.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.sql.SQLException;

/**
 * Created by Adam on 30/12/13.
 */
public class DBHelper extends OrmLiteSqliteOpenHelper {

    public static final String WHERE_SYMBOL_GT = " > ";
    public static final String WHERE_SYMBOL_GT_EQ = " >= ";
    public static final String WHERE_SYMBOL_LT = " < ";
    public static final String WHERE_SYMBOL_LT_EQ = " <= ";
    public static final String AND = " AND ";

    private static final String DATABASE_NAME = "whatsShaking.db";
    private static final int DATABASE_VERSION = 1;

    public static DBHelper getInstance(Context ctx) {
        return OpenHelperManager.getHelper(ctx, DBHelper.class);
    }

    public static void releaseHelper() {
        OpenHelperManager.releaseHelper();
    }

    /**
     * Do not use this directly. Use {@code DBHelper.getInstance(Context context)} instead.
     */
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Earthquake.class);
        } catch (SQLException e) {
            // If we can't create a table to store our database, everything else falls down anyway.
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {

    }
}
