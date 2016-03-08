package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.eventBus.MessageEB;
import eliteapps.SOSBattery.util.FilterDataUtil;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterListFragment extends Fragment {


    static DiscreteSeekBar distancia;
    static TextView txtDistancia;
    static FancyButton filtrarBtn;
    static CheckBox cabo, wifi;
    private final String TAG = this.getClass().getSimpleName();
    Spinner categoria;
    Tracker mTracker;
    int distIni;

    public FilterListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_filter_list, container, false);


        NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        getActivity().findViewById(R.id.filter_list).setVisibility(View.GONE);
        getActivity().findViewById(R.id.refresh_button).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        NavigationDrawerUtil.setIsDrawer(false);


        categoria = (Spinner) v.findViewById(R.id.spinnerCategoria);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categoria_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categoria.setPrompt("Selecione uma Categoria");
// Apply the adapter to the spinner
        categoria.setAdapter(adapter);


        distancia = (DiscreteSeekBar) v.findViewById(R.id.seekBarDist);
        txtDistancia = (TextView) v.findViewById(R.id.textViewDistancia);


        changeTextFont(txtDistancia);
        changeTextFont((TextView) v.findViewById(R.id.t));
        changeTextFont((TextView) v.findViewById(R.id.t1));
        changeTextFont((TextView) v.findViewById(R.id.t2));


        distancia.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

                if (value > 93)
                    txtDistancia.setText("Cidade");

                else if (value < 6)
                    txtDistancia.setText("1 km");

                else
                    txtDistancia.setText("" + (value / 6) + " km");

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });

        cabo = (CheckBox) v.findViewById(R.id.checkBoxCabo);
        wifi = (CheckBox) v.findViewById(R.id.checkBoxWifi);

        filtrarBtn = (FancyButton) v.findViewById(R.id.botaoFiltrar);

        filtrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageEB m = new MessageEB(TAG);
                m.setIsCabo(cabo.isChecked());
                m.setIsWifi(wifi.isChecked());
                m.setCategoria(categoria.getSelectedItem().toString());

                if (txtDistancia.getText().toString().equals("Cidade")) {
                    m.setRaio(20);
                    m.setDifDist(20 - distIni);
                } else {
                    int distFim = Integer.parseInt(txtDistancia.getText().subSequence(0, txtDistancia.getText().length() - 3).toString());
                    m.setRaio(distFim);
                    m.setDifDist(distFim - distIni);
                    Log.println(Log.ASSERT, TAG, "distIni: " + distIni + " - distFim: " + distFim);
                }


                FilterDataUtil.getInstance().setAll(cabo.isChecked(), wifi.isChecked(), categoria.getSelectedItemPosition(), categoria.getSelectedItem().toString(), distancia.getProgress(), txtDistancia.getText().toString());
                EventBus.getDefault().post(m);


            }
        });

        if (FilterDataUtil.getInstance().getDistancia() != null) {

            FilterDataUtil f = FilterDataUtil.getInstance();

            cabo.setChecked(f.isCabo());
            wifi.setChecked(f.isWifi());
            categoria.setSelection(f.getCategoria());
            distancia.setProgress(f.getProgresso());
            txtDistancia.setText(f.getDistancia());

        }

        if (txtDistancia.getText().toString().equals("Cidade"))
            distIni = 20;

        else
            distIni = Integer.parseInt(txtDistancia.getText().subSequence(0, txtDistancia.getText().length() - 3).toString());






        return v;
    }

    private void changeTextFont(TextView t) {
        Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Leelawadee.ttf");
        t.setTypeface(type);
    }


}
