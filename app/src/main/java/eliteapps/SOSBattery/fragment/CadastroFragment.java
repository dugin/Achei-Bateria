package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

import eliteapps.SOSBattery.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CadastroFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();

    RadioGroup radioGroup;
    PJuridicaFragment pJuridicaFragment = new PJuridicaFragment();
    EditText nome, email, senha, senhaRep;
    Boolean isNome = true, isEmail = true, isSenha = true, isSenhaRep = true;


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

        nome = (EditText) view.findViewById(R.id.cadastroNome);
        nome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isNome) {
                    nome.setText("");
                    nome.setTextColor(Color.BLACK);
                    isNome = false;
                }
            }
        });
        email = (EditText) view.findViewById(R.id.cadastroEmail);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isEmail) {
                    email.setText("");
                    email.setTextColor(Color.BLACK);
                    isEmail = false;
                }
            }
        });

        senha = (EditText) view.findViewById(R.id.cadastroSenha);

        senha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isSenha) {


                    senha.setInputType(129);

                    senha.setText("");
                    senha.setTextColor(Color.BLACK);
                    isSenha = false;
                }
            }
        });
        senhaRep = (EditText) view.findViewById(R.id.cadastroSenhaRepete);
        senhaRep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isSenhaRep) {
                    senhaRep.setInputType(129);

                    senhaRep.setText("");
                    senhaRep.setTextColor(Color.BLACK);
                    isSenhaRep = false;
                }
            }
        });
        return view;

    }

}
