package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval;

import android.content.Context;
import com.android.volley.Response;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.util.List;

/**
 * Created by Adam on 28/12/13.
 */
public interface EarthquakeRetrieval {
    public void getEarthquakes(Context ctx, Response.Listener<List<Earthquake>> listener, Response.ErrorListener errorListener);
}
