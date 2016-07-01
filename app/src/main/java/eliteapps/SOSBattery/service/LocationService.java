package eliteapps.SOSBattery.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.NotificationActivity;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.util.Configuration;
import eliteapps.SOSBattery.util.MathUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


public class LocationService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    LocationManager mlocManager;
    LocationListener mlocListener;
    boolean intervaloCerto;
    PrefManager prefManager;
    NotificationUtils notificationUtils;
    Tracker mTracker;
    public LocationService() {

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        intervaloCerto = false;

        prefManager = new PrefManager(getApplicationContext(), TAG);


        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();

        String currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());

        if (prefManager.getDataEHora() != null)
            intervaloCerto = MathUtil.calcIntTempo(prefManager.getDataEHora(), currentDateandTime);



        if (intervaloCerto) {
            Log.println(Log.ASSERT, TAG, "intervalo correto");
            mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mlocListener = new MyLocationListener();


        } else
            stopSelf();



    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (intervaloCerto)
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

        return START_STICKY;
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


    private void carregou(Location location) {



        Set<String> coord = prefManager.getCoord();

        for (String s : coord) {

            String lat = s.substring(0, s.indexOf("_"));

            String lon = s.substring(s.indexOf("_") + 1, s.indexOf("="));

            String key = s.substring(s.indexOf("=") + 1, s.length());


            Location locationLojas = new Location("");
            locationLojas.setLatitude(Double.parseDouble(lat));
            locationLojas.setLongitude(Double.parseDouble(lon));

            if (location.distanceTo(locationLojas) < 15) {
                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Core")
                        .setAction("Charging on ID: " + key)
                        .setLabel("Charging")
                        .build());

                showNotificationMessage(getApplicationContext());

                break;


            }

        }
    }


    private class MyLocationListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            Log.println(Log.ASSERT, TAG, "onLocationChanged");

            carregou(location);

            mlocManager.removeUpdates(this);
            mlocManager = null;

   /*
                if (prefManager.getLatLonCasa() == null) {
                    Log.println(Log.ASSERT, TAG, "prefManager.getLatLonCasa() == null");
                    TreeSet<String> t = new TreeSet<String>();
                    t.add(location.getLatitude() + "_" + location.getLongitude());
                    prefManager.setLatLonCasa(t);
                } else {
                    Log.println(Log.ASSERT, TAG, "prefManager.getLatLonCasa() != null");
                    prefManager.getLatLonCasa().add(location.getLatitude() + "_" + location.getLongitude());

                    Set<String> latLonCasa = prefManager.getLatLonCasa();
                    for (String s :latLonCasa) {
                        Log.println(Log.ASSERT, TAG, "s: " + s);
                    }
                }

                if (prefManager.getLatLonCasa().size() > 2) {
                    int cont = 0;

                    Set<String> latLonCasa = prefManager.getLatLonCasa();
                    ArrayList<Location> locations = new ArrayList<>();

                    for (String s : latLonCasa) {
                        Log.println(Log.ASSERT, TAG, "s: " + s);
                        String lat = s.substring(0, s.indexOf("_"));
                        String lon = s.substring(s.indexOf("_") + 1);
                        Location l = new Location("");
                        l.setLatitude(Double.parseDouble(lat));
                        l.setLongitude(Double.parseDouble(lon));
                        locations.add(l);

                    }

                    for (int i = 1; i < locations.size(); i++) {
                        if (locations.get(0).distanceTo(locations.get(i)) < 5)
                            cont++;
                    }
                    if (cont == 2) {
                        Log.println(Log.ASSERT, TAG, "casa");
                        prefManager.apagar();
                    }


            } */

            stopSelf();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}

