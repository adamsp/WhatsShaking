package nz.net.speakman.android.whatsshaking.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.adapters.EarthquakeListAdapter;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.EarthquakeRetrieval;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs.UsgsRetrieval;

import java.util.List;

/**
 * Created by Adam on 29/12/13.
 */
public class EarthquakeListFragment extends Fragment {

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_earthquake_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EarthquakeRetrieval earthquakeRetrieval = new UsgsRetrieval();
        earthquakeRetrieval.getEarthquakes(getActivity(), new Response.Listener<List<Earthquake>>() {
                    @Override
                    public void onResponse(List<Earthquake> earthquakes) {
                        View view = getView();
                        Context context = getActivity();
                        if (view != null && context != null) {
                            ListView list = (ListView)view.findViewById(R.id.earthquake_list);
                            list.setAdapter(new EarthquakeListAdapter(context, earthquakes));
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "Something went wrong, oops.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
