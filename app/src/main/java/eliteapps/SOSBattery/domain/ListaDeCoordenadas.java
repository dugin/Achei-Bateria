package eliteapps.SOSBattery.domain;

import android.location.Location;

import java.util.HashMap;

/**
 * Created by Rodrigo on 24/02/2016.
 */
public class ListaDeCoordenadas {

    private static ListaDeCoordenadas ourInstance = new ListaDeCoordenadas();

    private HashMap<String, Location> listaDeCoordenadas;

    private ListaDeCoordenadas() {
        listaDeCoordenadas = new HashMap<>();
    }

    public static ListaDeCoordenadas getInstance() {
        return ourInstance;
    }

    public HashMap<String, Location> getListaDeCoordenadas() {
        return listaDeCoordenadas;
    }

    public void setListaDeCoordenadas(HashMap<String, Location> coordenadas) {
        this.listaDeCoordenadas = coordenadas;
    }

    public void addEstabelecimento(String key, Location l) {

        this.listaDeCoordenadas.put(key, l);
    }
}