package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.domain.FacebookInfo;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    LoginButton loginButton;
    CallbackManager callbackManager;
    FancyButton btnEntrar, btnCadastrar;
    EditText txtLogin, txtSenha;
    TextView txtEsqueci;
    RadioGroup radioGroup;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "user_photos", "public_profile"));


        callbackManager = CallbackManager.Factory.create();

        NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        NavigationDrawerUtil.getDrawer().getDrawerLayout().setVisibility(View.GONE);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar); // Attaching the layout to the toolbar object

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        NavigationDrawerUtil.setIsDrawer(false);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");
                                            try {

                                                String jsonresult = String.valueOf(json);
                                                Log.println(Log.ASSERT, TAG, "JSON Result" + jsonresult);
                                                Log.println(Log.ASSERT, TAG, "id " + json.getString("id"));

                                                new FacebookInfo(json.getString("id"), json.getString("name"));

                                                Log.println(Log.ASSERT, TAG, "nome " + FacebookInfo.getFirtsName());
                                                Log.println(Log.ASSERT, TAG, "sobrenome " + FacebookInfo.getLastName());


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();

                    }


                    @Override
                    public void onCancel() {
                        // App code
                        Log.println(Log.ASSERT, TAG, "Facebook onCancel: ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.println(Log.ASSERT, TAG, "Facebook onError: ");
                    }
                });
        txtEsqueci = (TextView) view.findViewById(R.id.txtEsqueciSenha);
        txtEsqueci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txtLogin = (EditText) view.findViewById(R.id.editTextLogin);

        txtLogin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    view.findViewById(R.id.textViewLogin).setVisibility(View.GONE);
                else if (txtLogin.getText() == null)
                    view.findViewById(R.id.textViewLogin).setVisibility(View.VISIBLE);

            }
        });

        txtSenha = (EditText) view.findViewById(R.id.editTextSenha);
        txtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    view.findViewById(R.id.textViewSenha).setVisibility(View.GONE);
                else if (txtSenha.getText() == null)
                    view.findViewById(R.id.textViewSenha).setVisibility(View.VISIBLE);
            }
        });


        btnEntrar = (FancyButton) view.findViewById(R.id.botaoEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnCadastrar = (FancyButton) view.findViewById(R.id.botaoCadastrar);


        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack so the user can navigate back
                transaction.replace(R.id.drawer_container, new CadastroFragment(), "CadastroFragment");
                transaction.addToBackStack("LoginFragment");

                // Commit the transaction
                transaction.commit();

            }
        });



        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
