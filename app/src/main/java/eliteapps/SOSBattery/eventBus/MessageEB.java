package eliteapps.SOSBattery.eventBus;

import android.location.Location;

import eliteapps.SOSBattery.domain.Estabelecimentos;


public class MessageEB {

    private String nomeClasse;
    private int pos;
    private Location location;
    private Estabelecimentos e;
    private boolean isCabo, isWifi;
    private String categoria;
    private int raio;
    private int difDist;


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

    public boolean isCabo() {
        return isCabo;
    }

    public void setIsCabo(boolean isCabo) {
        this.isCabo = isCabo;
    }

    public boolean isWifi() {
        return isWifi;
    }

    public void setIsWifi(boolean isWifi) {
        this.isWifi = isWifi;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getRaio() {
        return raio;
    }

    public void setRaio(int raio) {
        this.raio = raio;
    }

    public int getDifDist() {
        return difDist;
    }

    public void setDifDist(int difDist) {
        this.difDist = difDist;
    }
}