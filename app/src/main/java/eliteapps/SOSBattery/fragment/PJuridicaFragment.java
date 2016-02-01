package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import eliteapps.SOSBattery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PJuridicaFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    EditText segSexIni, sabIni, domIni;
    EditText segSexFim, sabFim, domFim;
    int n1;

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
                int hora1, hora2;
                int minuto1;
                Log.println(Log.ASSERT, TAG, "s : " + s);
                Log.println(Log.ASSERT, TAG, "start : " + start);
                Log.println(Log.ASSERT, TAG, "n1 : " + n1);
                if (s.length() >= 1) {
                    hora1 = s.charAt(0) - '0';
                    if (hora1 > 2)
                        segSexIni.setText("");
                }
                if (s.length() == 2) {
                    hora1 = s.charAt(0) - '0';
                    hora2 = s.charAt(1) - '0';
                    if (hora1 == 2 && hora2 > 3) {
                        segSexIni.setText(s.subSequence(0, 1));
                        segSexIni.setSelection(1);
                    } else if (s.charAt(1) != ':') {
                        segSexIni.setText(s + ":");
                        segSexIni.setSelection(3);
                    }

                }
                if (s.length() == 3 && before == 3) {
                    segSexIni.setText(s.subSequence(0, 1));
                    segSexIni.setSelection(1);
                }

                if (s.length() >= 4) {
                    minuto1 = s.charAt(3) - '0';
                    if (minuto1 > 5) {
                        segSexIni.setText(s.subSequence(0, 2));
                        segSexIni.setSelection(3);
                    }
                }
                if (s.length() == 5)
                    segSexFim.requestFocus();

                // if (s.length()>= 5) segSexIni.setText(s.subSequence(0, 4));


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.println(Log.ASSERT, TAG, "Editable s : " + s);

            }
        });

        segSexFim = (EditText) view.findViewById(R.id.SegSexFim);


        return view;
    }

}
