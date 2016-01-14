package hmdugin.acheibateria.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.R;
import hmdugin.acheibateria.activities.NotificationActivity;
import hmdugin.acheibateria.eventBus.MessageEB;
import hmdugin.acheibateria.util.Configuration;
import hmdugin.acheibateria.util.GoogleAPIConnectionUtil;
import hmdugin.acheibateria.util.MathUtil;
import hmdugin.acheibateria.util.NotificationUtils;
import hmdugin.acheibateria.util.PrefManager;

/**
 * Created by Rodrigo on 06/01/2016.
 */
public class LocationService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    GoogleAPIConnectionUtil googleAPIConnectionUtil;
    Location location;
    PrefManager prefManager;
    NotificationUtils notificationUtils;
    GoogleApiClient mGoogleApiClient;
    public LocationService() {

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        prefManager = new PrefManager(getApplicationContext());
        Log.println(Log.ASSERT, TAG, "onCreate");

        googleAPIConnectionUtil = new GoogleAPIConnectionUtil(getApplicationContext());
        GoogleAPIConnectionUtil.setNomeDaClasse(TAG);
        mGoogleApiClient = googleAPIConnectionUtil.getmGoogleApiClient();
        mGoogleApiClient.connect();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.println(Log.ASSERT, TAG, "onStartCommand");


        Log.println(Log.ASSERT, TAG, "isConnecting?= " + mGoogleApiClient.isConnected());


        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onEvent(MessageEB event) {
        Log.d(TAG, "onEvent= " + event.getData());
        if (event.getData().equals(TAG)) {
            location = event.getLocation();
            mGoogleApiClient.disconnect();


            Log.println(Log.ASSERT, TAG, "Data e hora do BatteryLevelReceiver: " + prefManager.getDataEHora());
            carregou();
            stopSelf();

        }
    }

    private void showNotificationMessage(Context context) {
        String title = "Carregando";
        String message = "Obrigado por utilizar nossos serviços!";
        Intent intent = new Intent(context, NotificationActivity.class);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        String[] msg = notificationUtils.msgCarregouLoja();
        notificationUtils.showNotificationMessage(msg[0], msg[1], intent, R.drawable.ic_baterry_charged, R.drawable.ic_stat_baterry_charged, Configuration.NOTIFICATION_CHARGING_ID);
    }


    private void carregou() {

        String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());
        boolean intervaloCerto = MathUtil.calcIntTempo(prefManager.getDataEHora(), currentDateandTime);

        Set<String> coord = prefManager.getCoord();
        Log.println(Log.ASSERT, TAG, "minha localização - lat: " + location.getLatitude() + "lon: " + location.getLongitude());
        for (String s : coord) {
            String lat = s.substring(0, s.lastIndexOf("_"));
            String lon = s.substring(s.lastIndexOf("_") + 1);
            Location locationLojas = new Location("");
            locationLojas.setLatitude(Double.parseDouble(lat));
            locationLojas.setLongitude(Double.parseDouble(lon));

            Log.println(Log.ASSERT, TAG, "lat: " + lat + " lon: " + lon);
            Log.println(Log.ASSERT, TAG, "distancia: " + location.distanceTo(locationLojas));

            if (location.distanceTo(locationLojas) < 25)
                if (intervaloCerto) {
                    ParseAnalytics.trackEventInBackground("NotificationCarregadoClicado");
                    showNotificationMessage(getApplicationContext());
                    prefManager.apagar();
                    break;
                }


        }

    }


}

