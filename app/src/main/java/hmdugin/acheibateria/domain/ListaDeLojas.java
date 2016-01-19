package hmdugin.acheibateria.domain;

/**
 * Created by Rodrigo on 11/12/2015.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 14/11/2015.
 */
public class ListaDeLojas {

    private static ListaDeLojas ourInstance = new ListaDeLojas();
    private List<Loja> listaDeLojas;

    private ListaDeLojas() {
        listaDeLojas = new ArrayList<Loja>();
    }

    public static ListaDeLojas getInstance() {
        return ourInstance;
    }

    public List<Loja> getListaDeCompras() {
        return listaDeLojas;
    }

    public void setListaDeCompras(List<Loja> loja) {
        this.listaDeLojas = loja;
    }


}
