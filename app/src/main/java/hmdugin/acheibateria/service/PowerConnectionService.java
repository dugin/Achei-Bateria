package hmdugin.acheibateria.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.activities.MainActivity;
import hmdugin.acheibateria.eventBus.MessageEB;
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

            Log.d(TAG, "Data e hora passados pro PowerConnectionReceiver: " + prefManager.getDataEHora());

            String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());
            Log.d(TAG, "Data e hora do PowerConnectionReceiver: " + currentDateandTime);
            boolean intervaloCerto = calcIntTempo(prefManager.getDataEHora(), currentDateandTime);

            //  prefManager.apagar();

            if (intervaloCerto)
                showNotificationMessage(arg0, "Carregando", "Obrigado por utilizar nossos serviços!", new Intent(arg0, MainActivity.class));

            MessageEB m = new MessageEB(TAG);

            EventBus.getDefault().post(m);


            unregisterReceiver(PowerConnectionReceiver);
            stopSelf();

        }

    };
    int batteryStatus;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        this.registerReceiver(this.PowerConnectionReceiver, new IntentFilter(Intent.ACTION_POWER_CONNECTED));
    }


    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }


    private boolean calcIntTempo(String data, String data1) {
        Log.d(TAG, "Valor1 = " + Integer.parseInt(data.substring(12)) + " Valor2 = " + Integer.parseInt(data1.substring(12)));
        int min = Integer.parseInt(data.substring(12));
        int min1 = Integer.parseInt(data1.substring(12));
        int intervalo = Math.abs(min) - Math.abs(min1);

        return intervalo < 20;

    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);

    }


}
