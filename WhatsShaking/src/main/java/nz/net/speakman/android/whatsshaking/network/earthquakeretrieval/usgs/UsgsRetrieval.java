package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.RequestFuture;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.EarthquakeRetrieval;
import nz.net.speakman.android.whatsshaking.network.volley.VolleyProvider;

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
        // TODO Update this to return an endpoint relative to last checked time
        return "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson";
    }
}
