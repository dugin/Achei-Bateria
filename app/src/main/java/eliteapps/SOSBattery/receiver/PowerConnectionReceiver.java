package eliteapps.SOSBattery.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import eliteapps.SOSBattery.service.LocationService;


public class PowerConnectionReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {


        Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent); //start service for get location

    }
}
