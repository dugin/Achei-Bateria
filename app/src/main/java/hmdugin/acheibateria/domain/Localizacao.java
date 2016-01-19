package hmdugin.acheibateria.domain;

import android.location.Location;

/**
 * Created by Rodrigo on 15/12/2015.
 */
public class Localizacao {
    private static Location location;

    public Localizacao() {

    }

    public Localizacao(Location location) {
        Localizacao.location = location;
    }

    public static Location getLocation() {
        return location;
    }
}
