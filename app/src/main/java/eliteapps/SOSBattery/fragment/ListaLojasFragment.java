package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import java.util.Iterator;
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
import eliteapps.SOSBattery.util.InternetConnectionUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaLojasFragment extends Fragment {


    static boolean aqui = true;
    private final String TAG = this.getClass().getSimpleName();
    PrefManager prefManager;
    TreeMap<Float, Estabelecimentos> map = new TreeMap<>();
    TreeMap<Float, Estabelecimentos> mapComcabo = new TreeMap<>();
    MyRecyclerAdapter adapter;
    RecyclerView mRecyclerView;
    Location l;
    Location l2;
    Tracker mTracker;
    Firebase myFirebaseRef;
    View view;
    private double lat, lon;


    public ListaLojasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lista_lojas, container, false);

        if (aqui) {
            EventBus.getDefault().register(this);
            aqui = false;
        }

        prefManager = new PrefManager(getActivity(), "MainActivity");

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        limpaTreeMapa(map);
        limpaTreeMapa(mapComcabo);

        if(!ListaDeCoordenadas.getInstance().getListaDeCoordenadas().isEmpty())
            ListaDeCoordenadas.getInstance().getListaDeCoordenadas().clear();

        lat = Localizacao.getInstance().localizacaoAppBackground(getActivity()).getLatitude();
        lon = Localizacao.getInstance().localizacaoAppBackground(getActivity()).getLongitude();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);


        conectaAoFirebase(2);

        ImageButton filterListBtn = (ImageButton) getActivity().findViewById(R.id.filter_list);

        filterListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_container, new FilterListFragment(), "FilterListFragment");
                transaction.addToBackStack(TAG);
                transaction.commit();

            }
        });


        return view;
    }


    private void filtraDados(boolean isCabo, boolean isWifi, String categoria) {


        List<Estabelecimentos> list = ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos();

        Log.println(Log.ASSERT, TAG, "-------------Lista1-----------------");

        for (int i = 0; i < list.size(); i++)
            Log.println(Log.ASSERT, TAG, "" + list.get(i).getNome());


        Iterator<Estabelecimentos> it = list.iterator();

        while (it.hasNext()) {
            Estabelecimentos e = it.next();
            if (isCabo) {
                if (!e.getCabo()) {
                    it.remove();
                    continue;

                }
            }
            if (isWifi) {
                if (!e.getWifi()) {
                    it.remove();
                    continue;

                }
            }
            if (categoria != null) {
                if (!categoria.equals("Tudo")) {
                    if (categoria.equals("Restaurante") == !e.getTipo().equals("restaurant")) {

                        it.remove();


                    } else if (categoria.equals("Loja") == !e.getTipo().equals("clothing_store")) {

                        it.remove();


                    } else if (categoria.equals("Bar") == !e.getTipo().equals("bar")) {

                        it.remove();


                    }
                }
            }
        }


        semLoja(view, true);


        attachAdapter();


        if (!NotificationUtils.isAppIsInBackground(getActivity())) {


            if (ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().size() == ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size()) {


                prefManager = new PrefManager(getActivity(), "LocationService");

                prefManager.pegaDataEHora(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date()));

                toTreeset(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos());


            }
        }

    }


    private void attachAdapter() {
        adapter = new MyRecyclerAdapter(getActivity(), ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), l);

        mRecyclerView.setAdapter(adapter);


        adapter.setRecyclerViewOnClickListenerHack(new RecyclerViewOnClickListenerHack() {
            @Override
            public void onClickListener(View view, int position) {


                MessageEB m = new MessageEB("MapaFragment");
                m.setPos(position);

                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Button")
                        .setAction("Store Selected on the List position: " + position)
                        .setLabel("Store List")
                        .build());

                EventBus.getDefault().post(m);
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(1);
            }
        });
    }

    private void conectaAoFirebase(int raio) {

        myFirebaseRef = new Firebase("https://flickering-heat-3899.firebaseio.com/estabelecimentos");

        GeoFire geoFire = new GeoFire(new Firebase("https://flickering-heat-3899.firebaseio.com/coordenadas"));


        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), raio);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {


                Log.println(Log.ASSERT, TAG, "onKeyEntered");

                Location location1 = new Location("");
                location1.setLatitude(location.latitude);
                location1.setLongitude(location.longitude);

                ListaDeCoordenadas.getInstance().addEstabelecimento(key, location1);


                myFirebaseRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists()) {


                            Estabelecimentos estabelecimentos = dataSnapshot.getValue(Estabelecimentos.class);

                            l2 = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(estabelecimentos.getId());

                            if (estabelecimentos.getCabo())
                                mapComcabo.put(l.distanceTo(l2), estabelecimentos);
                            else
                                map.put(l.distanceTo(l2), estabelecimentos);


                            if (map.size() + mapComcabo.size() == ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size()) {


                                ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();

                                populaListaDeEstabelecimento(mapComcabo);
                                populaListaDeEstabelecimento(map);

                                semLoja(view, true);


                                attachAdapter();

                            }

                        }


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                        Log.println(Log.ASSERT, TAG, "onCancelled");
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


                if (!InternetConnectionUtil.isNetworkAvailable(getActivity())) {
                    if (DialogoDeProgresso.getDialog() != null)
                        DialogoDeProgresso.getDialog().dismiss();
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Falha na conexão")
                            .setMessage("Verifique se o seu dispositivo está conectado à rede e tente novamente")
                            .setPositiveButton("Reconectar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ImageButton refreshBtn = (ImageButton) getActivity().findViewById(R.id.refresh_button);
                                    refreshBtn.callOnClick();
                                }
                            })
                            .setIcon(R.drawable.ic_no_signal)
                            .show();

                }

                if (ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size() == 0) {



                    // Build and send an Event.
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Screen")
                            .setAction("No Store Available")
                            .setLabel("No Store")
                            .build());

                    semLoja(view, false);


                }
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {

                Log.println(Log.ASSERT, TAG, "onGeoQueryError");
            }

        });



        view.findViewById(R.id.botãoFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Button")
                        .setAction("No Store available")
                        .setLabel("No Store suggest")
                        .build());

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.drawer_container, new InsereEstabelecimentoFragment(), "InsereEstabelecimentoFragment");
                transaction.addToBackStack("MainFragment");
                transaction.commit();


            }
        });


    }

    private void semLoja(View view, boolean isStoreAvailble) {

        if (DialogoDeProgresso.getDialog() != null)
            DialogoDeProgresso.getDialog().dismiss();

        getActivity().findViewById(R.id.barraLoading).setVisibility(View.GONE);
        getActivity().findViewById(R.id.textLoading).setVisibility(View.GONE);
        view.findViewById(R.id.lista_lojas_fragment).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.filter_list).setVisibility(View.VISIBLE);


        if (isStoreAvailble) {

            view.findViewById(R.id.textNenhumaLoja).setVisibility(View.GONE);
            view.findViewById(R.id.imageSad).setVisibility(View.GONE);
            view.findViewById(R.id.botãoFeedbackLista).setVisibility(View.GONE);
            view.findViewById(R.id.txtSemLoja).setVisibility(View.GONE);
            PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
            pagerSlidingTabStrip.setVisibility(View.VISIBLE);
            CustomViewPager viewPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
            viewPager.setPagingEnabled(true);

        } else {
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
            viewPager.setCurrentItem(0);
            viewPager.setPagingEnabled(false);

        }
    }


    private void populaListaDeEstabelecimento(TreeMap<Float, Estabelecimentos> map) {

        for (Map.Entry<Float, Estabelecimentos> entry : map.entrySet()) {

            Estabelecimentos value = entry.getValue();
            ListaDeEstabelecimentos.getInstance().addEstabelecimento(value);
            MessageEB m = new MessageEB(TAG);
            m.setE(value);
            EventBus.getDefault().post(m);


        }
        //limpaTreeMapa(map);
    }

    private void limpaTreeMapa(TreeMap<Float, Estabelecimentos> e) {

        if (!e.isEmpty())
            e.clear();
    }


    private void toTreeset(List<Estabelecimentos> object) {
        Set<String> latlong = new TreeSet<>();
        HashMap<String,Location> hashMap = ListaDeCoordenadas.getInstance().getListaDeCoordenadas();
        for (int i = 0; i < object.size(); i++) {

            String coord = hashMap.get(object.get(i).getId()).getLatitude() + "_" + hashMap.get(object.get(i).getId()).getLongitude();

            latlong.add(coord);
        }
        prefManager.setlat(latlong);

    }

    public void onEvent(MessageEB event) {

        if (event.getData().equals("FilterListFragment")) {

            filtraDados(event.isCabo(), event.isWifi(), event.getCategoria());
            getActivity().onBackPressed();

            Log.println(Log.ASSERT, TAG, "Categoria: " + event.getCategoria());
            Log.println(Log.ASSERT, TAG, "Raio: " + event.getRaio());
            Log.println(Log.ASSERT, TAG, "wifi: " + event.isWifi());
            Log.println(Log.ASSERT, TAG, "cabo: " + event.isCabo());


        }
    }

}
