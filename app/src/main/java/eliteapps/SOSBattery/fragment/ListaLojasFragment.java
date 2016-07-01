package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.extras.RecyclerViewOnClickListenerHack;
import eliteapps.SOSBattery.extras.SimpleDividerItemDecoration;
import eliteapps.SOSBattery.util.CalendarUtil;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.FilterDataUtil;
import eliteapps.SOSBattery.util.FirebaseUtil;
import eliteapps.SOSBattery.util.GeoFireUtil;
import eliteapps.SOSBattery.util.InternetConnectionUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaLojasFragment extends Fragment {



    static boolean fromFilter = false;
    static boolean haveLocation = false;
    static TreeMap<Float, Estabelecimentos> map = new TreeMap<>();
    static TreeMap<Float, Estabelecimentos> mapComcabo = new TreeMap<>();
    static TreeMap<Float, Estabelecimentos> mapLojaFechada = new TreeMap<>();
    static List<Estabelecimentos> listFechados = new ArrayList<>();
    static MyRecyclerAdapter adapter;
    static RecyclerView mRecyclerView;
    private static double lat, lon;
    private final String TAG = this.getClass().getSimpleName();
    PrefManager prefManager;
    Location l;
    SwipeRefreshLayout swipeRefreshLayout;
    Tracker mTracker;
    Firebase myFirebaseRef = FirebaseUtil.getFirebase();

    GeoFire geoFire = GeoFireUtil.getFirebase();

    View view;
    long tam;
    TextView txtSemLoja;
    String cidade;



    public ListaLojasFragment() {
        // Required empty public constructor
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_lista_lojas, container, false);


        prefManager = new PrefManager(getActivity(), "MainActivity");
        txtSemLoja = (TextView) view.findViewById(R.id.txtSemLoja);

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);

        TextView t = (TextView) getActivity().findViewById(R.id.textLoading);
        t.setText("Carregando...");
        t.setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.barraLoading).setVisibility(View.VISIBLE);

        Log.println(Log.ASSERT, TAG, "onCreateView");
        lat = getArguments().getDouble("lat");
        lon = getArguments().getDouble("lon");

        l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);

        Log.println(Log.ASSERT, TAG, " CreateView lat: " + lat);
        Log.println(Log.ASSERT, TAG, "CreateView lon: " + lon);

        conectaAoFirebase(2);


        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));


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
                transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim,
                        android.R.animator.fade_in, android.R.animator.fade_out);
                transaction.replace(R.id.drawer_container, new InsereEstabelecimentoFragment(), "InsereEstabelecimentoFragment");
                transaction.addToBackStack("MainFragment");
                transaction.commit();


            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.primary_dark));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                ImageButton refreshBtn = (ImageButton) getActivity().findViewById(R.id.refresh_button);
                refreshBtn.callOnClick();

            }
        });

        ImageButton filterListBtn = (ImageButton) getActivity().findViewById(R.id.filter_list);

        filterListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim,
                        android.R.animator.fade_in, android.R.animator.fade_out);
                transaction.replace(R.id.drawer_container, new FilterListFragment(), "FilterListFragment");
                transaction.addToBackStack(TAG);
                transaction.commit();

            }
        });

        return view;
    }


    private void filtraDados(boolean isRestaurante, boolean isBar, boolean isLoja, boolean isCabo, boolean isWifi) {


        filtraDuasListas(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), isCabo, isWifi, isRestaurante, isBar, isLoja);
        filtraDuasListas(listFechados, isCabo, isWifi, isRestaurante, isBar, isLoja);

    }


    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    private void filtraDuasListas(List<Estabelecimentos> list, boolean isCabo, boolean isWifi, boolean isRestaurante, boolean isBar, boolean isLoja) {
        Iterator<Estabelecimentos> it = list.iterator();
        boolean temCategoria = false;

        while (it.hasNext()) {
            Estabelecimentos e = it.next();

            if (isCabo) {
                if (!e.getCabo().getAndroid()) {
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


            if (isRestaurante) {
                temCategoria = true;
                if (e.getTipo().equals("restaurant"))
                    continue;


            }
            if (isBar) {
                temCategoria = true;
                if (e.getTipo().equals("bar"))
                    continue;


            }
            if (isLoja) {
                temCategoria = true;
                if (e.getTipo().equals("clothing_store"))
                    continue;


            }
            if (temCategoria)
                it.remove();

            temCategoria = false;




        }


        if (ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().isEmpty()
                && listFechados.isEmpty()) {


            semLoja(view, false);

        } else {

            semLoja(view, true);

            MessageEB m = new MessageEB(TAG);
            m.setEstabelecimentosList(listFechados);

            EventBus.getDefault().post(m);


            if (DialogoDeProgresso.getDialog() != null)
                DialogoDeProgresso.getDialog().dismiss();


            if (!NotificationUtils.isAppIsInBackground(getActivity())) {

                prefManager = new PrefManager(getActivity(), "LocationService");

                prefManager.pegaDataEHora(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date()));

                toTreeset(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos());


            }
        }

    }


    private void attachAdapter() {

        Log.println(Log.ASSERT, TAG, "attachAdapter");

        adapter = new MyRecyclerAdapter(getActivity(), ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), listFechados, l);

        mRecyclerView.setAdapter(adapter);


        adapter.setRecyclerViewOnClickListenerHack(new RecyclerViewOnClickListenerHack() {
            @Override
            public void onClickListener(View view, int position) {


                MessageEB m = new MessageEB("MapaFragment");
                m.setPos(position);
                m.setEstabelecimentosList(listFechados);
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



        if (!ListaDeCoordenadas.getInstance().getListaDeCoordenadas().isEmpty())
            ListaDeCoordenadas.getInstance().getListaDeCoordenadas().clear();

        if (!listFechados.isEmpty())
            listFechados.clear();

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(lat, lon), raio);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {


                Location location1 = new Location("");
                location1.setLatitude(location.latitude);
                location1.setLongitude(location.longitude);

                ListaDeCoordenadas.getInstance().addEstabelecimento(key, location1);



                myFirebaseRef.child(key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (dataSnapshot.exists()) {


                            Estabelecimentos estabelecimentos = dataSnapshot.getValue(Estabelecimentos.class);


                            if (ListaDeCoordenadas.getInstance().getListaDeCoordenadas().containsKey(estabelecimentos.getId())) {
                                Location l2 = ListaDeCoordenadas.getInstance().getListaDeCoordenadas().get(estabelecimentos.getId());

                                verificaLojaFechada(l2, estabelecimentos);

                            }


                            if (map.size() + mapComcabo.size() + mapLojaFechada.size() == ListaDeCoordenadas.getInstance().getListaDeCoordenadas().size()) {
                                Handler handler = new Handler(getActivity().getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();
                                        listFechados.clear();

                                        populaListaDeEstabelecimento(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), mapComcabo);
                                        populaListaDeEstabelecimento(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), map);
                                        populaListaDeEstabelecimento(listFechados, mapLojaFechada);

                                        semLoja(view, true);

                                        if (FilterDataUtil.getInstance().getDistancia() != null)
                                            filtraDados(FilterDataUtil.getInstance().isRestaurante(),
                                                    FilterDataUtil.getInstance().isBar(), FilterDataUtil.getInstance().isLoja(),
                                                    FilterDataUtil.getInstance().isCabo(),
                                                    FilterDataUtil.getInstance().isWifi());

                                        else {
                                            if (!NotificationUtils.isAppIsInBackground(getActivity())) {

                                                prefManager = new PrefManager(getActivity(), "LocationService");

                                                prefManager.pegaDataEHora(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date()));

                                                toTreeset(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos());

                                            }
                                        }


                                        MessageEB m = new MessageEB(TAG);
                                        m.setEstabelecimentosList(listFechados);


                                        EventBus.getDefault().post(m);



                                    }


                                });

                                if (DialogoDeProgresso.getDialog() != null)
                                    DialogoDeProgresso.getDialog().dismiss();




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





    }


    private void semLoja(View view, boolean isStoreAvailble) {

        Log.println(Log.ASSERT, TAG, "semLoja " + isStoreAvailble);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.app_bar);
        TextView textNenhumaLoja = (TextView) view.findViewById(R.id.textNenhumaLoja);
        TextView txtMudaFiltro = (TextView) view.findViewById(R.id.txtMudaFiltro);
        CustomViewPager viewPager = (CustomViewPager) getActivity().findViewById(R.id.pager);

        PagerSlidingTabStrip pagerSlidingTabStrip = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
        viewPager.setOffscreenPageLimit(3);

        getActivity().findViewById(R.id.main_container).setBackgroundColor(getResources().getColor(R.color.colorBackground));
        getActivity().findViewById(R.id.splash_layout).setVisibility(View.GONE);
        getActivity().findViewById(R.id.main_layout).setVisibility(View.VISIBLE);

        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);

        if (DialogoDeProgresso.getDialog() != null)
            DialogoDeProgresso.getDialog().dismiss();

        view.findViewById(R.id.lista_lojas_fragment).setVisibility(View.VISIBLE);

        getActivity().findViewById(R.id.filter_list).setVisibility(View.VISIBLE);

        MessageEB m = new MessageEB("WifiFragment");
        EventBus.getDefault().post(m);


        if (isStoreAvailble) {

            mRecyclerView.setVisibility(View.VISIBLE);

            if (Build.VERSION.SDK_INT >= 21)
                toolbar.setElevation(0);

            txtMudaFiltro.setVisibility(View.GONE);
            textNenhumaLoja.setVisibility(View.GONE);
            view.findViewById(R.id.imageSad).setVisibility(View.GONE);
            view.findViewById(R.id.botãoFeedbackLista).setVisibility(View.GONE);
            txtSemLoja.setVisibility(View.GONE);

            swipeRefreshLayout.setEnabled(true);
            pagerSlidingTabStrip.setVisibility(View.VISIBLE);

            viewPager.setPagingEnabled(true);

        } else {

            swipeRefreshLayout.setEnabled(false);

            if (Build.VERSION.SDK_INT >= 21)
                toolbar.setElevation(10);
            txtMudaFiltro.setVisibility(View.VISIBLE);
            textNenhumaLoja.setVisibility(View.VISIBLE);
            view.findViewById(R.id.imageSad).setVisibility(View.VISIBLE);
            view.findViewById(R.id.botãoFeedbackLista).setVisibility(View.VISIBLE);
            txtSemLoja.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);

            pagerSlidingTabStrip.setVisibility(View.GONE);


            Typeface typeLeelawadee = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");


            txtSemLoja.setTypeface(typeLeelawadee);
            textNenhumaLoja.setTypeface(typeLeelawadee);

            viewPager.setCurrentItem(0);
            viewPager.setPagingEnabled(false);

        }

        attachAdapter();
    }


    private void populaListaDeEstabelecimento(List<Estabelecimentos> list, TreeMap<Float, Estabelecimentos> map) {

        for (Map.Entry<Float, Estabelecimentos> entry : map.entrySet()) {

            Estabelecimentos value = entry.getValue();
            value.setDistancia(entry.getKey());
            list.add(value);

        }
        limpaTreeMapa(map);
    }

    private void limpaTreeMapa(TreeMap<Float, Estabelecimentos> e) {

        if (!e.isEmpty())
            e.clear();
    }


    private void toTreeset(List<Estabelecimentos> object) {
        Set<String> latlong = new TreeSet<>();
        HashMap<String, Location> hashMap = ListaDeCoordenadas.getInstance().getListaDeCoordenadas();
        for (int i = 0; i < object.size(); i++) {

            String coord = hashMap.get(object.get(i).getId()).getLatitude() + "_" + hashMap.get(object.get(i).getId()).getLongitude() + '=' + object.get(i).getId();


            latlong.add(coord);
        }
        prefManager.setlat(latlong);

    }

    private void verificaLojaFechada(Location location, Estabelecimentos estabelecimentos) {


        String hrFunc = CalendarUtil.HrFuncionamento(estabelecimentos);
        if (hrFunc.equals("Não abre")) {

            mapLojaFechada.put(l.distanceTo(location), estabelecimentos);
        } else {

            int hrAtual = Integer.parseInt(CalendarUtil.getCurrentDateandTime().substring(CalendarUtil.getCurrentDateandTime().lastIndexOf(':') - 2, CalendarUtil.getCurrentDateandTime().lastIndexOf(':')));

            int hrAbre = Integer.parseInt(hrFunc.substring(0, 2));
            int hrFecha = Integer.parseInt(hrFunc.substring(hrFunc.length() - 3, hrFunc.length() - 1));

            if (hrAtual < 6)
                hrAtual += 24;


            if (hrFecha < 9)
                hrFecha += 24;


            if (hrAtual < hrAbre || hrAtual >= hrFecha) {

                mapLojaFechada.put(l.distanceTo(location), estabelecimentos);
            } else if (estabelecimentos.getCabo().getAndroid()) {

                mapComcabo.put(l.distanceTo(location), estabelecimentos);
            } else {

                map.put(l.distanceTo(location), estabelecimentos);
            }


        }


    }

    public void onEvent(MessageEB event) {

        if (event.getData().equals("FilterListFragment")) {

            getActivity().onBackPressed();
            new DialogoDeProgresso(getActivity(), "Carregando Estabelecimentos...");

            fromFilter = true;

            ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();
            listFechados.clear();
            if (event.getRaio() == 20) {
                wholeCityQuery();

            } else {

                conectaAoFirebase(event.getRaio());
            }

        } else if (event.getData().equals("All")) {


            haveLocation = true;

            limpaTreeMapa(map);
            limpaTreeMapa(mapComcabo);
            limpaTreeMapa(mapLojaFechada);


            lat = event.getLocation().getLatitude();
            lon = event.getLocation().getLongitude();

            Log.println(Log.ASSERT, TAG, "lat: " + lat);
            Log.println(Log.ASSERT, TAG, "lon: " + lon);

            Geocoder gcd = new Geocoder(getActivity(), Locale.FRENCH);
            List<Address> addresses;

            try {
                addresses = gcd.getFromLocation(lat, lon, 1);
                if (addresses.size() > 0)
                    cidade = addresses.get(0).getLocality();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.println(Log.ASSERT, TAG, "cidade vindo da Main: " + cidade);

            l = new Location("");
            l.setLatitude(lat);
            l.setLongitude(lon);


            if (FilterDataUtil.getInstance().getDistancia() != null) {
                if (FilterDataUtil.getInstance().getDistancia().equals("Cidade"))
                    wholeCityQuery();
                else
                    conectaAoFirebase(Integer.parseInt(FilterDataUtil.getInstance().getDistancia().substring(0, FilterDataUtil.getInstance().getDistancia().length() - 3)));

            } else
                conectaAoFirebase(2);





        }
    }

    private void wholeCityQuery() {

        Log.println(Log.ASSERT, TAG, "cidade: " + cidade);


        if (!ListaDeCoordenadas.getInstance().getListaDeCoordenadas().isEmpty())
            ListaDeCoordenadas.getInstance().getListaDeCoordenadas().clear();

        if (!listFechados.isEmpty())
            listFechados.clear();


        if (cidade.isEmpty())
            cidade = "Rio de Janeiro";

        myFirebaseRef.orderByChild("cidade").equalTo(cidade).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                tam = dataSnapshot.getChildrenCount();
                Log.println(Log.ASSERT, TAG, "Firebase whole city onDataChange");

                if (dataSnapshot.exists()) {

                    Log.println(Log.ASSERT, TAG, "Firebase whole city Data exist");

                    Handler handler = new Handler(getActivity().getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();


                            while (it.hasNext()) {
                                Estabelecimentos estabelecimentos = it.next().getValue(Estabelecimentos.class);

                                Location location1 = new Location("");

                                location1.setLatitude(Double.parseDouble(estabelecimentos.getCoordenadas()[0]));
                                location1.setLongitude(Double.parseDouble(estabelecimentos.getCoordenadas()[1]));

                                ListaDeCoordenadas.getInstance().addEstabelecimento(estabelecimentos.getId(), location1);

                                verificaLojaFechada(location1, estabelecimentos);


                                if (map.size() + mapComcabo.size() + mapLojaFechada.size() == tam) {


                                    ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();
                                    listFechados.clear();

                                    semLoja(view, true);


                                    populaListaDeEstabelecimento(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), mapComcabo);
                                    populaListaDeEstabelecimento(ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos(), map);
                                    populaListaDeEstabelecimento(listFechados, mapLojaFechada);

                                    filtraDados(FilterDataUtil.getInstance().isRestaurante(),
                                            FilterDataUtil.getInstance().isBar(), FilterDataUtil.getInstance().isLoja(),
                                            FilterDataUtil.getInstance().isCabo(),
                                            FilterDataUtil.getInstance().isWifi());


                                    MessageEB m = new MessageEB(TAG);
                                    m.setEstabelecimentosList(listFechados);

                                    EventBus.getDefault().post(m);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (DialogoDeProgresso.getDialog() != null)
                                                DialogoDeProgresso.getDialog().dismiss();
                                        }
                                    });


                                }

                            }

                        }
                    });
                } else {
                    semLoja(view, false);
                }


            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.println(Log.ASSERT, TAG, "Firebase whole city onCancelled");

            }
        });
    }
}

