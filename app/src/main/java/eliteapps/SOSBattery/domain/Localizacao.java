package eliteapps.SOSBattery.domain;

import android.location.Location;

/**
 * Created by Rodrigo on 03/03/2016.
 */
public class Localizacao {
    private static Localizacao ourInstance = new Localizacao();

    private Location location;

    private Localizacao() {
    }

    public static Localizacao getInstance() {
        return ourInstance;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
