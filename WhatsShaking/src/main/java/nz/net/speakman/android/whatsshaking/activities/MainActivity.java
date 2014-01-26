package nz.net.speakman.android.whatsshaking.activities;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.fragments.EarthquakeListFragment;
import nz.net.speakman.android.whatsshaking.fragments.EarthquakeMapFragment;
import nz.net.speakman.android.whatsshaking.loaders.LoaderIds;
import nz.net.speakman.android.whatsshaking.loaders.NetworkLoader;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import nz.net.speakman.android.whatsshaking.preferences.Preferences;
import nz.net.speakman.android.whatsshaking.views.FiltersPopup;
import org.joda.time.DateTime;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener,
        LoaderManager.LoaderCallbacks<Boolean> {

    private static final String FRAGMENT_TAG_EARTHQUAKE_LIST = "earthquakeList";
    private static final String FRAGMENT_TAG_EARTHQUAKE_MAP = "earthquakeMap";

    private static final int NAV_POSITION_LIST = 0;
    private static final int NAV_POSITION_MAP = 1;
    private static final int NAV_POSITION_GRAPH = 2;

    private int mCurrentlySelectedNavigationIndex;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private PopupWindow mFiltersPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        getResources().getStringArray(R.array.page_titles)),
                this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EarthquakeListFragment.newInstance(), FRAGMENT_TAG_EARTHQUAKE_LIST)
                    .commit();
            mCurrentlySelectedNavigationIndex = NAV_POSITION_LIST;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DateTime lastCheckedTime = new Preferences(this).getLastCheckedDate();
        DateTime currentTime = new DateTime();
        // Was the last time we checked for quakes greater than 1 hour ago?
        if (lastCheckedTime.isBefore(currentTime.minusHours(1))) {
            retrieveNewEarthquakes();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Crouton.cancelAllCroutons();
        if (mFiltersPopup != null) {
            // Dismiss popup window so we don't leak it.
            mFiltersPopup.dismiss();
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            mCurrentlySelectedNavigationIndex = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
            getSupportActionBar().setSelectedNavigationItem(mCurrentlySelectedNavigationIndex);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                mCurrentlySelectedNavigationIndex);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_filter:
                showFiltersPopup();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // If the user has re-selected a position (or we're updating it to suit at re-entry)
        // then we don't want to recreate the fragment.
        if (position == mCurrentlySelectedNavigationIndex) {
            return true;
        }
        mCurrentlySelectedNavigationIndex = position;
        switch(position) {
            case NAV_POSITION_LIST:
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, EarthquakeListFragment.newInstance(), FRAGMENT_TAG_EARTHQUAKE_LIST)
                    .commit();
                break;
            case NAV_POSITION_MAP:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, EarthquakeMapFragment.newInstance(), FRAGMENT_TAG_EARTHQUAKE_MAP)
                        .commit();
                break;
            case NAV_POSITION_GRAPH:
            default:
                break;
        }

        return true;
    }

    public void onEarthquakeClick(String earthquakeId) {
        // Handle click here so we can do different things depending on layout.
        EarthquakeDetailActivity.navigateTo(this, earthquakeId);
    }

    private void showFiltersPopup() {
        // Lazy load the popup window; user may never open it.
        if (mFiltersPopup == null) {
            // TODO Hook up some callbacks into this so we can update display when values change.
            mFiltersPopup = new FiltersPopup(this);
        }
        View view = findViewById(R.id.action_filter);
        if (view == null) return;
        mFiltersPopup.showAsDropDown(view, 0, -10);
    }

    public void retrieveNewEarthquakes() {
        getSupportLoaderManager().initLoader(LoaderIds.LOADER_NETWORK, null, this);
    }

    @Override
    public Loader<Boolean> onCreateLoader(int i, Bundle bundle) {
        NetworkLoader loader = new NetworkLoader(getApplicationContext());
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Boolean> booleanLoader, Boolean success) {
        getSupportLoaderManager().destroyLoader(booleanLoader.getId());
        if (!success) {
            Crouton.makeText(this, R.string.toast_check_connectivity, Style.ALERT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> booleanLoader) {

    }
}
