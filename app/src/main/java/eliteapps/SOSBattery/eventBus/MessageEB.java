package eliteapps.SOSBattery.eventBus;

import android.location.Location;

import eliteapps.SOSBattery.domain.Estabelecimentos;


public class MessageEB {

    private String nomeClasse;
    private int pos;
    private Location location;
    private Estabelecimentos e;

    public MessageEB(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public Estabelecimentos getE() {
        return e;
    }

    public void setE(Estabelecimentos e) {
        this.e = e;
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