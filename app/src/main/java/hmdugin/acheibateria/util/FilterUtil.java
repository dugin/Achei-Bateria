package hmdugin.acheibateria.util;


import android.util.Log;

import com.parse.ParseGeoPoint;

import java.util.Iterator;
import java.util.List;

import de.greenrobot.event.EventBus;
import hmdugin.acheibateria.domain.ListaDeLojas;
import hmdugin.acheibateria.domain.Localizacao;
import hmdugin.acheibateria.domain.Loja;
import hmdugin.acheibateria.eventBus.MessageEB;

/**
 * Created by Rodrigo on 18/01/2016.
 */
public class FilterUtil {
    private final String TAG = this.getClass().getSimpleName();
    private int distancia;
    private boolean wifi;

    public FilterUtil(int distancia, boolean wifi) {
        this.distancia = distancia;
        this.wifi = wifi;

        if (distancia < 3)
            menorQueTres();
        else
            maiorQueDois();

    }


    private void menorQueTres() {
        Log.println(Log.ASSERT, TAG, "menorQueTres");
        MessageEB m = new MessageEB(TAG);
        List<Loja> lojas = ListaDeLojas.getInstance().getListaDeCompras();
        ParseGeoPoint minhaLocalizacao = new ParseGeoPoint(Localizacao.getLocation().getLatitude(), Localizacao.getLocation().getLongitude());


        Log.println(Log.ASSERT, TAG, "Is wifi on? = " + wifi);
        for (Iterator<Loja> iter = lojas.listIterator(); iter.hasNext(); ) {

            Loja a = iter.next();
            ParseGeoPoint geoPoint = a.getCoord();
            Log.println(Log.ASSERT, TAG, "Distancia " + distancia + " Distancia2: " + minhaLocalizacao.distanceInKilometersTo(geoPoint));
            if (wifi) {
                if (!a.getIsWifiAvailable()) {
                    iter.remove();
                    continue;
                }
            }
            if (minhaLocalizacao.distanceInKilometersTo(geoPoint) > distancia) {
                Log.println(Log.ASSERT, TAG, "Entrou");
                iter.remove();
            }
        }

        ListaDeLojas.getInstance().setListaDeCompras(lojas);

        for (int i = 0; i < lojas.size(); i++)
            Log.println(Log.ASSERT, TAG, "" + lojas.get(i).getNome());

        EventBus.getDefault().post(m);


    }

    private void maiorQueDois() {

    }
}
