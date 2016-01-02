package hmdugin.acheibateria.fragment;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.ListaDeLojas;
import hmdugin.acheibateria.domain.Localizacao;
import hmdugin.acheibateria.domain.Loja;
import hmdugin.acheibateria.util.BitmapDecodeUtil;
import hmdugin.acheibateria.util.CalendarUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    MapView mMapView;
    Localizacao localizacao = new Localizacao();
    private GoogleMap googleMap;
    private FancyButton carreguei, recomendo;
    private int pos;
    private View view;

    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);


        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately
        Bundle args = getArguments();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();


        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
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


        final List<Loja> listaDeLojas = ListaDeLojas.getInstance().getListaDeCompras();
        double latitude = localizacao.getLocation().getLatitude();
        double longitude = localizacao.getLocation().getLongitude();
        // latitude and longitude
        pos = args.getInt("pos");



        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title("Você está aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint))
                .flat(false);
        // adding marker
        googleMap.addMarker(marker);

        for (int i = 0; i < listaDeLojas.size(); i++) {
            Loja loja = listaDeLojas.get(i);

            // create marker
            MarkerOptions marker2 = new MarkerOptions().position(
                    new LatLng(loja.getCoord().getLatitude(), loja.getCoord().getLongitude()))
                    .title(loja.getNome())
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.recarrega2));

            if (pos == i)
                googleMap.addMarker(marker2).showInfoWindow();
            else
                googleMap.addMarker(marker2);

        }


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

                    } else if (n == -1) {

                        return getActivity().getLayoutInflater().inflate(R.layout.marker_info_eu, null);


                    }
                } catch (ParseException e) {
                    e.printStackTrace();

                }

                Bitmap bitmap = BitmapDecodeUtil.getRoundedCornerBitmap(BitmapDecodeUtil.decodeFile(f));
                image.setImageBitmap(bitmap);


                //  TextView textView = (TextView) view.findViewById(R.id.textView);
                //textView.setText(listaDeLojas.get(2).getNome());

                return view;
            }
        });

        // Perform any camera updates here
        return v;
    }


    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
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


}
