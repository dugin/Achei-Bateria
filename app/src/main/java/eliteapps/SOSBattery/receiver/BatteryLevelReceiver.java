package eliteapps.SOSBattery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parse.ParseAnalytics;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.MainActivity;
import eliteapps.SOSBattery.util.Configuration;
import eliteapps.SOSBattery.util.NotificationUtils;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    NotificationUtils notificationUtils;

    @Override
    public void onReceive(Context context, Intent intent) {

        ParseAnalytics.trackEventInBackground("Sem_Bateria");
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
