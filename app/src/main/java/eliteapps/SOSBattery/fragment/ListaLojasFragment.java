package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.adapter.MyRecyclerAdapter;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.domain.ListaDeCoordenadas;
import eliteapps.SOSBattery.domain.ListaDeEstabelecimentos;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.extras.RecyclerViewOnClickListenerHack;
import eliteapps.SOSBattery.extras.SimpleDividerItemDecoration;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaLojasFragment extends Fragment {


    private final String TAG = this.getClass().getSimpleName();
    Localizacao localizacao = new Localizacao();

    PrefManager prefManager;
    TreeMap<Float, Estabelecimentos> map = new TreeMap<>();
    MyRecyclerAdapter adapter;
    RecyclerView mRecyclerView;
    Location l;
    Location l2;
    Tracker mTracker;
    private double lat, lon;
    public ListaLojasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lista_lojas, container, false);

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        if (!map.isEmpty())
            map.clear();

        if(!ListaDeCoordenadas.getInstance().getListaDeCoordenadas().isEmpty())
            ListaDeCoordenadas.getInstance().getListaDeCoordenadas().clear();

        try {
            lat = localizacao.getLocation().getLatitude();
            lon = localizacao.getLocation().getLongitude();
        } catch (NullPointerException e) {

            System.exit(1);

        }

        final Firebase myFirebaseRef = new Firebase("https://flickering-heat-3899.firebaseio.com/estabelecimentos");

        GeoFire geoFire = new GeoFire(new Firebase("https://flickering-heat-3899.firebaseio.com/coordenadas"));


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);
        adapter = new MyRecyclerAdapter(getActivity(), ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), l);

        mRecyclerView.setAdapter(adapter);


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), 2);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                Location location1 = new Location("");
                location1.setLatitude(location.latitude);
                location1.setLongitude(location.longitude);

                ListaDeCoordenadas.getInstance().addEstabelecimento(key, location1);



                myFirebaseRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        Estabelecimentos estabelecimentos = dataSnapshot.getValue(Estabelecimentos.class);

                        l2 = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(dataSnapshot.getKey());


                        map.put(l.distanceTo(l2), estabelecimentos);


                        if (map.size() != ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().size()) {

                            ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();
                            for (Map.Entry<Float, Estabelecimentos> entry : map.entrySet()) {

                                Estabelecimentos value = entry.getValue();
                                ListaDeEstabelecimentos.getInstance().addEstabelecimento(value);
                                adapter.notifyDataSetChanged();


                            }
                        }

                        if (!NotificationUtils.isAppIsInBackground(getActivity())) {


                            if (ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().size() == ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size()) {

                                prefManager = new PrefManager(getActivity(), "LocationService");

                                prefManager.pegaDataEHora(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date()));

                                toTreeset(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos());
                                MessageEB m = new MessageEB(TAG);

                                EventBus.getDefault().post(m);

                            }


                        }


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                if (DialogoDeProgresso.getDialog() != null)
                    DialogoDeProgresso.getDialog().dismiss();

                getActivity().findViewById(R.id.barraLoading).setVisibility(View.GONE);
                getActivity().findViewById(R.id.textLoading).setVisibility(View.GONE);
                view.findViewById(R.id.lista_lojas_fragment).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);

                if (ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size() == 0) {

                    // Build and send an Event.
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Screen")
                            .setAction("No Store Available")
                            .setLabel("No Store")
                            .build());

                    view.findViewById(R.id.textNenhumaLoja).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.imageSad).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.botãoFeedbackLista).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.txtSemLoja).setVisibility(View.VISIBLE);
                    PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
                    pagerSlidingTabStrip.setVisibility(View.GONE);
                    CustomViewPager viewPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                    TextView t1 = (TextView) view.findViewById(R.id.txtSemLoja);
                    TextView t2 = (TextView) view.findViewById(R.id.textNenhumaLoja);
                    Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
                    t1.setTypeface(type);
                    t2.setTypeface(type);
                    viewPager.setPagingEnabled(false);


                }
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {

            }

        });

        adapter.setRecyclerViewOnClickListenerHack(new RecyclerViewOnClickListenerHack() {
            @Override
            public void onClickListener(View view, int position) {

                MessageEB m = new MessageEB("MapaFragment");
                m.setPos(position);

                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Button")
                        .setAction("Store Selected on the List position: "+position)
                        .setLabel("Store List")
                        .build());

                EventBus.getDefault().post(m);
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(1);
            }
        });

        view.findViewById(R.id.botãoFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Email")
                        .setAction("No Store Send Email")
                        .setLabel("No Store Email")
                        .build());


                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hmdugin@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
            }
        });




        return view;
    }


    private void toTreeset(List<Estabelecimentos> object) {
        Set<String> latlong = new TreeSet<>();
        HashMap<String,Location> hashMap = ListaDeCoordenadas.getInstance().getListaDeCoordenadas();
        for (int i = 0; i < object.size(); i++) {

            String coord = hashMap.get(object.get(i).getId()).getLatitude() + "_" + hashMap.get(object.get(i).getId()).getLongitude();
            Log.println(Log.ASSERT,TAG,"toTreeset : "+coord);
            latlong.add(coord);
        }
        prefManager.setlat(latlong);

    }

}
