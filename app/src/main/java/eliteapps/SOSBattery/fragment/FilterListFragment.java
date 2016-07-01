package eliteapps.SOSBattery.fragment;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.FilterDataUtil;
import eliteapps.SOSBattery.util.InternetConnectionUtil;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterListFragment extends Fragment {


    static DiscreteSeekBar distancia;
    static TextView txtDistancia;
    static FancyButton filtrarBtn;
    static CheckBox cabo, wifi, restaurante, bar, loja;
    private final String TAG = this.getClass().getSimpleName();
    AlertDialog alertDialog;
    Tracker mTracker;
    int distIni;

    // Declaring the String Array with the Text Data for the Spinners
    String[] categorias = {"Tudo", "Loja", "Bar",
            "Restaurante"};

    public FilterListFragment() {
        // Required empty public constructor
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filter_list, container, false);


        NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();



        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        getActivity().findViewById(R.id.filter_list).setVisibility(View.GONE);
        getActivity().findViewById(R.id.refresh_button).setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= 21)
            toolbar.setElevation(10);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        NavigationDrawerUtil.setIsDrawer(false);





        distancia = (DiscreteSeekBar) v.findViewById(R.id.seekBarDist);
        txtDistancia = (TextView) v.findViewById(R.id.textViewDistancia);


        changeTextFont(txtDistancia);
        changeTextFont((TextView) v.findViewById(R.id.t));
        changeTextFont((TextView) v.findViewById(R.id.t1));
        changeTextFont((TextView) v.findViewById(R.id.t2));


        distancia.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                if (value > 93)
                    txtDistancia.setText("Cidade");

                else if (value < 6)
                    txtDistancia.setText("1 km");

                else
                    txtDistancia.setText("" + (value / 6) + " km");

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        cabo = (CheckBox) v.findViewById(R.id.checkBoxCabo);
        wifi = (CheckBox) v.findViewById(R.id.checkBoxWifi);
        restaurante = (CheckBox) v.findViewById(R.id.checkBoxRestaurante);
        bar = (CheckBox) v.findViewById(R.id.checkBoxBar);
        loja = (CheckBox) v.findViewById(R.id.checkBoxLoja);

        filtrarBtn = (FancyButton) v.findViewById(R.id.botaoFiltrar);

        filtrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.println(Log.ASSERT, TAG, "filtrarBtn onClick");

                MessageEB m = new MessageEB(TAG);
                m.setIsCabo(cabo.isChecked());
                m.setIsWifi(wifi.isChecked());
                m.setIsBar(bar.isChecked());
                m.setIsRestaurant(restaurante.isChecked());
                m.setIsStore(loja.isChecked());

                if (txtDistancia.getText().toString().equals("Cidade")) {
                    m.setRaio(20);
                    m.setDifDist(20 - distIni);
                } else {
                    int distFim = Integer.parseInt(txtDistancia.getText().subSequence(0, txtDistancia.getText().length() - 3).toString());
                    m.setRaio(distFim);
                    m.setDifDist(distFim - distIni);

                }


                FilterDataUtil.getInstance().setAll(restaurante.isChecked(), bar.isChecked(), loja.isChecked()
                        , cabo.isChecked(), wifi.isChecked(), distancia.getProgress(), txtDistancia.getText().toString());

                if (!InternetConnectionUtil.isNetworkAvailable(getActivity())
                        && !NotificationUtils.isAppIsInBackground(getActivity()))
                    semConexao();

                else
                    EventBus.getDefault().post(m);



            }
        });

        if (FilterDataUtil.getInstance().getDistancia() != null) {

            FilterDataUtil f = FilterDataUtil.getInstance();

            cabo.setChecked(f.isCabo());
            wifi.setChecked(f.isWifi());
            bar.setChecked(f.isBar());
            restaurante.setChecked(f.isRestaurante());
            loja.setChecked(f.isLoja());
            distancia.setProgress(f.getProgresso());
            txtDistancia.setText(f.getDistancia());

        }

        if (txtDistancia.getText().toString().equals("Cidade"))
            distIni = 20;

        else
            distIni = Integer.parseInt(txtDistancia.getText().subSequence(0, txtDistancia.getText().length() - 3).toString());






        return v;
    }

    private void changeTextFont(TextView t) {
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
        t.setTypeface(type);
    }

    protected void semConexao() {

        if (!InternetConnectionUtil.isNetworkAvailable(getActivity())
                && !NotificationUtils.isAppIsInBackground(getActivity())) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    estaConectado();
                }
            });

        }
    }

    private void estaConectado() {

        alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Falha na conexão")
                .setMessage("Verifique se o seu dispositivo está conectado à rede e tente novamente")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                })
                .setIcon(R.drawable.ic_no_signal)
                .show();


    }



}
