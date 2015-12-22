package hmdugin.acheibateria.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.R;
import hmdugin.acheibateria.activities.MainActivity;
import hmdugin.acheibateria.eventBus.MessageEB;
import hmdugin.acheibateria.util.Configuration;
import hmdugin.acheibateria.util.NotificationUtils;
import hmdugin.acheibateria.util.PrefManager;

/**
 * Created by Rodrigo on 16/12/2015.
 */
public class BatteryLevelService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    NotificationUtils notificationUtils;
    PrefManager prefManager;


    int batteryStatus;
    public BroadcastReceiver BatteryLevelReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Log.d(TAG, "" + batteryStatus);
            prefManager = new PrefManager(arg0);

            String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());

            if (batteryStatus != BatteryManager.BATTERY_STATUS_CHARGING) {
                if (prefManager.getPrimeiraVez()) {
                    prefManager.setPrimeiraVez(false);
                } else {

                    ParseAnalytics.trackEventInBackground("NotificationLowBatteryClicado");
                    showNotificationMessage(arg0);

                    prefManager.setPrimeiraVez(false);

                    prefManager.pegaDataEHora(currentDateandTime);

                    MessageEB m = new MessageEB(TAG);

                    EventBus.getDefault().post(m);

                    unregisterReceiver(BatteryLevelReceiver);
                    stopSelf();
                }


            }
        }

    };

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        this.registerReceiver(this.BatteryLevelReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            if (intent.getExtras() != null)
            Log.d(TAG, "" + intent.getExtras().getString("id"));

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
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
