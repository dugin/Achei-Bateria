package hmdugin.acheibateria;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.parse.ParseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import hmdugin.acheibateria.activities.NotificationActivity;
import hmdugin.acheibateria.util.Configuration;
import hmdugin.acheibateria.util.GoogleAPIConnectionUtil;
import hmdugin.acheibateria.util.MathUtil;
import hmdugin.acheibateria.util.NotificationUtils;
import hmdugin.acheibateria.util.PrefManager;

/**
 * Created by Rodrigo on 06/01/2016.
 */
public class LocationService extends IntentService {

    private final String TAG = this.getClass().getSimpleName();
    GoogleAPIConnectionUtil googleAPIConnectionUtil;
    Location location;
    PrefManager prefManager;
    NotificationUtils notificationUtils;

    public LocationService() {
        super("LocationService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        prefManager = new PrefManager(getApplicationContext());

        Log.println(Log.ASSERT, TAG, "onHandleIntent");
        googleAPIConnectionUtil = new GoogleAPIConnectionUtil(getApplicationContext());
        googleAPIConnectionUtil.getmGoogleApiClient().connect();

        while (googleAPIConnectionUtil.minhaLocalizacao() == null) {

        }
        location = googleAPIConnectionUtil.minhaLocalizacao();
        googleAPIConnectionUtil.getmGoogleApiClient().disconnect();


        Log.d(TAG, "Data e hora do BatteryLevelReceiver: " + prefManager.getDataEHora());
        carregou();


    }

    private void showNotificationMessage(Context context) {
        String title = "Carregando";
        String message = "Obrigado por utilizar nossos serviços!";
        Intent intent = new Intent(context, NotificationActivity.class);

        notificationUtils = new NotificationUtils(context);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent, R.drawable.baterry_charged, R.drawable.baterry_charged3, Configuration.NOTIFICATION_CHARGING_ID);
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

