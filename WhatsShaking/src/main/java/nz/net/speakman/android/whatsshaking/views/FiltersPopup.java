package nz.net.speakman.android.whatsshaking.views;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
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
    private final Preferences mPreferences;
    private TextView mMagnitudeLabel;
    private TextView mMmiLabel;
    private TextView mDateLabel;
    private SeekBar mMagnitudeSeekBar;
    private SeekBar mMmiSeekBar;
    // TODO Replace the datepicker with a number picker? Needs consideration. DatePicker is buggy with 1 month range, and range doesn't work below API 11 anyway.
    private DatePicker mDatePicker;

    /**
     * Because seek bars require an integer max/progress value, we multiply our floats to suit.
     */
    private static final float SEEK_BAR_MULTIPLIER = 10.0f;
    private static final int MAGNITUDE_MAX = (int)(6 * SEEK_BAR_MULTIPLIER);
    private static final int MMI_MAX = (int)(10 * SEEK_BAR_MULTIPLIER);

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
        }
    };

    private final DatePicker.OnDateChangedListener dateChangedListener = new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // We get supplied a month from 0-11; we require a month from 1-12 for JodaTime.
            DateTime newDisplayDate = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);
            mPreferences.setDisplaySinceDate(newDisplayDate);
            updateDateLabel(newDisplayDate);
        }
    };

    public FiltersPopup(Activity activity) {
        super(activity.getLayoutInflater().inflate(R.layout.popup_filter, null),
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPreferences = new Preferences(activity);
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

        mDateLabel = (TextView) v.findViewById(R.id.filter_label_date);
        mDatePicker = (DatePicker) v.findViewById(R.id.filter_date_picker);
        DateTime oneMonthAgo = DateTime.now().minusMonths(1);
        DateTime savedDate = mPreferences.getDisplaySinceDate();
        if (savedDate.isBefore(oneMonthAgo) || savedDate.isAfter(DateTime.now())) {
            mPreferences.setDisplaySinceDate(oneMonthAgo);
        }
        // TODO Min API 11 :(
//        mDatePicker.setMinDate(oneMonthAgo.getMillis());
//        mDatePicker.setMaxDate(System.currentTimeMillis());
        updateDateLabel(mPreferences.getDisplaySinceDate());
        setDatePickerInitialDate();
    }

    private void setupListeners() {
        mMagnitudeSeekBar.setOnSeekBarChangeListener(magnitudeChangedListener);
        mMmiSeekBar.setOnSeekBarChangeListener(mmiChangedListener);
    }

    private void updateMagnitudeLabel(float minMagnitude) {
        // TODO Make the display minimum magnitude label descriptive
        mMagnitudeLabel.setText(String.valueOf(minMagnitude));
    }

    private void setMagnitudeSeekInitialProgress() {
        float minMagnitude = mPreferences.getMinimumMagnitude();
        mMagnitudeSeekBar.setProgress((int)(minMagnitude * SEEK_BAR_MULTIPLIER));
    }

    private void updateMmiLabel(float minMmi) {
        // TODO Make the display minimum mmi label descriptive
        mMmiLabel.setText(String.valueOf(minMmi));
    }

    private void setMmiSeekInitialProgress() {
        float minMmi = mPreferences.getMinimumMmi();
        mMmiSeekBar.setProgress((int)(minMmi * SEEK_BAR_MULTIPLIER));
    }

    private void updateDateLabel(DateTime displaySince) {
        // TODO Make the display since date label descriptive
        mDateLabel.setText(DateTimeFormat.shortDate().print(displaySince));
    }

    private void setDatePickerInitialDate() {
        DateTime displaySince = mPreferences.getDisplaySinceDate();
        // We require months 0-11; JodaTime provides 1-12.
        int month = displaySince.getMonthOfYear() - 1;
        mDatePicker.init(displaySince.getYear(), month,
                displaySince.getDayOfMonth(), dateChangedListener);
    }
}