package nz.net.speakman.android.whatsshaking.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import nz.net.speakman.android.whatsshaking.R;
import nz.net.speakman.android.whatsshaking.db.DBHelper;
import nz.net.speakman.android.whatsshaking.db.EarthquakeDbContract;
import nz.net.speakman.android.whatsshaking.model.Earthquake;
import org.joda.time.format.DateTimeFormat;

import java.sql.SQLException;

/**
 * Created by Adam on 5/01/14.
 */
public class EarthquakeDetailActivity extends ActionBarActivity {

    public static final String EXTRA_QUAKE_ID = "earthquakeId";

    public static void navigateTo(Context ctx, String earthquakeId) {
        Intent intent = new Intent(ctx, EarthquakeDetailActivity.class);
        intent.putExtra(EarthquakeDetailActivity.EXTRA_QUAKE_ID, earthquakeId);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String quakeId = getIntent().getStringExtra(EXTRA_QUAKE_ID);
        if (quakeId == null || quakeId.length() == 0) {
            finish();
            return;
        }

        DBHelper dbHelper = DBHelper.getInstance(this);
        try {
            Dao<Earthquake, Integer> dao = Earthquake.getDao(dbHelper.getConnectionSource());
            PreparedQuery<Earthquake> preparedQuery = dao.queryBuilder().where().eq(EarthquakeDbContract.Columns.PrimaryId, quakeId).prepare();
            Earthquake earthquake = dao.queryForFirst(preparedQuery);
            if (earthquake == null) {
                finish();
            }
            setContentView(R.layout.activity_earthquake_detail);
            displayEarthquake(earthquake);
        } catch (SQLException e) {
            e.printStackTrace();
            finish();
        } finally {
            DBHelper.releaseHelper();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // As per docs: http://developer.android.com/training/implementing-navigation/ancestral.html
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                                    // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayEarthquake(Earthquake earthquake) {
        TextView tv = (TextView) findViewById(R.id.earthquake_detail_magnitude);
        tv.setText(String.valueOf(earthquake.getMagnitude()));

        tv = (TextView) findViewById(R.id.earthquake_detail_depth);
        tv.setText(String.valueOf(earthquake.getDepth()));

        tv = (TextView) findViewById(R.id.earthquake_detail_event_time);
        tv.setText(DateTimeFormat.longDateTime().print(earthquake.getEventTime()));

        tv = (TextView) findViewById(R.id.earthquake_detail_place);
        tv.setText(earthquake.getPlace());
    }
}
