package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.domain.ListaDeLojas;
import eliteapps.SOSBattery.domain.ListaMarker;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.domain.Loja;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.BitmapDecodeUtil;
import eliteapps.SOSBattery.util.CalendarUtil;

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
    double latitude;
    double longitude;
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



        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.setMyLocationEnabled(true);

        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);


        new MyTask().execute();


        try {
            latitude = localizacao.getLocation().getLatitude();
            longitude = localizacao.getLocation().getLongitude();
        } catch (NullPointerException e) {
            Log.println(Log.ASSERT, TAG, "Catch");

            System.exit(1);


        }

        listaDeLojas = ListaDeLojas.getInstance().getListaDeCompras();


        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                    ParseAnalytics.trackEventInBackground("Marker_Loja");
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
                int n = marker.getId().charAt(1) - '0';
                TextView txtNome, txtHrFunc;
                txtNome = (TextView) view.findViewById(R.id.txtNomeMapa);
                ImageView image = (ImageView) view.findViewById(R.id.imgLojaMapa);
                File f = null;

                try {


                    f = listaDeLojas.get(n).getImg().getFile();
                        txtNome.setText(listaDeLojas.get(n).getNome());
                        if (!listaDeLojas.get(n).getIsWifiAvailable())
                            view.findViewById(R.id.imgWifiMapa).setVisibility(View.GONE);
                        txtHrFunc = (TextView) view.findViewById(R.id.txtHrFuncMapa);
                        String texto = CalendarUtil.HrFuncionamento(listaDeLojas.get(n));
                        txtHrFunc.setText(texto);
                        System.gc();


                } catch (ParseException e) {
                    e.printStackTrace();

                }

                bitmap = BitmapDecodeUtil.getRoundedCornerBitmap(BitmapDecodeUtil.decodeFile(f));
                image.setImageBitmap(bitmap);


                return view;
            }
        });

        CustomViewPager customViewPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
        customViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1)
                    googleMap.setMyLocationEnabled(true);
                else
                    googleMap.setMyLocationEnabled(false);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

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

        super.onDestroy();
        mMapView.onDestroy();



    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public void onEvent(MessageEB event) {

        if (event.getData().equals(TAG)) {

            int pos = event.getPos();

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


            for (int i = 0; i < listaDeLojas.size(); i++) {
                Loja loja = listaDeLojas.get(i);

                // create marker
                MarkerOptions marker2 = new MarkerOptions().position(
                        new LatLng(loja.getCoord().getLatitude(), loja.getCoord().getLongitude()))
                        .title(loja.getNome())
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_battery_charging_full_34_dp));


                Marker marker = googleMap.addMarker(marker2);
                ListaMarker.getInstance().addMarker(marker);

            }


            relativeLayout.setVisibility(View.VISIBLE);
        }


    }


}
