package eliteapps.SOSBattery.util;

import com.firebase.client.Firebase;

/**
 * Created by Rodrigo on 15/03/2016.
 */
public class FirebaseUtil {


    private static Firebase firebase;


    private FirebaseUtil() {
    }

    public static Firebase getFirebase() {

        if (firebase == null) {
            firebase = new Firebase("https://flickering-heat-3899.firebaseio.com/estabelecimentos");
        }
        return (firebase);
    }


}
