package eliteapps.SOSBattery.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.fragment.CarregueiFragment;

public class NotificationActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();

        if (getIntent() != null)
            if (getIntent().getExtras() != null)
                if (getIntent().getExtras().getBoolean("from_notification_charging")) {

                    // Build and send an Event.
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Notification")
                            .setAction("Charging Notification")
                            .setLabel("Charging Notification")
                            .build());


                }

        getFragmentManager().beginTransaction()
                .add(R.id.notification_layout, new CarregueiFragment())
                .addToBackStack(null)
                .commit();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case R.id.homeAsUp:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case android.R.id.home:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

}
