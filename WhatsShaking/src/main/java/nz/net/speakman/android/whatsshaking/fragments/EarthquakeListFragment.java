package nz.net.speakman.android.whatsshaking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import nz.net.speakman.android.whatsshaking.adapters.EarthquakeListAdapter;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.db.EarthquakeDBLoader;
import nz.net.speakman.android.whatsshaking.model.Earthquake;

import java.util.List;

/**
 * Created by Adam on 29/12/13.
 */
public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Earthquake>> {

    private DBHelper mDBHelper;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateAdapter(true);
        }
    };
    private LocalBroadcastManager mBroadcastMgr;
    private EarthquakeListAdapter mAdapter;

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDBHelper = DBHelper.getInstance(getActivity());
        mBroadcastMgr = LocalBroadcastManager.getInstance(getActivity());
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_UPDATED));
        updateAdapter(false);
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

    private void updateAdapter(boolean forceNewLoader) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        if (mAdapter == null) {
            mAdapter = new EarthquakeListAdapter(activity);
            setListAdapter(mAdapter);
        }
        if (forceNewLoader) {
            // Force restart here; we may have filters applied before the previous query returns.
            activity.getSupportLoaderManager().restartLoader(Earthquake.LOADER_DB, null, this);
        } else {
            // Just reconnect to existing one, if one exists.
            activity.getSupportLoaderManager().initLoader(Earthquake.LOADER_DB, null, this);
        }

    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        EarthquakeDBLoader earthquakeDBLoader = new EarthquakeDBLoader(getActivity());
        earthquakeDBLoader.forceLoad();
        return earthquakeDBLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> listLoader, List<Earthquake> earthquakes) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        activity.getSupportLoaderManager().destroyLoader(Earthquake.LOADER_DB);
        if (mAdapter == null) return;
        mAdapter.setEarthquakes(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> listLoader) {

    }
}
