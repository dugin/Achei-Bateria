package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.GoogleApiClient;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.MainActivity;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.GoogleAPIConnectionUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment {

    static boolean aqui = true;
    static boolean temWifi = false;
    private final String TAG = this.getClass().getSimpleName();
    FancyButton btnConectar;
    TextView txtExplicaCon;
    Double lat, lon;
    ValueEventListener valueEventListener;
    Firebase myFirebaseRef;
    GeoFire geoFire;
    GoogleAPIConnectionUtil googleAPIConnectionUtil;
    GoogleApiClient mGoogleApiClient;

    public WifiFragment() {
        // Required empty public constructor
    }


    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_wifi, container, false);

        googleAPIConnectionUtil = new GoogleAPIConnectionUtil(getActivity());
        mGoogleApiClient = googleAPIConnectionUtil.getmGoogleApiClient();


        if (aqui) {
            EventBus.getDefault().register(this);
            aqui = false;
        }


        txtExplicaCon = (TextView) v.findViewById(R.id.textViewConecta);

        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
        txtExplicaCon.setTypeface(type);


        btnConectar = (FancyButton) v.findViewById(R.id.botaoConecta);

        if (getActivity().findViewById(R.id.barraLoading).getVisibility() == View.VISIBLE) {
            btnConectar.setVisibility(View.GONE);
            txtExplicaCon.setVisibility(View.GONE);
        } else {
            btnConectar.setVisibility(View.VISIBLE);
            txtExplicaCon.setVisibility(View.VISIBLE);
        }


        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleAPIConnectionUtil.setMinhaLocalizacao(null);
                googleAPIConnectionUtil.startLocationUpdates();


                myFirebaseRef = new Firebase("https://flickering-heat-3899.firebaseio.com/estabelecimentos");
                geoFire = new GeoFire(new Firebase("https://flickering-heat-3899.firebaseio.com/coordenadas"));

                new DialogoDeProgresso(getActivity(), "Conectando-se ao Wifi do Estabelecimento...");

                Handler handler = new Handler(getActivity().getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        while (true)
                            if (googleAPIConnectionUtil.minhaLocalizacao() != null)
                                break;


                        firebaseValueListener();


                        lat = googleAPIConnectionUtil.minhaLocalizacao().getLatitude();
                        lon = googleAPIConnectionUtil.minhaLocalizacao().getLongitude();

                        Log.println(Log.ASSERT, TAG, "lat: " + lat);
                        Log.println(Log.ASSERT, TAG, "lon: " + lon);


                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), 0.040);
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {

                                temWifi = true;

                                Log.println(Log.ASSERT, TAG, "key: " + key);


                                myFirebaseRef.child(key).addValueEventListener(valueEventListener);


                            }

                            @Override
                            public void onKeyExited(String key) {

                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {


                                        if (!temWifi) {
                                            if (DialogoDeProgresso.getDialog() != null)
                                                DialogoDeProgresso.getDialog().dismiss();

                                            new AlertDialog.Builder(getActivity())
                                                    .setTitle("Erro")
                                                    .setMessage("Nenhum estabelecimento com Wifi encontrado")
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    })
                                                    .setIcon(R.drawable.ic_error_red_48dp)
                                                    .show();

                                        }
                                    }
                                });


                            }

                            @Override
                            public void onGeoQueryError(FirebaseError error) {

                            }
                        });


                    }
                });


            }
        });


        return v;


    }

    private void firebaseValueListener() {
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.exists()) {

                    Estabelecimentos estabelecimentos = dataSnapshot.getValue(Estabelecimentos.class);

                    if (estabelecimentos.getWifi()) {
                        WifiConfiguration wifiConfig = new WifiConfiguration();
                        if (estabelecimentos.getWifi_ssid() == null) {

                            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);


                        } else {


                            wifiConfig.preSharedKey = String.format("\"%s\"", estabelecimentos.getWifi_senha());
                        }


                        wifiConfig.SSID = String.format("\"%s\"", estabelecimentos.getWifi_ssid());

                        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(MainActivity.WIFI_SERVICE);

                        int netId = wifiManager.addNetwork(wifiConfig);
                        wifiManager.disconnect();

                        wifiManager.enableNetwork(netId, true);

                        wifiManager.reconnect();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                new AlertDialog.Builder(getActivity())
                                        .setTitle("Sucesso")
                                        .setMessage("Dentro de alguns segundos o dispositivo se conectará ao Wifi")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(R.drawable.ic_success)
                                        .show();

                                if (DialogoDeProgresso.getDialog() != null)
                                    DialogoDeProgresso.getDialog().dismiss();


                            }
                        });


                        myFirebaseRef.getParent().removeEventListener(valueEventListener);


                    }
                } else {

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (DialogoDeProgresso.getDialog() != null)
                                DialogoDeProgresso.getDialog().dismiss();

                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Erro")
                                    .setMessage("Nenhuma conexão Wifi disponível")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .setIcon(R.drawable.ic_error_red_48dp)
                                    .show();


                        }
                    });


                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }


        };
    }


    public void onEvent(MessageEB event) {

        if (event.getData().equals(TAG)) {

            btnConectar.setVisibility(View.VISIBLE);
            txtExplicaCon.setVisibility(View.VISIBLE);
        }

    }
}
