package nz.net.speakman.android.whatsshaking.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Adam on 3/01/14.
 */
public class Preferences {

    public static final String FILE_NAME = "nz.net.speakman.android.whatsshaking.preferences.PREFERENCES";

    public static final String KEY_LAST_CHECKED_DATE = "key_lastCheckedDate";

    public static final long DEFAULT_LAST_CHECKED_DATE = 0;

    private final Context mContext;
    private SharedPreferences mSharedPrefs;

    public Preferences(Context ctx) {
        mContext = ctx;
    }

    private SharedPreferences getPreferences() {
        if (mSharedPrefs == null) {
            mSharedPrefs = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return mSharedPrefs;
    }

    public DateTime getLastCheckedDate() {
        return new DateTime(getPreferences().getLong(KEY_LAST_CHECKED_DATE, DEFAULT_LAST_CHECKED_DATE));
    }

    public void setLastCheckedDate(DateTime date) {
        getPreferences().edit().putLong(KEY_LAST_CHECKED_DATE, date.getMillis()).commit();
    }

}
