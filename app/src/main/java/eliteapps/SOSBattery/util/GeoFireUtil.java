package eliteapps.SOSBattery.util;

import com.firebase.client.Firebase;
import com.firebase.geofire.GeoFire;

/**
 * Created by Rodrigo on 15/03/2016.
 */

public class GeoFireUtil {


    private static GeoFire geoFire;


    private GeoFireUtil() {
    }

    public static GeoFire getFirebase() {
        if (geoFire == null) {
            geoFire = new GeoFire(new Firebase("https://flickering-heat-3899.firebaseio.com/coordenadas"));
        }
        return (geoFire);
    }


}
