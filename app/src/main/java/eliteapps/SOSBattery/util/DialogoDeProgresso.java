package eliteapps.SOSBattery.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Rodrigo on 07/01/2016.
 */
public class DialogoDeProgresso {
    private static ProgressDialog dialog;

    public DialogoDeProgresso(Context context) {

        dialog = ProgressDialog.show(context, "",
                "Carregando Lojas...", true);


    }

    public static ProgressDialog getDialog() {
        return dialog;
    }
}
