package hmdugin.acheibateria.util;

/**
 * Created by Rodrigo on 18/01/2016.
 */
public class SeekBarPadding {
    private static SeekBarPadding ourInstance = new SeekBarPadding();
    private static int[] all_padding;
    private static int padding;
    private static int valor;

    private SeekBarPadding() {
        all_padding = new int[11];
    }

    public static SeekBarPadding getInstance() {
        return ourInstance;
    }

    public static int[] getAll_padding() {
        return all_padding;
    }

    public static void setPadding(int padding) {
        SeekBarPadding.padding = padding;
    }

    public static void setValor(int valor) {
        SeekBarPadding.valor = valor;
    }

    public static void setSeekBarPadding() {

        for (int i = 1; i < 11; i++) {
            if (i == 1)
                all_padding[i - 1] = padding - valor;
            else if (i == 10)
                all_padding[i - 1] = padding + valor * (i - 2) - 15;
            else
                all_padding[i - 1] = padding + valor * (i - 2);
        }
    }
}
