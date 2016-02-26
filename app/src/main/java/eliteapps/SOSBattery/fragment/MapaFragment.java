package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
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

import com.squareup.picasso.Picasso;

import java.util.List;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.domain.ListaDeCoordenadas;
import eliteapps.SOSBattery.domain.ListaDeEstabelecimentos;
import eliteapps.SOSBattery.domain.ListaMarker;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.extras.CircleTransformation;
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
    List<Estabelecimentos> estabelecimentosList;
    double latitude;
    double longitude;
    private GoogleMap googleMap;

    Tracker mTracker;

    private View view;

    public MapaFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();




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



        try {
            latitude = localizacao.getLocation().getLatitude();
            longitude = localizacao.getLocation().getLongitude();
        } catch (NullPointerException e) {
            Log.println(Log.ASSERT, TAG, "Catch");

            System.exit(1);


        }




        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.moveCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Marker")
                        .setAction("Marker Selected")
                        .setLabel("Marker "+ marker.getId())
                        .build());

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
                estabelecimentosList = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();



                view = getActivity().getLayoutInflater().inflate(R.layout.marker_info, null);
                int n = marker.getId().charAt(1) - '0';

                TextView txtNome = (TextView) view.findViewById(R.id.txtNomeMapa);
                TextView hrFunc = (TextView) view.findViewById(R.id.txtHrFuncMapa);

                ImageView image = (ImageView) view.findViewById(R.id.imgLojaMapa);

                txtNome.setText(estabelecimentosList.get(n).getNome());
                String hrFunc2 = CalendarUtil.HrFuncionamento(estabelecimentosList.get(n));
                hrFunc.setText(hrFunc2);
                Picasso.with(getActivity())
                        .load(estabelecimentosList.get(n).getImgURL())
                        .resize(150, 150)
                        .transform(new CircleTransformation())
                        .centerCrop()
                        .into(image);

                if (!estabelecimentosList.get(n).getWifi())
                    view.findViewById(R.id.imgWifiMapa).setVisibility(View.GONE);

                if (!estabelecimentosList.get(n).getCabo())
                    view.findViewById(R.id.imgCaboMapa).setVisibility(View.GONE);



                        System.gc();







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
                if (position == 1) {
                    googleMap.setMyLocationEnabled(true);
                    mTracker.setScreenName( "MapView");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                }
                else {
                    googleMap.setMyLocationEnabled(false);
                    mTracker.setScreenName( "ListView");
                    mTracker.send(new HitBuilders.ScreenViewBuilder().build());
                }

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

            String ID = estabelecimentosList.get(pos).getId();
            Location location = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(ID);

            ListaMarker.getInstance().getListaMarker().get(pos).showInfoWindow();
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
            googleMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else if (event.getData().equals("ListaLojasFragment")) {

            estabelecimentosList = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();


            for (int i = 0; i < estabelecimentosList.size(); i++) {
                Estabelecimentos estabelecimentos = estabelecimentosList.get(i);
                String ID = estabelecimentosList.get(i).getId();
                Location location = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(ID);

                // create marker
                MarkerOptions marker2 = new MarkerOptions().position(
                        new LatLng(location.getLatitude(), location.getLongitude()))
                        .title(estabelecimentos.getNome())
                        .flat(true)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_battery_charging_full_34_dp));


                Marker marker = googleMap.addMarker(marker2);
                ListaMarker.getInstance().addMarker(marker);


            }


            relativeLayout.setVisibility(View.VISIBLE);
        }


    }


}
