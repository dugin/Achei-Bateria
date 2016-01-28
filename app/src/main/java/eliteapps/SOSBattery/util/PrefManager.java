package eliteapps.SOSBattery.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Rodrigo on 16/12/2015.
 */
public class PrefManager {
    // Shared pref file name
    private static final String PREF_NAME_1 = "LocationService";
    private static final String PREF_NAME_2 = "MainActivity";
    private static final String DATA_E_HORA = "data";
    private static final String MINHA_COORD = "coord";
    private static final String APP_PAUSED = "pause";
    private static final Set<String> COORD = new TreeSet<>();

    private static final String LATLONG = "latlong";

    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Constructor
    public PrefManager(Context context, String nomeDaClasse) {
        this._context = context;

        if (nomeDaClasse.equals(PREF_NAME_1))
            pref = _context.getSharedPreferences(PREF_NAME_1, PRIVATE_MODE);
        else if (nomeDaClasse.equals(PREF_NAME_2))
            pref = _context.getSharedPreferences(PREF_NAME_2, PRIVATE_MODE);

        editor = pref.edit();
    }

    public Set getCoord() {
        return pref.getStringSet(LATLONG, COORD);
    }

    public void setlat(Set<String> coord) {

        editor.putStringSet(LATLONG, coord);

        editor.commit();
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

    public boolean getVeiodoPause() {

        return pref.getBoolean(APP_PAUSED, false);
    }

    public void setVeiodoPause(boolean trueFalse) {

        editor.putBoolean(APP_PAUSED, trueFalse);
        editor.commit();
    }

    public String getMinhaCoord() {
        return pref.getString(MINHA_COORD, null);
    }

    public void setMinhaCoord(String minhaCoord) {

        editor.putString(MINHA_COORD, minhaCoord);
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