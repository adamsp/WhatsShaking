package nz.net.speakman.android.whatsshaking.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import nz.net.speakman.android.whatsshaking.R;
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
    public EarthquakeListCursorAdapter(Context context, Cursor c) {
        super(context, layout, c, from, to, flags);
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
        return v;
    }

    private String formatDate(DateTime date) {
        return DateTimeFormat.shortDateTime().print(date);
    }
}
