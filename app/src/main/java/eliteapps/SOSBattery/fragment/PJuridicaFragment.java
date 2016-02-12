package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.util.TextRestrictionsUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class PJuridicaFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    EditText segSexIni, sabIni, domIni;
    EditText segSexFim, sabFim, domFim;
    boolean n1 = true;
    TextRestrictionsUtil textRestrictionsUtil = new TextRestrictionsUtil();

    public PJuridicaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pjuridica, container, false);


        segSexIni = (EditText) view.findViewById(R.id.SegSexIni);
        segSexIni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, segSexIni, segSexFim);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        segSexFim = (EditText) view.findViewById(R.id.SegSexFim);
        segSexFim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, segSexFim, sabIni);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sabIni = (EditText) view.findViewById(R.id.SabadoIni);
        sabIni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, sabIni, sabFim);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sabFim = (EditText) view.findViewById(R.id.SabadoFim);
        sabFim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, sabFim, domIni);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        domIni = (EditText) view.findViewById(R.id.DomingoIni);
        domIni.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, domIni, domFim);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        domFim = (EditText) view.findViewById(R.id.DomingoFim);
        domFim.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                textRestrictionsUtil.getHrFuncRestricao(s, domFim, null);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.estado, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(18);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.wifiRadio);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

            }
        });

        return view;
    }

}
