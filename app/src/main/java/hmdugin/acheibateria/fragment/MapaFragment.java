package hmdugin.acheibateria.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.ListaDeLojas;
import hmdugin.acheibateria.domain.Loja;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapaFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    MapView mMapView;
    private GoogleMap googleMap;
    private Button carreguei, recomendo;
    public MapaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mapa, container, false);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately
        Bundle args = getArguments();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();

        List<Loja> listaDeLojas = ListaDeLojas.getInstance().getListaDeCompras();
        double latitude = args.getDouble("lat");
        double longitude = args.getDouble("lon");
        // latitude and longitude
        int pos = args.getInt("pos");



        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude))
                .title("Você")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pinpoint))
                .flat(false);


        for (int i = 0; i < listaDeLojas.size(); i++) {
            Loja loja = listaDeLojas.get(i);

            // create marker
            MarkerOptions marker2 = new MarkerOptions().position(
                    new LatLng(loja.getCoord().getLatitude(), loja.getCoord().getLongitude()))
                    .title(loja.getNome())
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.recarrega));
            if (pos == i)
                googleMap.addMarker(marker2).showInfoWindow();
            else
                googleMap.addMarker(marker2);

        }
        // adding marker
        googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


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
