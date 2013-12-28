package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonRequest;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.util.List;

/**
 * Created by Adam on 28/12/13.
 */
public class UsgsJsonRequest extends JsonRequest<List<Earthquake>> {
    public UsgsJsonRequest(String url, Response.Listener<List<Earthquake>> listener, Response.ErrorListener errorListener) {
        super(Method.GET, url, "", listener, errorListener);
    }

    @Override
    protected Response<List<Earthquake>> parseNetworkResponse(NetworkResponse networkResponse) {
        // TODO Parse the JSON into a list of Earthquake objects.
        return null;
    }
}
