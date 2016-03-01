package eliteapps.SOSBattery.util;

import android.app.ProgressDialog;
import android.content.Context;

import eliteapps.SOSBattery.R;

/**
 * Created by Rodrigo on 07/01/2016.
 */
public class DialogoDeProgresso {
    private static ProgressDialog dialog;

    public DialogoDeProgresso(Context context, String texto) {
        dialog = new ProgressDialog(context, R.style.CustomAppCompatAlertDialogStyle);
        dialog = ProgressDialog.show(context, "", texto, true, true);




    }


    public static ProgressDialog getDialog() {
        return dialog;
    }
}
