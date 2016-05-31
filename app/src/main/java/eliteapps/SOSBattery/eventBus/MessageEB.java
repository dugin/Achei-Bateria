package eliteapps.SOSBattery.eventBus;

import android.location.Location;

import java.util.List;

import eliteapps.SOSBattery.domain.Estabelecimentos;


public class MessageEB {

    private String nomeClasse;
    private int pos;
    private Location location;
    private Estabelecimentos e;
    private boolean isCabo, isWifi, isRestaurant, isBar, isStore;

    private int raio;
    private int difDist;
    private List<Estabelecimentos> estabelecimentosList;

    public MessageEB(String nomeClasse) {
        this.nomeClasse = nomeClasse;
    }

    public List<Estabelecimentos> getEstabelecimentosList() {
        return estabelecimentosList;
    }

    public void setEstabelecimentosList(List<Estabelecimentos> estabelecimentosList) {
        this.estabelecimentosList = estabelecimentosList;
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

    public boolean isRestaurant() {
        return isRestaurant;
    }

    public void setIsRestaurant(boolean isRestaurant) {
        this.isRestaurant = isRestaurant;
    }

    public boolean isBar() {
        return isBar;
    }

    public void setIsBar(boolean isBar) {
        this.isBar = isBar;
    }

    public boolean isStore() {
        return isStore;
    }

    public void setIsStore(boolean isStore) {
        this.isStore = isStore;
    }
}