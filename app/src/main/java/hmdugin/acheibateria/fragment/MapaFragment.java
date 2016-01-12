package hmdugin.acheibateria.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseAnalytics;
import com.parse.ParseException;

import java.io.File;
import java.util.List;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.ListaDeLojas;
import hmdugin.acheibateria.domain.ListaMarker;
import hmdugin.acheibateria.domain.Localizacao;
import hmdugin.acheibateria.domain.Loja;
import hmdugin.acheibateria.eventBus.MessageEB;
import hmdugin.acheibateria.util.BitmapDecodeUtil;
import hmdugin.acheibateria.util.CalendarUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment {
    static boolean aqui = true;
    private final String TAG = this.getClass().getSimpleName();
    MapView mMapView;
    RelativeLayout relativeLayout;
    Localizacao localizacao = new Localizacao();
    List<Loja> listaDeLojas;
    Bitmap bitmap;
    private GoogleMap googleMap;
    private View view;
    public MapaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (aqui) {
            EventBus.getDefault().register(this);
            aqui = false;
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);


        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.mapa_layout);
        mMapView.onResume();// needed to get the map to display immediately
        Bundle args = getArguments();


        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        new MyTask().execute();




        double latitude = localizacao.getLocation().getLatitude();
        double longitude = localizacao.getLocation().getLongitude();
        // latitude and longitude
        //pos = args.getInt("pos");
        listaDeLojas = ListaDeLojas.getInstance().getListaDeCompras();


        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title("Você está aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint))
                .flat(false);
        // adding marker
        googleMap.addMarker(marker);




        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Log.d(TAG, marker.getId());
                if (marker.getId().equals("m0")) {
                    ParseAnalytics.trackEventInBackground("MarkerPessoaClicado");

                } else
                    ParseAnalytics.trackEventInBackground("MarkerLojaClicado");
                return false;
            }
        });


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                view = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);
                int n = marker.getId().charAt(1) - '0' - 1;
                TextView txtNome, txtHrFunc;
                txtNome = (TextView) view.findViewById(R.id.txtNomeMapa);
                ImageView image = (ImageView) view.findViewById(R.id.imgLojaMapa);
                File f = null;
                byte[] bitmapdata = new byte[0];
                try {
                    if (n >= 0) {
                        bitmapdata = listaDeLojas.get(n).getImg().getData();
                        f = listaDeLojas.get(n).getImg().getFile();
                        txtNome.setText(listaDeLojas.get(n).getNome());
                        if (!listaDeLojas.get(n).getIsWifiAvailable())
                            view.findViewById(R.id.imgWifiMapa).setVisibility(View.GONE);
                        txtHrFunc = (TextView) view.findViewById(R.id.txtHrFuncMapa);
                        String texto = CalendarUtil.HrFuncionamento(listaDeLojas.get(n));
                        txtHrFunc.setText(texto);
                        System.gc();


                    } else if (n == -1) {

                        return getActivity().getLayoutInflater().inflate(R.layout.marker_info_eu, null);


                    }
                } catch (ParseException e) {
                    e.printStackTrace();

                }


                bitmap = BitmapDecodeUtil.getRoundedCornerBitmap(BitmapDecodeUtil.decodeFile(f));
                image.setImageBitmap(bitmap);


                //  TextView textView = (TextView) view.findViewById(R.id.textView);
                //textView.setText(listaDeLojas.get(2).getNome());

                return view;
            }
        });

        v.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hmdugin@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
            }
        });

        // Perform any camera updates here
        return v;
    }


    @Override
    public void onResume() {

        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();



    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onEvent(MessageEB event) {
        Log.d(TAG, "onEvent= " + event.getData());
        if (event.getData().equals(TAG)) {

            int pos = event.getPos();
            Log.d(TAG, "Posição: " + pos);
            ListaMarker.getInstance().getListaMarker().get(pos).showInfoWindow();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(listaDeLojas.get(pos).getCoord().getLatitude(),
                            listaDeLojas.get(pos).getCoord().getLongitude())).zoom(15).build();
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }


    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            if (listaDeLojas != null) {
                while (listaDeLojas.isEmpty()) {
                    listaDeLojas = ListaDeLojas.getInstance().getListaDeCompras();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            Log.d(TAG, "onPostExecute");
            for (int i = 0; i < listaDeLojas.size(); i++) {
                Loja loja = listaDeLojas.get(i);

                // create marker
                MarkerOptions marker2 = new MarkerOptions().position(
                        new LatLng(loja.getCoord().getLatitude(), loja.getCoord().getLongitude()))
                        .title(loja.getNome())
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.recarrega2));


                Marker marker = googleMap.addMarker(marker2);
                ListaMarker.getInstance().addMarker(marker);

            }


            relativeLayout.setVisibility(View.VISIBLE);
        }


    }


}
