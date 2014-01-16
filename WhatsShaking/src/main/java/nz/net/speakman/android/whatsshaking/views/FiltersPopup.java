package nz.net.speakman.android.whatsshaking.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;


/**
 * Created by Adam on 7/01/14.
 */
public class FiltersPopup extends PopupWindow {


    public static final String FILTER_UPDATED_MAGNITUDE = "nz.net.speakman.android.whatsshaking.views.FILTER_UPDATED_MAGNITUDE";
    public static final String FILTER_UPDATED_MMI = "nz.net.speakman.android.whatsshaking.views.FILTER_UPDATED_MMI";
    public static final String FILTER_UPDATED_DAYS_COUNT = "nz.net.speakman.android.whatsshaking.views.FILTER_UPDATED_DAYS_COUNT";

    /**
     * Because seek bars require an integer max/progress value, we multiply our floats to suit.
     */
    private static final float SEEK_BAR_MULTIPLIER = 10.0f;
    private static final int MAGNITUDE_MAX = (int)(6 * SEEK_BAR_MULTIPLIER);
    private static final int MMI_MAX = (int)(10 * SEEK_BAR_MULTIPLIER);
    private static final int LAST_DAYS_MAX = 30;

    private final Preferences mPreferences;
    private final LocalBroadcastManager mBroadcastMgr;
    private final Context mContext;
    private TextView mMagnitudeLabel;
    private TextView mMmiLabel;
    private TextView mLastDaysCountLabel;
    private SeekBar mMagnitudeSeekBar;
    private SeekBar mMmiSeekBar;
    private SeekBar mLastDaysCountSeekBar;

    private final SeekBar.OnSeekBarChangeListener magnitudeChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateMagnitudeLabel(progress / SEEK_BAR_MULTIPLIER);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPreferences.setMinimumMagnitude(seekBar.getProgress() / SEEK_BAR_MULTIPLIER);
            mBroadcastMgr.sendBroadcast(new Intent(FILTER_UPDATED_MAGNITUDE));
        }
    };

    private final SeekBar.OnSeekBarChangeListener mmiChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateMmiLabel(progress / SEEK_BAR_MULTIPLIER);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPreferences.setMinimumMmi(seekBar.getProgress() / SEEK_BAR_MULTIPLIER);
            mBroadcastMgr.sendBroadcast(new Intent(FILTER_UPDATED_MMI));
        }
    };

    private final SeekBar.OnSeekBarChangeListener lastDaysCountChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            updateLastDaysCountLabel(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mPreferences.setDisplayLastDaysCount(seekBar.getProgress());
            mBroadcastMgr.sendBroadcast(new Intent(FILTER_UPDATED_DAYS_COUNT));
        }
    };

    public FiltersPopup(Activity activity) {
        super(activity.getLayoutInflater().inflate(R.layout.popup_filter, null),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPreferences = new Preferences(activity);
        mBroadcastMgr = LocalBroadcastManager.getInstance(activity);
        mContext = activity;
        // This is a workaround to make the popup auto-dismiss when tapped outside of.
        // See http://stackoverflow.com/a/3122696/1217087
        setBackgroundDrawable(new BitmapDrawable());
        setOutsideTouchable(true);
        // setFocusable required to ensure outside touch events simply dismiss the popup - like a Spinner.
        setFocusable(true);

        setupViews();

        setupListeners();
    }

    private void setupViews() {
        View v = getContentView();
        mMagnitudeLabel = (TextView) v.findViewById(R.id.filter_label_magnitude);
        mMagnitudeSeekBar = (SeekBar) v.findViewById(R.id.filter_seek_magnitude);
        mMagnitudeSeekBar.setMax(MAGNITUDE_MAX);
        updateMagnitudeLabel(mPreferences.getMinimumMagnitude());
        setMagnitudeSeekInitialProgress();

        mMmiLabel = (TextView) v.findViewById(R.id.filter_label_mmi);
        mMmiSeekBar = (SeekBar) v.findViewById(R.id.filter_seek_mmi);
        mMmiSeekBar.setMax(MMI_MAX);
        updateMmiLabel(mPreferences.getMinimumMmi());
        setMmiSeekInitialProgress();

        mLastDaysCountLabel = (TextView) v.findViewById(R.id.filter_label_last_days_count);
        mLastDaysCountSeekBar = (SeekBar) v.findViewById(R.id.filter_seek_last_days_count);
        mLastDaysCountSeekBar.setMax(LAST_DAYS_MAX);
        updateLastDaysCountLabel(mPreferences.getDisplayLastDaysCount());
        setLastDaysCountSeekInitialProgress();
    }

    private void setupListeners() {
        mMagnitudeSeekBar.setOnSeekBarChangeListener(magnitudeChangedListener);
        mMmiSeekBar.setOnSeekBarChangeListener(mmiChangedListener);
        mLastDaysCountSeekBar.setOnSeekBarChangeListener(lastDaysCountChangedListener);
    }

    private void updateMagnitudeLabel(float minMagnitude) {
        mMagnitudeLabel.setText(mContext.getString(R.string.filter_label_min_magnitude, minMagnitude));
    }

    private void setMagnitudeSeekInitialProgress() {
        float minMagnitude = mPreferences.getMinimumMagnitude();
        mMagnitudeSeekBar.setProgress((int)(minMagnitude * SEEK_BAR_MULTIPLIER));
    }

    private void updateMmiLabel(float minMmi) {
        mMmiLabel.setText(mContext.getString(R.string.filter_label_min_mmi, minMmi));
    }

    private void setMmiSeekInitialProgress() {
        float minMmi = mPreferences.getMinimumMmi();
        mMmiSeekBar.setProgress((int)(minMmi * SEEK_BAR_MULTIPLIER));
    }

    private void updateLastDaysCountLabel(int daysCount) {
        mLastDaysCountLabel.setText(mContext.getString(R.string.filter_label_last_days_count, daysCount));
    }

    private void setLastDaysCountSeekInitialProgress() {
        int lastDaysCount = mPreferences.getDisplayLastDaysCount();
        mLastDaysCountSeekBar.setProgress(lastDaysCount);
    }
}
