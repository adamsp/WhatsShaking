package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.RequestFuture;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.EarthquakeRetrieval;
import nz.net.speakman.android.whatsshaking.network.volley.VolleyProvider;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Adam on 28/12/13.
 * <p/>
 * Retrieves Earthquakes from the USGS GeoJSON earthquake feed.
 * <p/>
 * http://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php
 */
public class UsgsRetrieval implements EarthquakeRetrieval {

    private static final String USGS_ENDPOINT_HOUR = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_hour.geojson";
    private static final String USGS_ENDPOINT_DAY = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";
    private static final String USGS_ENDPOINT_WEEK = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.geojson";
    private static final String USGS_ENDPOINT_MONTH = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_month.geojson";


    @Override
    public List<Earthquake> getEarthquakes(Context ctx) {
        RequestFuture<List<Earthquake>> future = RequestFuture.newFuture();
        String url = getEndpoint(ctx);
        RequestQueue queue = VolleyProvider.getQueue(ctx);
        queue.add(new UsgsJsonRequest(url, future, future));
        try {
            return future.get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Returns an endpoint for retrieving earthquakes from USGS. Will differ depending on the last time we checked for
     * quakes. For example, if we last checked 3 days ago, this will return the "last 7 days" endpoint. If we last
     * checked 6 hours ago, this will return the "last 24 hours endpoint". The brackets are 1 hour and 1, 7, 30 days.
     */
    private String getEndpoint(Context ctx) {
        DateTime lastCheckedTime = new Preferences(ctx).getLastCheckedDate();
        DateTime currentTime = new DateTime();
        String endpoint;
        // Our 'last checked' time is in the future - User must have changed their device time. Check 1 day.
        if (currentTime.isBefore(lastCheckedTime)) {
            endpoint = USGS_ENDPOINT_DAY;
        }
        // Is our last check > 1 week ago?
        else if (lastCheckedTime.isBefore(currentTime.minusDays(7))) {
            endpoint = USGS_ENDPOINT_MONTH;
        }
        // Is our last check > 1 day ago?
        else if (lastCheckedTime.isBefore(currentTime.minusDays(1))) {
            endpoint = USGS_ENDPOINT_WEEK;
        }
        // Is our last check > 1 hour ago?
        else if (lastCheckedTime.isBefore(currentTime.minusHours(1))) {
            endpoint = USGS_ENDPOINT_DAY;
        }
        // If none of the above, we've checked sometime in the last hour.
        else {
            endpoint = USGS_ENDPOINT_HOUR;
        }
        return endpoint;
    }
}
