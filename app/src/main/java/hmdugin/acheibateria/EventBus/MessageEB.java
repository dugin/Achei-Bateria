package hmdugin.acheibateria.eventBus;

import android.location.Location;

import java.util.List;


public class MessageEB {

    private String nomeClasse;
    private int pos;
    private List<Integer> posLojasARemover;
    private Location location;


    public MessageEB(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public List<Integer> getPosLojasARemover() {
        return posLojasARemover;
    }

    public void setPosLojasARemover(List<Integer> posLojasARemover) {
        this.posLojasARemover = posLojasARemover;
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