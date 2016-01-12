package hmdugin.acheibateria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hmdugin.acheibateria.service.LocationService;

/**
 * Created by Rodrigo on 06/01/2016.
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.println(Log.ASSERT, TAG, "onReceive");
        Intent serviceIntent = new Intent(context, LocationService.class);
        context.startService(serviceIntent); //start service for get location

    }
}
