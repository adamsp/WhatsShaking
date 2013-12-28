package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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


        List<Earthquake> quakes = new ArrayList<Earthquake>();
        try {
            String json = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
            Object nextVal = new JSONTokener(json).nextValue();
            if (JSONObject.NULL.equals(nextVal)) return null;

            JSONObject o = (JSONObject) nextVal;
            JSONArray features = o.getJSONArray("features");
            // Iterate backwards over the quakes - newest is at the top, we want newest at the bottom (as that's how we're storing them).
            for (int i = features.length() - 1; i >= 0; i--) {
                try {
                    o = (JSONObject) features.get(i);
                    Earthquake q = getQuakeFromJSON(o);
                    quakes.add(q);
                } catch (JSONException e) {
                    continue;
                }
            }
        } catch (JSONException e) {
            return null;
        } catch (ClassCastException e) {
            return null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        Response<List<Earthquake>> result = Response.success(quakes, HttpHeaderParser.parseCacheHeaders(networkResponse));
        return result;
    }

    private Earthquake getQuakeFromJSON(JSONObject jsonQuake) throws JSONException {
        /*
         We can't use Gson (or another similar automatic tool) because a) it's potentially quite slow during periods
         of heavy seismic activity - ie, a large number of quakes could take a while to parse using reflection - and
         b) the USGS JSON key names are rather short/non-descriptive in some cases, would like to map to another name.
        */

        Earthquake earthquake = new Earthquake();
        earthquake.setPrimaryId(jsonQuake.getString("id"));

        // TODO Potentially opt doubles into negative values instead of NaN, depending on how SQLite handles them...
        JSONObject properties = jsonQuake.getJSONObject("properties");
        earthquake.setAllPotentialPrimaryIds(properties.optString("ids"));
        earthquake.setMagnitude(properties.optDouble("mag"));
        earthquake.setPlace(properties.optString("place"));
        earthquake.setEventTime(properties.optLong("time"));
        earthquake.setUpdatedTime(properties.optLong("updated"));
        earthquake.setTimeZoneOffsetMinutes(properties.optInt("tz"));
        earthquake.setUrl(properties.optString("url"));
        earthquake.setDetailDataUrl(properties.optString("detail"));
        earthquake.setFeltCount(properties.optInt("felt"));
        earthquake.setReportedIntensity(properties.optDouble("cdi"));
        earthquake.setCalculatedIntensity(properties.optDouble("mmi"));
        earthquake.setAlertLevel(properties.optString("alert", Earthquake.ALERT_LEVEL_NONE));
        earthquake.setStatus(properties.optString("status", Earthquake.STATUS_UNKNOWN));
        earthquake.setPossibleTsunami(properties.optInt("tsunami", Earthquake.TSUNAMI_UNLIKELY));
        earthquake.setSignificance(properties.optInt("sig"));
        earthquake.setPrimaryReportingNetwork(properties.optString("net"));
        earthquake.setPrimaryReportingNetworkEventId(properties.optString("code"));
        earthquake.setSources(properties.optString("sources"));
        earthquake.setTypes(properties.optString("types"));
        earthquake.setNumberOfReportingStations(properties.optInt("nst"));
        earthquake.setNearestReportingStationDegrees(properties.optDouble("dmin"));
        earthquake.setRms(properties.optDouble("rms"));
        earthquake.setGapBetweenAdjacentStationsDegrees(properties.optDouble("gap"));
        earthquake.setMagnitudeType(properties.optString("magType"));
        earthquake.setEventType(properties.optString("type", Earthquake.EVENT_TYPE_UNKNOWN));

        JSONArray coordinates = jsonQuake.getJSONObject("geometry").getJSONArray("coordinates");
        earthquake.setLongitude(coordinates.getDouble(0));
        earthquake.setLatitude(coordinates.getDouble(1));
        earthquake.setDepth(coordinates.getDouble(2));

        return earthquake;
    }
}
