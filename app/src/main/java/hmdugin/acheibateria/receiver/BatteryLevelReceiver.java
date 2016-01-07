package hmdugin.acheibateria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.activities.MainActivity;
import hmdugin.acheibateria.util.Configuration;
import hmdugin.acheibateria.util.NotificationUtils;

/**
 * Created by Rodrigo on 06/01/2016.
 */
public class BatteryLevelReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    NotificationUtils notificationUtils;

    @Override
    public void onReceive(Context context, Intent intent) {


        showNotificationMessage(context);
    }


    private void showNotificationMessage(Context context) {
        String title = "Bateria Acabando";
        String message = "Encontre locais para carregá-la!";
        Intent intent = new Intent(context, MainActivity.class);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, R.drawable.baterry_low, R.drawable.baterry_low, Configuration.NOTIFICATION_LOW_BATTERY_ID);

    }
}
