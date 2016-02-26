package eliteapps.SOSBattery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.MainActivity;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.util.Configuration;
import eliteapps.SOSBattery.util.NotificationUtils;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    NotificationUtils notificationUtils;
    Tracker mTracker;

    @Override
    public void onReceive(Context context, Intent intent) {

        App application = (App) context.getApplicationContext();
        mTracker = application.getDefaultTracker();

        // Build and send an Event.
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Core")
                .setAction("Low Battery phone")
                .setLabel("Low Battery")
                .build());



        showNotificationMessage(context);

    }


    private void showNotificationMessage(Context context) {


        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("from_notification_low_battery", true);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        String[] msg = notificationUtils.msgBateriaMorrendo();

        notificationUtils.showNotificationMessage(msg[0], msg[1], intent, R.drawable.ic_baterry_low, R.drawable.ic_stat_baterry_low, Configuration.NOTIFICATION_LOW_BATTERY_ID);

    }
}
