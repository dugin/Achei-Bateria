package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import eliteapps.SOSBattery.R;
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

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

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
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
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
                view.findViewById(R.id.textViewLogin).setVisibility(View.GONE);
            }
        });


        txtSenha = (EditText) view.findViewById(R.id.editTextSenha);
        txtSenha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                view.findViewById(R.id.textViewSenha).setVisibility(View.GONE);
            }
        });


        btnEntrar = (FancyButton) view.findViewById(R.id.botãoEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
