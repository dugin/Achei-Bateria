package eliteapps.SOSBattery.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseAnalytics;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.NotificationActivity;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.Configuration;
import eliteapps.SOSBattery.util.GoogleAPIConnectionUtil;
import eliteapps.SOSBattery.util.MathUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


public class LocationService extends Service {

    private final String TAG = this.getClass().getSimpleName();
    GoogleAPIConnectionUtil googleAPIConnectionUtil;
    Location location;
    PrefManager prefManager;
    NotificationUtils notificationUtils;
    GoogleApiClient mGoogleApiClient;
    public LocationService() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        boolean intervaloCerto = false;
        EventBus.getDefault().register(this);
        prefManager = new PrefManager(getApplicationContext());

        String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());

        if (prefManager.getDataEHora() != null)
            intervaloCerto = MathUtil.calcIntTempo(prefManager.getDataEHora(), currentDateandTime);



        if (intervaloCerto) {
            googleAPIConnectionUtil = new GoogleAPIConnectionUtil(getApplicationContext());
            GoogleAPIConnectionUtil.setNomeDaClasse(TAG);
            mGoogleApiClient = googleAPIConnectionUtil.getmGoogleApiClient();
            mGoogleApiClient.connect();
        } else {
            prefManager.apagar();
            EventBus.getDefault().unregister(this);
            stopSelf();
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onEvent(MessageEB event) {


        if (event.getData().equals(TAG)) {
            location = event.getLocation();
            mGoogleApiClient.disconnect();

            carregou();
            stopSelf();
            EventBus.getDefault().unregister(this);
        }
    }

    private void showNotificationMessage(Context context) {

        Intent intent = new Intent(context, NotificationActivity.class);

        notificationUtils = new NotificationUtils(context);
        intent.putExtra("from_notification_charging", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String[] msg = notificationUtils.msgCarregouLoja();

        notificationUtils.showNotificationMessage(msg[0], msg[1], intent, R.drawable.ic_baterry_charged,
                R.drawable.ic_stat_baterry_charged, Configuration.NOTIFICATION_CHARGING_ID);
    }


    private void carregou() {


        Set<String> coord = prefManager.getCoord();

        for (String s : coord) {
            String lat = s.substring(0, s.lastIndexOf("_"));
            String lon = s.substring(s.lastIndexOf("_") + 1);
            Location locationLojas = new Location("");
            locationLojas.setLatitude(Double.parseDouble(lat));
            locationLojas.setLongitude(Double.parseDouble(lon));

            if (location.distanceTo(locationLojas) < 25) {

                ParseAnalytics.trackEventInBackground("Carregando");
                showNotificationMessage(getApplicationContext());
                prefManager.apagar();
                break;


            }

        }
    }


}

