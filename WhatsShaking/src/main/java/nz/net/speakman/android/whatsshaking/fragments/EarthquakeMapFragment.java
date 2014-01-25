package nz.net.speakman.android.whatsshaking.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.dao.Dao;
import nz.net.speakman.android.whatsshaking.activities.EarthquakeDetailActivity;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;
import nz.net.speakman.android.whatsshaking.views.FiltersPopup;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam on 25/01/14.
 */
public class EarthquakeMapFragment extends SupportMapFragment implements LoaderManager.LoaderCallbacks<List<Earthquake>>, GoogleMap.OnMarkerClickListener {

    public static final long MAX_EARTHQUAKES_ON_MAP = 20L;

    private DBHelper mDBHelper;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FiltersPopup.FILTER_UPDATED_MAGNITUDE)
                    || action.equals(FiltersPopup.FILTER_UPDATED_MMI)
                    || action.equals(FiltersPopup.FILTER_UPDATED_DAYS_COUNT)
                    || action.equals(Earthquake.DATA_RETRIEVAL_SUCCESSFUL)) {
                updateEarthquakes(true);
            }
        }
    };

    private LocalBroadcastManager mBroadcastMgr;
    private Preferences mPreferences;
    private Map<String, String> mMarkerIdToEarthquakeId;

    public static EarthquakeMapFragment newInstance() {
        return new EarthquakeMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMarkerIdToEarthquakeId = new HashMap<String, String>();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPreferences = new Preferences(getActivity());
        mDBHelper = DBHelper.getInstance(getActivity());
        mBroadcastMgr = LocalBroadcastManager.getInstance(getActivity());

        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_RETRIEVAL_STARTED));

        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_RETRIEVAL_SUCCESSFUL));
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(Earthquake.DATA_RETRIEVAL_FAILED));

        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(FiltersPopup.FILTER_UPDATED_MAGNITUDE));
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(FiltersPopup.FILTER_UPDATED_MMI));
        mBroadcastMgr.registerReceiver(mBroadcastReceiver, new IntentFilter(FiltersPopup.FILTER_UPDATED_DAYS_COUNT));

        updateEarthquakes(false);
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

    private void updateEarthquakes(boolean forceNewLoader) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;

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
        Loader<List<Earthquake>> loader = new AsyncTaskLoader<List<Earthquake>>(getActivity()) {
            @Override
            public List<Earthquake> loadInBackground() {
                try {
                    Dao<Earthquake, Integer> earthquakeDao = Earthquake.getDao(mDBHelper.getConnectionSource());
                    return earthquakeDao.queryBuilder()
                            .limit(MAX_EARTHQUAKES_ON_MAP)
                            .where().raw(DBHelper.buildWhereClauseFromFilter(mPreferences))
                            .query();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> listLoader, List<Earthquake> earthquakes) {
        FragmentActivity activity = getActivity();
        if (activity == null) return;
        activity.getSupportLoaderManager().destroyLoader(listLoader.getId());
        displayEarthquakes(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> listLoader) {

    }

    private void displayEarthquakes(List<Earthquake> earthquakes) {
        GoogleMap map = getMap();
        if (map == null || earthquakes == null)
                return;

        mMarkerIdToEarthquakeId.clear();
        for (Earthquake q : earthquakes) {
            addEarthquakeMarker(map, q);
        }

        map.setOnMarkerClickListener(this);
    }

    private void addEarthquakeMarker(GoogleMap map, Earthquake q) {
        if (map == null || q == null) return;
        MarkerOptions markerOptions = getMarkerForEarthquake(q);
        Marker marker = map.addMarker(markerOptions);
        mMarkerIdToEarthquakeId.put(marker.getId(), q.getPrimaryId());
    }

    private MarkerOptions getMarkerForEarthquake(Earthquake q) {
        LatLng position = new LatLng(q.getLatitude(), q.getLongitude());
        MarkerOptions m = new MarkerOptions()
                .position(position);
        return m;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String quakeId = mMarkerIdToEarthquakeId.get(marker.getId());
        if (!TextUtils.isEmpty(quakeId)) {
            EarthquakeDetailActivity.navigateTo(getActivity(), quakeId);
            return true;
        } else {
            return false;
        }
    }
}

