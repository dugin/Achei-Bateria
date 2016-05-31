package eliteapps.SOSBattery.util;

/**
 * Created by Rodrigo on 08/03/2016.
 */
public class FilterDataUtil {

    private static FilterDataUtil ourInstance = new FilterDataUtil();
    boolean wifi;
    boolean restaurante;
    boolean bar;
    boolean loja;
    boolean cabo;

    int progresso;
    String distancia;


    private FilterDataUtil() {
    }

    public static FilterDataUtil getInstance() {
        return ourInstance;
    }

    public void setAll(boolean restaurante, boolean bar, boolean loja, boolean cabo, boolean wifi, int progresso, String distancia) {
        this.cabo = cabo;
        this.wifi = wifi;
        this.restaurante = restaurante;
        this.bar = bar;
        this.loja = loja;

        this.progresso = progresso;
        this.distancia = distancia;
    }

    public boolean isWifi() {
        return wifi;
    }

    public boolean isCabo() {
        return cabo;
    }


    public int getProgresso() {
        return progresso;
    }

    public String getDistancia() {
        return distancia;
    }

    public boolean isRestaurante() {
        return restaurante;
    }

    public boolean isBar() {
        return bar;
    }

    public boolean isLoja() {
        return loja;
    }
}
