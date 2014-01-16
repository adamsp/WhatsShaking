package nz.net.speakman.android.whatsshaking.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import org.joda.time.DateTime;

/**
 * Created by Adam on 3/01/14.
 */
public class Preferences {

    public static final String FILE_NAME = "nz.net.speakman.android.whatsshaking.preferences.PREFERENCES";

    public static final String KEY_LAST_CHECKED_DATE = "key_lastCheckedDate";
    public static final String KEY_MINIMUM_MMI = "key_minimumMmi";
    public static final String KEY_MINIMUM_MAGNITUDE = "key_minimumMagnitude";
    public static final String KEY_DISPLAY_LAST_DAYS_COUNT = "key_displayLastDaysCount";

    public static final long DEFAULT_LAST_CHECKED_DATE = 0;
    public static final float DEFAULT_MINIMUM_MAGNITUDE = 0.0f;
    public static final float DEFAULT_MINIMUM_MMI = 0.0f;
    public static final int DEFAULT_DISPLAY_LAST_DAYS_COUNT = 30;

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

    public float getMinimumMmi() {
        return getPreferences().getFloat(KEY_MINIMUM_MMI, DEFAULT_MINIMUM_MMI);
    }

    public void setMinimumMmi(float minimumMmi) {
        getPreferences().edit().putFloat(KEY_MINIMUM_MMI, minimumMmi).commit();
    }

    public float getMinimumMagnitude() {
        return getPreferences().getFloat(KEY_MINIMUM_MAGNITUDE, DEFAULT_MINIMUM_MAGNITUDE);
    }

    public void setMinimumMagnitude(float minimumMagnitude) {
        getPreferences().edit().putFloat(KEY_MINIMUM_MAGNITUDE, minimumMagnitude).commit();
    }

    public int getDisplayLastDaysCount() {
        return getPreferences().getInt(KEY_DISPLAY_LAST_DAYS_COUNT, DEFAULT_DISPLAY_LAST_DAYS_COUNT);
    }

    public void setDisplayLastDaysCount(int dayCount) {
        getPreferences().edit().putInt(KEY_DISPLAY_LAST_DAYS_COUNT, dayCount).commit();
    }

}
