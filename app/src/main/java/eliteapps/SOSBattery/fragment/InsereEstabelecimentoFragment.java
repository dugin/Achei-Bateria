package eliteapps.SOSBattery.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.StoreLocationUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsereEstabelecimentoFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    EditText nome, end, numero, email, senha, senhaRep;
    TextView senhaDiscplaimer;

    FancyButton sugerirBtn;
    CheckBox isWifi, isCabo;

    public InsereEstabelecimentoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insere_estabelecimento, container, false);

        NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        getActivity().findViewById(R.id.refresh_button).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        NavigationDrawerUtil.setIsDrawer(false);

        nome = (EditText) view.findViewById(R.id.editTextNome);
        end = (EditText) view.findViewById(R.id.editTextEnd);
        numero = (EditText) view.findViewById(R.id.editTextNum);
        email = (EditText) view.findViewById(R.id.editTextEmail);
        isWifi = (CheckBox) view.findViewById(R.id.checkBoxWifi);
        isCabo = (CheckBox) view.findViewById(R.id.checkBoxCabo);
        senhaDiscplaimer = (TextView) view.findViewById(R.id.textViewSenha);
        senha = (EditText) view.findViewById(R.id.editTextSenha);
        senhaRep = (EditText) view.findViewById(R.id.editTextRepSenha);


        nome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                editText(nome, "Nome da Empresa", hasFocus);

            }
        });
        end.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText(end, "Endereço", hasFocus);
            }
        });
        numero.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                editText(numero, "N°", hasFocus);
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                editText(email, "E-mail", hasFocus);
            }
        });

        isWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    senha.setVisibility(View.VISIBLE);
                    senhaDiscplaimer.setVisibility(View.VISIBLE);
                } else {
                    senha.setVisibility(View.GONE);
                    senhaDiscplaimer.setVisibility(View.GONE);
                    senhaRep.setVisibility(View.GONE);
                    senha.setText("Senha");
                    senha.setTextColor(Color.parseColor("#50000000"));
                    senhaRep.setText("Repetir Senha");
                    senhaRep.setTextColor(Color.parseColor("#50000000"));
                    senha.setInputType(InputType.TYPE_CLASS_TEXT);
                    senhaRep.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        senha.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                senha.setInputType(129);
                editText(senha, "Senha", hasFocus);
                senhaRep.setVisibility(View.VISIBLE);
            }
        });

        senhaRep.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                senhaRep.setInputType(129);


                editText(senhaRep, "Repetir Senha", hasFocus);

            }
        });


        sugerirBtn = (FancyButton) view.findViewById(R.id.botaoCadastrar);

        sugerirBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password;

                if (nome.getText().toString().isEmpty() || nome.getText().toString().equals("Nome da Empresa") ||
                        end.getText().toString().isEmpty() || end.getText().toString().equals("Endereço") ||
                        numero.getText().toString().isEmpty() || numero.getText().toString().equals("N°")) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Erro")
                            .setMessage("Um ou mais campos não foram preenchidos")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.ic_error_red_48dp)
                            .show();

                } else if (!email.getText().toString().contains("@") || !email.getText().toString().contains(".com")) {

                    new AlertDialog.Builder(getActivity())
                            .setTitle("Erro")
                            .setMessage("E-mail inválido")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.ic_error_red_48dp)
                            .show();

                } else if ((!senha.getText().toString().equals("Senha") && !senha.getText().toString().isEmpty()) &&
                        (!senhaRep.getText().toString().equals("Repetir Senha") && !senhaRep.getText().toString().isEmpty())) {


                    if (!senha.getText().toString().equals(senhaRep.getText().toString())) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Erro")
                                .setMessage("As senhas não conferem!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setIcon(R.drawable.ic_error_red_48dp)
                                .show();
                    } else {
                        password = senha.getText().toString();

                        String search = juntaPalavra(nome.getText().toString()) + "+"
                                + juntaPalavra(end.getText().toString()) + "+"
                                + juntaPalavra(numero.getText().toString());

                        new DialogoDeProgresso(getActivity(), "Cadastrando Estabelecimento...");

                        new StoreLocationUtil(getActivity(), search,
                                isWifi.isChecked(),
                                isCabo.isChecked(),
                                password
                        );
                    }
                } else {


                    if (senha.getText().toString().equals("Senha"))
                        password = "";
                    else
                        password = senha.getText().toString();


                    new DialogoDeProgresso(getActivity(), "Cadastrando Estabelecimento...");
                    String search = juntaPalavra(nome.getText().toString()) + "+"
                            + juntaPalavra(end.getText().toString()) + "+"
                            + juntaPalavra(numero.getText().toString());


                    new StoreLocationUtil(getActivity(), search,
                            isWifi.isChecked(),
                            isCabo.isChecked(),
                            password
                    );
                }


            }
        });


        return view;
    }

    private void editText(EditText campo, String texto, boolean hasFocus) {

        if (campo.getText().toString().equals("") && !hasFocus) {
            campo.setText(texto);
            campo.setTextColor(Color.parseColor("#50000000"));
            campo.setInputType(InputType.TYPE_CLASS_TEXT);

        } else if (hasFocus && campo.getText().toString().equals(texto)) {
            campo.setText("");
            campo.setTextColor(Color.BLACK);


        }


    }


    private String juntaPalavra(String word) {
        if (word.contains(" "))
            return word.replace(" ", "");
        return word;
    }


}
