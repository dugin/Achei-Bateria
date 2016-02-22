package eliteapps.SOSBattery.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 16/02/2016.
 */
public class ListaDeEstabelecimentos {


    private static ListaDeEstabelecimentos ourInstance = new ListaDeEstabelecimentos();
    private List<Estabelecimentos> listaDeEstabelecimentos;

    private ListaDeEstabelecimentos() {
        listaDeEstabelecimentos = new ArrayList<>();
    }

    public static ListaDeEstabelecimentos getInstance() {
        return ourInstance;
    }

    public List<Estabelecimentos> getListaDeEstabelecimentos() {
        return listaDeEstabelecimentos;
    }

    public void setListaDeEstabelecimentos(List<Estabelecimentos> estabelecimento) {
        this.listaDeEstabelecimentos = estabelecimento;
    }

    public void addEstabelecimento(Estabelecimentos estabelecimento) {
        this.listaDeEstabelecimentos.add(estabelecimento);
    }
}

