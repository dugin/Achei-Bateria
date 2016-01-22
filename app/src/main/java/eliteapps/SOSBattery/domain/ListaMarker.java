package eliteapps.SOSBattery.domain;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rodrigo on 05/01/2016.
 */
public class ListaMarker {
    private static ListaMarker ourInstance = new ListaMarker();
    private List<Marker> listaMarker;

    private ListaMarker() {
        listaMarker = new ArrayList<>();
    }

    public static ListaMarker getInstance() {
        return ourInstance;
    }

    public void addMarker(Marker marker) {
        listaMarker.add(marker);
    }

    public List<Marker> getListaMarker() {
        return listaMarker;
    }
}
