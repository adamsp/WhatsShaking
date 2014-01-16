package nz.net.speakman.android.whatsshaking.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.colors.ColorMapper;
import nz.net.speakman.android.whatsshaking.db.EarthquakeDbContract;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Created by Adam on 4/01/14.
 */
public class EarthquakeListCursorAdapter extends SimpleCursorAdapter {
    private static final int layout = R.layout.list_earthquake_row_item;
    private static String[] from = {
            EarthquakeDbContract.Columns.Place,
            EarthquakeDbContract.Columns.Magnitude
    };
    private static final int [] to = {
            R.id.earthquake_list_row_detail_place,
            R.id.earthquake_list_row_detail_magnitude
    };
    private static final int flags = 0;

    private final int mIntensityBarHeight;

    public EarthquakeListCursorAdapter(Context context, Cursor c) {
        super(context, layout, c, from, to, flags);
        Resources resources = context.getResources();
        mIntensityBarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.earthquake_list_row_detail_mmi_bar_height),
                resources.getDisplayMetrics());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        if (v == null) {
            return null;
        }

        Cursor cursor = (Cursor) getItem(position);
        long dateLong = cursor.getLong(cursor.getColumnIndex(EarthquakeDbContract.Columns.EventTime));
        DateTime eventTime = new DateTime(dateLong);
        ((TextView) v.findViewById(R.id.earthquake_list_row_detail_event_time)).setText(formatDate(eventTime));

        // Set up our intensity indicator bar background color
        float mmi = (float) cursor.getDouble(cursor.getColumnIndex(EarthquakeDbContract.Columns.CalculatedIntensity));
        View mmiBar = v.findViewById(R.id.earthquake_list_row_detail_mmi_bar);
        mmiBar.setBackgroundColor(ColorMapper.mmiColor(mmi));
        // ... and width (via weight).
        float weight = Math.min(1f, mmi/10.0f);
        mmiBar.setLayoutParams(new LinearLayout.LayoutParams(0, mIntensityBarHeight, weight));
        return v;
    }

    private String formatDate(DateTime date) {
        return DateTimeFormat.shortDateTime().print(date);
    }
}
