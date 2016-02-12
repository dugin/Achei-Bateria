package eliteapps.SOSBattery.util;

import android.widget.EditText;

/**
 * Created by Rodrigo on 01/02/2016.
 */
public class TextRestrictionsUtil {
    static boolean colocaDoisPontos = true;


    public void getHrFuncRestricao(CharSequence s, EditText ori, EditText dest) {
        int hora1, hora2;
        int minuto1;

        if (s.length() >= 1) {
            hora1 = s.charAt(0) - '0';
            if (hora1 > 2)
                ori.setText("");
        }
        if (s.length() == 2) {
            hora1 = s.charAt(0) - '0';
            hora2 = s.charAt(1) - '0';
            if (hora1 == 2 && hora2 > 3) {
                ori.setText(s.subSequence(0, 1));
                ori.setSelection(1);
            } else {
                if (colocaDoisPontos) {
                    ori.setText(s + ":");
                    ori.setSelection(3);
                    colocaDoisPontos = false;
                } else {
                    ori.setText(s.subSequence(0, 1));
                    ori.setSelection(1);
                    colocaDoisPontos = true;
                }
            }
        }
        if (s.length() >= 4) {
            minuto1 = s.charAt(3) - '0';
            if (minuto1 > 5) {
                ori.setText(s.subSequence(0, 3));
                ori.setSelection(3);
            }
        }
        if (s.length() == 5 && dest != null)
            dest.requestFocus();
        if (s.length() > 5)
            ori.setText(s.subSequence(0, 5));


    }
}

