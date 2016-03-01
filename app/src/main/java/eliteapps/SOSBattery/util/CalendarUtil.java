package eliteapps.SOSBattery.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import eliteapps.SOSBattery.domain.Estabelecimentos;

/**
 * Created by Rodrigo on 22/12/2015.
 */
public class CalendarUtil {

    private static String currentDateandTime;

    public CalendarUtil() {
        currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());

    }

    public static String getCurrentDateandTime() {
        return currentDateandTime;
    }

    public static String HrFuncionamento(Estabelecimentos lojas) {

        Calendar calendar = Calendar.getInstance();
        String hr_abre = "";
        String hr_fecha = "";

        int day = calendar.get(Calendar.DAY_OF_WEEK);

            if (day > 1 && day < 7) {
                hr_abre = lojas.getHr_open()[0];
                hr_fecha = lojas.getHr_close()[0];
            } else if (day == 7) {
                hr_abre = lojas.getHr_open()[1];
                hr_fecha = lojas.getHr_close()[1];
            } else if (day == 1) {
                hr_abre = lojas.getHr_open()[2];
                hr_fecha = lojas.getHr_close()[2];
            }

        if (hr_abre.equals("00:00") && hr_fecha.equals("00:00"))
            return "Fechado";


        return hr_abre.substring(0, 2) + "h às " + hr_fecha.substring(0, 2) + "h";
    }

    public static String diaDaSemana() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case 1:
                return "Domingo";
            case 2:
                return "Segunda";
            case 3:
                return "Terça";
            case 4:
                return "Quarta";
            case 5:
                return "Quinta";
            case 6:
                return "Sexta";
            case 7:
                return "Sábado";
        }
        return "Erro";
    }
}
