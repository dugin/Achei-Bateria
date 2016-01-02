package hmdugin.acheibateria.util;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hmdugin.acheibateria.domain.Loja;

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

    public static String HrFuncionamento(Loja lojas) {

        Calendar calendar = Calendar.getInstance();
        String hr_abre = "";
        String hr_fecha = "";

        int day = calendar.get(Calendar.DAY_OF_WEEK);
        try {
            if (day > 1 && day < 7) {
                hr_abre = lojas.getHrOpen().getString(0);
                hr_fecha = lojas.getHrClose().getString(0);
            } else if (day == 7) {
                hr_abre = lojas.getHrOpen().getString(1);
                hr_fecha = lojas.getHrClose().getString(1);
            } else if (day == 1) {
                hr_abre = lojas.getHrOpen().getString(2);
                hr_fecha = lojas.getHrClose().getString(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (hr_abre.equals("00:00") || hr_fecha.equals("00:00"))
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
