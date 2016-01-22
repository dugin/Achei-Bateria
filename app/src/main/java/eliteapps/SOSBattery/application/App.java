package eliteapps.SOSBattery.application;

import android.app.Application;

import eliteapps.SOSBattery.util.ParseUtils;

/**
 * Created by Rodrigo on 10/12/2015.
 */
public class App extends Application {

    private static App mInstance;

    public static synchronized App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // register with parse
        ParseUtils.registerParse(this);

    }

}