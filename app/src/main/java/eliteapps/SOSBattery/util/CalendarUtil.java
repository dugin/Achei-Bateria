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
        currentDateandTime = new SimpleDateFormat("dd-MM-yy HH:mm", Locale.FRENCH).format(new Date());
        return currentDateandTime;
    }

    public static String HrFuncionamento(Estabelecimentos lojas) {

        Calendar calendar = Calendar.getInstance();
        String hr_abre = "";
        String hr_fecha = "";
        String resp = "";

        int day = calendar.get(Calendar.DAY_OF_WEEK);

            if (day > 1 && day < 7) {
                hr_abre = lojas.getHr_open()[0].substring(0, 2) + "h às ";
                hr_fecha = lojas.getHr_close()[0].substring(0, 2) + "h";
                resp = hr_abre + "" + hr_fecha;

                if (lojas.getHr_open().length > 3 && lojas.getHr_close().length > 3) {
                    resp += "\n" + lojas.getHr_open()[3].substring(0, 2) + "h às ";
                    resp += lojas.getHr_close()[3].substring(0, 2) + "h";

                }
            } else if (day == 7) {
                hr_abre = lojas.getHr_open()[1].substring(0, 2) + "h às ";
                hr_fecha = lojas.getHr_close()[1].substring(0, 2) + "h";
                resp = hr_abre + "" + hr_fecha;

                if (lojas.getHr_open().length > 4 && lojas.getHr_close().length > 4) {
                    resp += "\n" + lojas.getHr_open()[4].substring(0, 2) + "h às ";
                    resp += lojas.getHr_close()[4].substring(0, 2) + "h";

                }
            } else if (day == 1) {
                hr_abre = lojas.getHr_open()[2].substring(0, 2) + "h às ";
                hr_fecha = lojas.getHr_close()[2].substring(0, 2) + "h";
                resp = hr_abre + "" + hr_fecha;

                if (lojas.getHr_open().length > 5 && lojas.getHr_close().length > 5) {
                    resp += "\n" + lojas.getHr_open()[5].substring(0, 2) + "h às ";
                    resp += lojas.getHr_close()[5].substring(0, 2) + "h";

                }
            }

        if (hr_abre.equals("00h às ") && hr_fecha.equals("00h"))
            return "Não abre";


        return resp;
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
