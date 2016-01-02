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
import hmdugin.acheibateria.activities.NotificationActivity;
import hmdugin.acheibateria.eventBus.MessageEB;
import hmdugin.acheibateria.util.Configuration;
import hmdugin.acheibateria.util.MathUtil;
import hmdugin.acheibateria.util.NotificationUtils;
import hmdugin.acheibateria.util.PrefManager;

/**
 * Created by Rodrigo on 17/12/2015.
 */
public class PowerConnectionService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    NotificationUtils notificationUtils;
    PrefManager prefManager;
    public BroadcastReceiver PowerConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {

            prefManager = new PrefManager(arg0);

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            String tipo = intent.getAction();

            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            Log.d(TAG, "Tipo da bateria: " + tipo);

            Log.d(TAG, "Status da bateria: " + status);

            if (tipo.equals("android.intent.action.ACTION_POWER_CONNECTED")) {
                Log.d(TAG, "Data e hora passados pro PowerConnectionReceiver: " + prefManager.getDataEHora());

                String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());
                Log.d(TAG, "Data e hora do PowerConnectionReceiver: " + currentDateandTime);
                boolean intervaloCerto = MathUtil.calcIntTempo(prefManager.getDataEHora(), currentDateandTime);

                //  prefManager.apagar();

                if (intervaloCerto) {
                    ParseAnalytics.trackEventInBackground("NotificationCarregadoClicado");
                    showNotificationMessage(arg0);

                }
            } else if (tipo.equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                MessageEB m = new MessageEB(TAG);
                Log.d(TAG, "Entrou no Action Disconnected");
                EventBus.getDefault().post(m);
                unregisterReceiver(PowerConnectionReceiver);
                stopSelf();
            }

        }

    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(this.PowerConnectionReceiver, intentFilter);

    }


    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }


    private void showNotificationMessage(Context context) {
        String title = "Carregando";
        String message = "Obrigado por utilizar nossos serviços!";
        Intent intent = new Intent(context, NotificationActivity.class);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, R.drawable.baterry_charged, R.drawable.baterry_charged3, Configuration.NOTIFICATION_CHARGING_ID);
    }


}
