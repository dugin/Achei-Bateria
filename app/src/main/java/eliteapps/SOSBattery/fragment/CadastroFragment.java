package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import eliteapps.SOSBattery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CadastroFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    RadioGroup radioGroup;
    PJuridicaFragment pJuridicaFragment = new PJuridicaFragment();

    public CadastroFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_cadastro, container, false);


        radioGroup = (RadioGroup) view.findViewById(R.id.radioPessoa);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                FragmentTransaction transaction = getFragmentManager().beginTransaction();


                if (checkedId == R.id.radioPJuridica) {
                    view.findViewById(R.id.botao_cadastrar_pfisica).setVisibility(View.GONE);

                    transaction.replace(R.id.layoutPFuridica, pJuridicaFragment, "PJuridicaFragment");
                    // Commit the transaction
                    transaction.commit();
                } else {
                    view.findViewById(R.id.botao_cadastrar_pfisica).setVisibility(View.VISIBLE);
                    transaction.remove(pJuridicaFragment);
                    transaction.commit();
                }

            }
        });
        return view;

    }

}
