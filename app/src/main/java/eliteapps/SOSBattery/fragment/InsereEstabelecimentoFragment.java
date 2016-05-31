package eliteapps.SOSBattery.fragment;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.StoreLocationUtil;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsereEstabelecimentoFragment extends Fragment {

    private final String TAG = this.getClass().getSimpleName();
    EditText nome, end, numero, senha, senhaRep, nomeWifi;
    TextView senhaDiscplaimer;
    Tracker mTracker;
    FancyButton sugerirBtn;
    CheckBox isWifi, isCabo;

    public InsereEstabelecimentoFragment() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insere_estabelecimento, container, false);

        NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);

        App application = (App) getActivity().getApplication();
        mTracker = application.getDefaultTracker();



        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        getActivity().findViewById(R.id.refresh_button).setVisibility(View.GONE);
        getActivity().findViewById(R.id.filter_list).setVisibility(View.GONE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        NavigationDrawerUtil.setIsDrawer(false);

        nome = (EditText) view.findViewById(R.id.editTextNome);
        end = (EditText) view.findViewById(R.id.editTextEnd);
        numero = (EditText) view.findViewById(R.id.editTextNum);

        isWifi = (CheckBox) view.findViewById(R.id.checkBoxWifi);
        isCabo = (CheckBox) view.findViewById(R.id.checkBoxCabo);
        senhaDiscplaimer = (TextView) view.findViewById(R.id.textViewSenha);
        senha = (EditText) view.findViewById(R.id.editTextSenha);
        senhaRep = (EditText) view.findViewById(R.id.editTextRepSenha);
        nomeWifi = (EditText) view.findViewById(R.id.editTextNomeWifi);


        mudaFocoEditText(nome, end);
        mudaFocoEditText(end, numero);
        mudaFocoEditText(nomeWifi, senha);

        denyCopyPaste(nome);
        denyCopyPaste(end);
        denyCopyPaste(numero);
        denyCopyPaste(senha);
        denyCopyPaste(senhaRep);
        denyCopyPaste(nomeWifi);




        nome.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                editText(nome, "Nome da Empresa", hasFocus);

            }
        });

        nomeWifi.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                editText(nomeWifi, "Nome do Wifi", hasFocus);

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


        isWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    nomeWifi.setVisibility(View.VISIBLE);
                    senha.setVisibility(View.VISIBLE);
                    senhaDiscplaimer.setVisibility(View.VISIBLE);
                } else {
                    nomeWifi.setVisibility(View.GONE);
                    senha.setVisibility(View.GONE);
                    senhaDiscplaimer.setVisibility(View.GONE);
                    senhaRep.setVisibility(View.GONE);

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

                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Button")
                        .setAction("Suggest Store")
                        .setLabel("Sugest Store button")
                        .build());

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

                } else if (isWifi.isChecked()) {

                    if (nomeWifi.getText().toString().equals("Nome do Wifi")) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Erro")
                                .setMessage("O campo nome do wifi não pode ficar em branco")
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
                                password,
                                nomeWifi.getText().toString()
                        );
                    }
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
                            password,
                            nomeWifi.getText().toString()
                    );
                }


            }
        });


        return view;
    }

    private void editText(EditText campo, String texto, boolean hasFocus) {


        if (hasFocus && campo.getText().toString().equals(texto)) {
            campo.setText("");
            campo.setTextColor(Color.BLACK);
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        }


    }

    private void denyCopyPaste(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public void onDestroyActionMode(ActionMode mode) {
            }

            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }
        });
    }


    private void mudaFocoEditText(EditText atual, final EditText proximo) {

        atual.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    proximo.requestFocus();
                    return true;

                }

                return false;

            }


        });
    }


    private String juntaPalavra(String word) {
        if (word.contains(" "))
            return word.replace(" ", "");
        return word;
    }


}
