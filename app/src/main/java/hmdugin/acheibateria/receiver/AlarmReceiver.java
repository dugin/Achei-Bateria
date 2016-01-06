package hmdugin.acheibateria.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import hmdugin.acheibateria.util.NotificationUtils;
import hmdugin.acheibateria.util.PrefManager;

/**
 * Created by Rodrigo on 29/12/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();
    PrefManager prefManager;

    @Override
    public void onReceive(Context context, Intent intent) {


        System.gc();

        Log.d(TAG, "onReceive");
        if (NotificationUtils.isAppIsInBackground(context)) {
            prefManager = new PrefManager(context);
            prefManager.setPrimeiraVez(false);


        }
    }


}
