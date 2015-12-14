package hmdugin.acheibateria.EventBus;

import android.location.Location;

import hmdugin.acheibateria.domain.Loja;

/**
 * Created by Rodrigo on 11/12/2015.
 */
public class MessageEB {


    private Location location;
    private Loja loja;
    private String classTester;

    public Loja getLoja() {
        return loja;
    }

    public void setLoja(Loja loja) {
        this.loja = loja;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getClassTester() {
        return classTester;
    }

    public void setClassTester(String classTester) {
        this.classTester = classTester;
    }
}


