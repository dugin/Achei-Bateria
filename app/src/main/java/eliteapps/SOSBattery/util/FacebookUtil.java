package eliteapps.SOSBattery.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Window;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import eliteapps.SOSBattery.R;

/**
 * Created by Rodrigo on 10/03/2016.
 */
public class FacebookUtil {

    static CallbackManager callbackManager;
    static Dialog dialog;
    private final String TAG = this.getClass().getSimpleName();
    LoginButton loginButton;
    Firebase ref;
    Context mContext;
    AccessTokenTracker mFacebookAccessTokenTracker;
    PrefManager prefManager;



    public FacebookUtil(Context context) {


        prefManager = new PrefManager(context, TAG);

        // custom dialog
        dialog = new Dialog(context);
        mContext = context;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_facebook);

        FacebookSdk.sdkInitialize(context.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) dialog.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));


        mFacebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                ref = new Firebase("https://flickering-heat-3899.firebaseio.com");

                if (currentAccessToken == null) {
                    ref.unauth();
                    NavigationDrawerUtil.getHeaderNavigationLeft().removeProfile(0);
                    NavigationDrawerUtil.getHeaderNavigationLeft().addProfiles(
                            new ProfileDrawerItem().withName("Olá visitante!")

                    );


                }

                onFacebookAccessTokenChange(currentAccessToken);

            }
        };


        dialog.show();


    }

    public static CallbackManager getCallbackManager() {
        return callbackManager;
    }

    public static Dialog getDialog() {
        return dialog;
    }

    private void onFacebookAccessTokenChange(AccessToken token) {

        if (token != null) {
            Log.println(Log.ASSERT, TAG, "token != null");
            ref.authWithOAuthToken("facebook", token.getToken(), new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {

                    HashMap<String, Object> map = (HashMap<String, Object>) authData.getProviderData().get("cachedUserProfile");

                    Map<String, String> mapaBD = new HashMap<>();

                    String nome = (String) map.get("first_name");
                    String sobrenome = (String) map.get("last_name");
                    String email = (String) map.get("email");

                    String sexo = (String) map.get("gender");

                    LinkedHashMap ageRange = (LinkedHashMap) map.get("age_range");

                    mapaBD.put("id", authData.getUid());
                    mapaBD.put("nome", nome + " " + sobrenome);
                    mapaBD.put("email", email);
                    mapaBD.put("sexo", sexo);


                    mapaBD.put("imgURL", authData.getProviderData().get("profileImageURL").toString());

                    String faixa_idade;

                    if (ageRange.get("max") == null)
                        faixa_idade = "21+";
                    else
                        faixa_idade = ageRange.get("min") + "-" + ageRange.get("max");

                    mapaBD.put("faixa_idade", faixa_idade);

                    String aniversario = (String) map.get("birthday");

                    if (aniversario != null)
                        mapaBD.put("aniversario", aniversario);

                    prefManager.setNomeFacebook(nome + " " + sobrenome);
                    prefManager.setImgURLFacebook(authData.getProviderData().get("profileImageURL").toString());

                    ref.child("usuarios").child(authData.getUid()).setValue(mapaBD);

                    NavigationDrawerUtil.getHeaderNavigationLeft().removeProfile(0);
                    NavigationDrawerUtil.getHeaderNavigationLeft().addProfiles(
                            new ProfileDrawerItem().withName(nome + " " + sobrenome)
                                    .withIcon(authData.getProviderData().get("profileImageURL").toString())
                    );

                    new AlertDialog.Builder(mContext)
                            .setTitle("Login Efetuado")
                            .setMessage("Seja bem vindo(a) " + nome + "!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.ic_success)
                            .show();


                    // The Facebook user is now authenticated with your Firebase app
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    Log.println(Log.ASSERT, TAG, "" + firebaseError.getMessage());
                    Log.println(Log.ASSERT, TAG, "" + firebaseError.getDetails());
                }
            });
        } else {
        /* Logged out of Facebook so do a logout from the Firebase app */
            ref.unauth();
        }
    }
}
