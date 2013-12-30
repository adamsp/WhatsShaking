package nz.net.speakman.android.whatsshaking.network.earthquakeretrieval;

import android.content.Context;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.util.List;

/**
 * Created by Adam on 28/12/13.
 */
public interface EarthquakeRetrieval {
    public List<Earthquake> getEarthquakes(Context ctx);
}
