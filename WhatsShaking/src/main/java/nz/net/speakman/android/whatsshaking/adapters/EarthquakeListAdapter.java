package nz.net.speakman.android.whatsshaking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 29/12/13.
 */
public class EarthquakeListAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Earthquake> mEarthquakes;

    public EarthquakeListAdapter(Context ctx) {
        mContext = ctx;
    }

    public void setEarthquakes(List<Earthquake> earthquakes) {
        mEarthquakes = earthquakes;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mEarthquakes == null ? 0 : mEarthquakes.size();
    }

    @Override
    public Earthquake getItem(int position) {
        return mEarthquakes == null ? null : mEarthquakes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mEarthquakes == null ? 0 : mEarthquakes.get(position).getPrimaryId().hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Earthquake earthquake = getItem(position);
        if (earthquake == null) {
            return null;
        }

        EarthquakeViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_earthquake_row_item, parent, false);
            viewHolder = new EarthquakeViewHolder();
            viewHolder.place = (TextView) convertView.findViewById(R.id.earthquake_list_row_detail_place);
            viewHolder.magnitude = (TextView) convertView.findViewById(R.id.earthquake_list_row_detail_magnitude);
            viewHolder.eventTime = (TextView) convertView.findViewById(R.id.earthquake_list_row_detail_event_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EarthquakeViewHolder) convertView.getTag();
        }

        viewHolder.magnitude.setText(String.valueOf(earthquake.getMagnitude()));
        viewHolder.place.setText(earthquake.getPlace());
        DateFormat df = android.text.format.DateFormat.getMediumDateFormat(mContext);
        DateFormat tf = android.text.format.DateFormat.getTimeFormat(mContext);
        Date date = new Date(earthquake.getEventTime());
        viewHolder.eventTime.setText(df.format(date) + " " + tf.format(date));

        return convertView;
    }


    private static class EarthquakeViewHolder {
        public TextView place;
        public TextView magnitude;
        public TextView eventTime;
    }
}
