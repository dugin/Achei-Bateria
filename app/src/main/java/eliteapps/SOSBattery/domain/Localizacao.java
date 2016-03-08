package eliteapps.SOSBattery.domain;

import android.content.Context;
import android.location.Location;

import eliteapps.SOSBattery.util.PrefManager;

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


    public Location localizacaoAppBackground(Context mContext) {

        PrefManager prefManager = new PrefManager(mContext, "MainActivity");
        Double lat, lon;

        if (Localizacao.getInstance().getLocation() == null) {

            if (prefManager.getMinhaCoord() != null) {

                lat = Double.parseDouble(prefManager.getMinhaCoord().substring(0, prefManager.getMinhaCoord().lastIndexOf('_') - 1));
                lon = Double.parseDouble(prefManager.getMinhaCoord().substring(prefManager.getMinhaCoord().lastIndexOf('_') + 1));


            } else {

                lat = -22.982271;
                lon = -43.217286;
            }

        } else {

            lat = location.getLatitude();
            lon = location.getLongitude();

        }

        Location l = new Location("");
        l.setLatitude(lat);
        l.setLongitude(lon);

        return l;

    }
}
