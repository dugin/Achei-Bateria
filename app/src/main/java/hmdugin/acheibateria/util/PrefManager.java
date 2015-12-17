package hmdugin.acheibateria.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Rodrigo on 16/12/2015.
 */
public class PrefManager {
    // Shared pref file name
    private static final String PREF_NAME = "acheiBateria";
    private static final String DATA_E_HORA = "data";
    private static final String APP_ABERTO = "aberto";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void pegaDataEHora(String dataEHora) {

        // Storing email in pref
        editor.putString(DATA_E_HORA, dataEHora);

        // commit changes
        editor.commit();
    }

    public boolean getPrimeiraVez() {

        return pref.getBoolean(APP_ABERTO, true);
    }

    public void setPrimeiraVez(boolean trueFalse) {

        editor.putBoolean(APP_ABERTO, trueFalse);
        editor.commit();
    }

    public String getDataEHora() {
        return pref.getString(DATA_E_HORA, null);
    }


    public void apagar() {
        editor.clear();
        editor.commit();
    }
}