package nz.net.speakman.android.whatsshaking.fragments;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.activities.MainActivity;
import nz.net.speakman.android.whatsshaking.adapters.EarthquakeListAdapter;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.db.EarthquakeDBLoader;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import uk.co.senab.actionbarpulltorefresh.extras.actionbarcompat.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import java.util.List;

/**
 * Created by Adam on 29/12/13.
 */
public class EarthquakeListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Earthquake>>, OnRefreshListener {

    private DBHelper mDBHelper;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hideRefreshUI();
            if (intent.getAction().equals(Earthquake.DATA_UPDATED)) {
                updateAdapter(true);
            }
        }
    };
    private LocalBroadcastManager mBroadcastMgr;
    private EarthquakeListAdapter mAdapter;
    private PullToRefreshLayout mPullToRefreshLayout;

    public static EarthquakeListFragment newInstance() {
        return new EarthquakeListFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);

        // As per docs https://github.com/chrisbanes/ActionBar-PullToRefresh/wiki/ListFragment

        // This is the View which is created by ListFragment
        ViewGroup viewGroup = (ViewGroup) view;

        // We need to create a PullToRefreshLayout manually
        mPullToRefreshLayout = new PullToRefreshLayout(viewGroup.getContext());

        // We can now setup the PullToRefreshLayout
        ActionBarPullToRefresh.from(getActivity())

                // We need to insert the PullToRefreshLayout into the Fragment's ViewGroup
                .insertLayoutInto(viewGroup)

                // We need to mark the ListView and it's Empty View as pullable
                // This is because they are not dirent children of the ViewGroup
                .theseChildrenArePullable(getListView(), getListView().getEmptyView())

                // We can now complete the setup as desired
                .listener(this)
                .setup(mPullToRefreshLayout);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDBHelper = DBHelper.getInstance(getActivity());
        mBroadcastMgr = LocalBroadcastManager.getInstance(getActivity());
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_UPDATED));
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_RETRIEVAL_FAILED));
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
        activity.getSupportLoaderManager().destroyLoader(listLoader.getId());
        if (mAdapter == null) return;
        mAdapter.setEarthquakes(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> listLoader) {

    }

    @Override
    public void onRefreshStarted(View view) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof MainActivity) {
            ((MainActivity)activity).retrieveNewEarthquakes();
        }
    }

    private void hideRefreshUI() {
        if (mPullToRefreshLayout != null) {
            mPullToRefreshLayout.setRefreshComplete();
        }
    }
}
