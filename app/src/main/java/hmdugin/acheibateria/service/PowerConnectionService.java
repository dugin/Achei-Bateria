package hmdugin.acheibateria.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

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
                boolean intervaloCerto = calcIntTempo(prefManager.getDataEHora(), currentDateandTime);

                //  prefManager.apagar();

                if (intervaloCerto)
                    showNotificationMessage(arg0);
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
        // this.registerReceiver(this.PowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_DISCONNECTED));
    }


    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }


    private boolean calcIntTempo(String data, String data1) {
        Log.d(TAG, "Valor1 = " + Integer.parseInt(data.substring(12)) + " Valor2 = " + Integer.parseInt(data1.substring(12)));
        int dia = Integer.parseInt(data.substring(0, 1));
        int dia1 = Integer.parseInt(data1.substring(0, 1));
        int hr = Integer.parseInt(data.substring(9, 10));
        int hr1 = Integer.parseInt(data1.substring(9, 10));
        int min = Integer.parseInt(data.substring(12));
        int min1 = Integer.parseInt(data1.substring(12));
        int tempo, tempo1;
        tempo = hr * 60 * min;
        if (dia == dia1)
            tempo1 = hr1 * 60 * min1;
        else
            tempo1 = 24 * hr1 * 60 * min1;

        Log.d(TAG, "tempo1 = " + tempo + " tempo2 = " + tempo1);
        return Math.abs(tempo1 - tempo) < 20;

    }

    private void showNotificationMessage(Context context) {
        String title = "Carregando";
        String message = "Obrigado por utilizar nossos serviços!";
        Intent intent = new Intent(context, MainActivity.class);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, R.drawable.baterry_charged, Configuration.NOTIFICATION_CHARGING_ID);
    }


}
