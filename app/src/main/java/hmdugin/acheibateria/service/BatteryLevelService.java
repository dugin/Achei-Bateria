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
import java.util.Random;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.activities.MainActivity;
import hmdugin.acheibateria.eventBus.MessageEB;
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

            prefManager = new PrefManager(arg0);

            String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());

            if (batteryStatus != BatteryManager.BATTERY_STATUS_CHARGING) {
                if (prefManager.getPrimeiraVez()) {
                    prefManager.setPrimeiraVez(false);
                } else {

                    showNotificationMessage(arg0, "Bateria Acabando", "Encontre locais para carregá-la!", new Intent(arg0, MainActivity.class));

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
        if (intent.getExtras() != null) {
            Log.d(TAG, "" + intent.getExtras().getString("id"));
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    private String msgNotification() {
        String msg1 = "Corre! A BATERIA do seu celular esta morrendo! Clique aqui para acharmos um lugar para você salva-la :)";
        String msg2 = " Nossos videntes previram que a sua bateria morrerá... Clique aqui para consultar aonde você pode prevenir a morte dela!";
        String msg3 = "Clique aqui para achar um local perto de você, para carregar a sua bateria!";
        Random gerador = new Random();
        int n = gerador.nextInt(3);
        switch (n) {
            case 0:
                return msg1;
            case 1:
                return msg2;
            case 2:
                return msg3;
        }
        return "Erro no random";
    }

    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);

    }


}
