package hmdugin.acheibateria.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import hmdugin.acheibateria.domain.Loja;

/**
 * Created by Rodrigo on 16/12/2015.
 */
public class ParseUtils {

    private static String TAG = ParseUtils.class.getSimpleName();


    public static void verifyParseConfiguration(Context context) {
        if (TextUtils.isEmpty(Configuration.PARSE_APPLICATION_ID) || TextUtils.isEmpty(Configuration.PARSE_CLIENT_KEY)) {
            Toast.makeText(context, "Please configure your Parse Application ID and Client Key in AppConfig.java", Toast.LENGTH_LONG).show();
            ((Activity) context).finish();
        }
    }

    public static void registerParse(Context context) {
        // initializing parse library
        ParseObject.registerSubclass(Loja.class);
        Parse.initialize(context, Configuration.PARSE_APPLICATION_ID, Configuration.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();

        ParsePush.subscribeInBackground(Configuration.PARSE_CHANNEL, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null)
                    Log.e(TAG, "Erro: " + e);


            }
        });
    }

    public static void subscribeWithEmail(String email) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("email", email);

        installation.saveInBackground();
    }
}
