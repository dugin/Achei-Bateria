package eliteapps.SOSBattery.eventBus;

import android.location.Location;


public class MessageEB {

    private String nomeClasse;
    private int pos;
    private Location location;

    public MessageEB(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public String getData() {
        return nomeClasse;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}