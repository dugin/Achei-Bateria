package eliteapps.SOSBattery.util;

/**
 * Created by Rodrigo on 29/12/2015.
 */
public class MathUtil {


    public static boolean calcIntTempo(String data, String data1) {

        int dia = Integer.parseInt(data.substring(0, 2));
        int dia1 = Integer.parseInt(data1.substring(0, 2));

        int hr = Integer.parseInt(data.substring(9, 11));
        int hr1 = Integer.parseInt(data1.substring(9, 11));

        int min = Integer.parseInt(data.substring(12));
        int min1 = Integer.parseInt(data1.substring(12));

        int tempo, tempo1;
        tempo = hr * 60 + min;
        if (dia == dia1)
            tempo1 = hr1 * 60 + min1;
        else
            tempo1 = 24 * hr1 * 60 + min1;


        return Math.abs(tempo1 - tempo) < 30;

    }
}
