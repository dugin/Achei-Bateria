package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseAnalytics;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.MainActivity;
import eliteapps.SOSBattery.adapter.CustomAdapter;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.domain.ListaDeLojas;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.domain.Loja;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListaLojasFragment extends Fragment {


    static boolean aqui = true;
    private final String TAG = this.getClass().getSimpleName();
    Localizacao localizacao = new Localizacao();
    ListView mListView;
    PrefManager prefManager;
    private double lat, lon;
    private CustomAdapter urgentTodosAdapter;

    public ListaLojasFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_lista_lojas, container, false);


        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);


        try {
            lat = localizacao.getLocation().getLatitude();
            lon = localizacao.getLocation().getLongitude();
        } catch (NullPointerException e) {
            startActivity(new Intent(getActivity(), MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }



        // Initialize main ParseQueryAdapter
        ParseGeoPoint point = new ParseGeoPoint(lat, lon);
        urgentTodosAdapter = new CustomAdapter(getActivity(), point);
        mListView = (ListView) view.findViewById(R.id.list);
        mListView.setAdapter(urgentTodosAdapter);

        urgentTodosAdapter.setObjectsPerPage(20);
        urgentTodosAdapter.loadObjects();
        urgentTodosAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            @Override
            public void onLoading() {
            }

            @Override
            public void onLoaded(List<ParseObject> objects, Exception e) {
                if (DialogoDeProgresso.getDialog() != null)
                    DialogoDeProgresso.getDialog().dismiss();
                ListaDeLojas.getInstance().setListaDeCompras((List) objects);
                if (!NotificationUtils.isAppIsInBackground(getActivity())) {

                    getActivity().findViewById(R.id.barraLoading).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.textLoading).setVisibility(View.GONE);
                    view.findViewById(R.id.lista_lojas_fragment).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);


                    prefManager = new PrefManager(getActivity());

                    prefManager.pegaDataEHora(new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date()));
                    toTreeset((List) objects);

                }

                if (urgentTodosAdapter.isEmpty()) {
                    ParseAnalytics.trackEventInBackground("Nenhuma_Loja");
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
                MessageEB m = new MessageEB(TAG);

                EventBus.getDefault().post(m);

            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Loja loja = (Loja) mListView.getItemAtPosition(position);

                Map dimensions = new HashMap();
                dimensions.put("id", loja.getObjectId());
                dimensions.put("pos", position);
                ParseAnalytics.trackEventInBackground("Lista_Loja", dimensions);

                MessageEB m = new MessageEB("MapaFragment");
                m.setPos(position);

                EventBus.getDefault().post(m);
                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                viewPager.setCurrentItem(1);

            }
        });

        view.findViewById(R.id.botãoFeedback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseAnalytics.trackEventInBackground("Nenhuma_Loja_Email");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hmdugin@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
            }
        });


        return view;
    }


    private void toTreeset(List<Loja> object) {
        Set<String> latlong = new TreeSet<>();
        for (int i = 0; i < object.size(); i++) {
            String coord = object.get(i).getCoord().getLatitude() + "_" + object.get(i).getCoord().getLongitude();
            latlong.add(coord);
        }
        prefManager.setlat(latlong);

    }

}
