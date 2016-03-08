package eliteapps.SOSBattery.util;

/**
 * Created by Rodrigo on 08/03/2016.
 */
public class FilterDataUtil {

    private static FilterDataUtil ourInstance = new FilterDataUtil();
    boolean wifi;
    boolean cabo;
    int categoria;
    int progresso;
    String distancia;

    private FilterDataUtil() {
    }

    public static FilterDataUtil getInstance() {
        return ourInstance;
    }

    public void setAll(boolean cabo, boolean wifi, int categoria, int progresso, String distancia) {
        this.cabo = cabo;
        this.wifi = wifi;
        this.categoria = categoria;
        this.progresso = progresso;
        this.distancia = distancia;
    }

    public boolean isWifi() {
        return wifi;
    }

    public boolean isCabo() {
        return cabo;
    }

    public int getCategoria() {
        return categoria;
    }

    public int getProgresso() {
        return progresso;
    }

    public String getDistancia() {
        return distancia;
    }
}
