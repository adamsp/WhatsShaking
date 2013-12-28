package nz.net.speakman.android.whatsshaking.network.volley;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Adam on 28/12/13.
 */
public class VolleyProvider {
    private static RequestQueue queue = null;

    private VolleyProvider() {
    }

    public static synchronized RequestQueue getQueue(Context ctx) {
        if (queue == null) {
            queue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return queue;
    }
}
