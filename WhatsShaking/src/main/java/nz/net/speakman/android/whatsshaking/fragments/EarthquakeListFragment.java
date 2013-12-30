package nz.net.speakman.android.whatsshaking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.j256.ormlite.dao.Dao;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.adapters.EarthquakeListAdapter;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.EarthquakeRetrieval;
import nz.net.speakman.android.whatsshaking.network.earthquakeretrieval.usgs.UsgsRetrieval;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 29/12/13.
 */
public class EarthquakeListFragment extends ListFragment {

    private DBHelper mDBHelper;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAdapter();
        }
    };
    private LocalBroadcastManager mBroadcastMgr;

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDBHelper = DBHelper.getInstance(getActivity());
        mBroadcastMgr = LocalBroadcastManager.getInstance(getActivity());
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_UPDATED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastMgr != null) {
            mBroadcastMgr.unregisterReceiver(mBroadcastReceiver);
        }
        if (mDBHelper != null) {
            mDBHelper = null;
            DBHelper.releaseHelper();
        }
    }

    private void updateAdapter() {
        Context ctx = getActivity();
        if (ctx == null) return;
        // TODO This is innefficient, creating a new adapter each time. Also loading *all* items at once, on UI thread.
        DBHelper helper = DBHelper.getInstance(ctx);
        try {
            Dao<Earthquake,Integer> dao = Earthquake.getDao(helper.getConnectionSource());
            List<Earthquake> earthquakes = dao.queryBuilder().orderBy("eventTime", false).query();
            setListAdapter(new EarthquakeListAdapter(getActivity(), earthquakes));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBHelper.releaseHelper();;
        }
    }
}
