package hmdugin.acheibateria.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import org.json.JSONException;

import java.util.Calendar;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.ListaDeLojas;
import hmdugin.acheibateria.domain.Loja;

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {


    private static ParseGeoPoint mGeoPoint;
    private final String TAG = this.getClass().getSimpleName();
    private TextView txtEnd, txtNome, txtDist, txtHrFunc, txtBairro;
    private ParseImageView imgLoja;

    public CustomAdapter(Context context, final ParseGeoPoint geoPoint) {

        // Use the QueryFactory to construct a PQA that will only show
        // Todos marked as high-pri
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("loja");
                query.whereWithinKilometers("coord", geoPoint, 2);
                return query;
            }
        });
        mGeoPoint = geoPoint;
    }

    // Customize the layout by overriding getItemView
    @Override
    public View getItemView(ParseObject object, View v, ViewGroup parent) {
        if (v == null) {
            v = View.inflate(getContext(), R.layout.content_lista_lojas, null);
        }

        super.getItemView(object, v, parent);
        Loja lojas = (Loja) object;
        ListaDeLojas listaDeLojas = ListaDeLojas.getInstance();
        listaDeLojas.setListaDeCompras(lojas);

        // Add and download the image
        imgLoja = (ParseImageView) v.findViewById(R.id.imgLoja);
        ParseFile imageFile = lojas.getImg();
        if (imageFile != null) {
            imgLoja.setParseFile(imageFile);
            imgLoja.loadInBackground();
        }

        // Add the title view
        TextView txtNome = (TextView) v.findViewById(R.id.txtNome);
        txtNome.setText(lojas.getNome());

        txtEnd = (TextView) v.findViewById(R.id.txtEnd);
        String endereco = lojas.getEnd();
        txtEnd.setText(endereco);
        txtBairro = (TextView) v.findViewById(R.id.txtBairro);
        txtBairro.setText(lojas.getBairro());
        txtDist = (TextView) v.findViewById(R.id.txtDist);

        double distanciaKm = mGeoPoint.distanceInKilometersTo(lojas.getCoord());
        int minutos = calculaTempoMin(distanciaKm);
        txtDist.setText(String.format("%d min", (int) minutos));


        txtHrFunc = (TextView) v.findViewById(R.id.txtHrFunc);
        txtHrFunc.setText(HrFuncionamento(lojas));

        return v;
    }


    private String HrFuncionamento(Loja lojas) {

        Calendar calendar = Calendar.getInstance();
        String hr_abre = "";
        String hr_fecha = "";

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        try {
            if (day > 1 && day < 7) {
                hr_abre = lojas.getHrOpen().getString(0);
                hr_fecha = lojas.getHrClose().getString(0);
            } else if (day == 7) {
                hr_abre = lojas.getHrOpen().getString(1);
                hr_fecha = lojas.getHrClose().getString(1);
            } else if (day == 1) {
                hr_abre = lojas.getHrOpen().getString(2);
                hr_fecha = lojas.getHrClose().getString(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hr_abre.equals("00:00") || hr_fecha.equals("00:00"))
            return "Fechado";

        return hr_abre + " - " + hr_fecha;
    }

    private int calculaTempoMin(double distanciaKm) {
        int distanciaM = (int) (1000 * distanciaKm);
        double segundos = distanciaM / 1.2;
        double minutos = segundos / 60;
        if ((int) minutos == 0)
            minutos = 1;
        return (int) minutos;

    }


}