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

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.domain.Loja;

public class CustomAdapter extends ParseQueryAdapter<ParseObject> {


    private static ParseGeoPoint mGeoPoint;
    private final String TAG = this.getClass().getSimpleName();


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


        // Add and download the image
        ParseImageView todoImage = (ParseImageView) v.findViewById(R.id.imgLoja);
        ParseFile imageFile = lojas.getImg();
        if (imageFile != null) {
            todoImage.setParseFile(imageFile);
            todoImage.loadInBackground();
        }

        // Add the title view
        TextView txtNome = (TextView) v.findViewById(R.id.txtNome);
        txtNome.setText(lojas.getNome());

        TextView txtEnd = (TextView) v.findViewById(R.id.txtEnd);
        String endereco = lojas.getEnd();
        txtEnd.setText(endereco);
        TextView txtBairro = (TextView) v.findViewById(R.id.txtBairro);
        txtBairro.setText(lojas.getBairro());
        TextView txtDist = (TextView) v.findViewById(R.id.txtDist);

        int distancia = (int) (1000 * mGeoPoint.distanceInKilometersTo(lojas.getCoord()));
        double segundos = distancia / 1.2;
        double minutos = segundos / 60;
        if ((int) minutos == 0)
            minutos = 1;
        txtDist.setText(String.format("%d min", (int) minutos));


        return v;
    }


}