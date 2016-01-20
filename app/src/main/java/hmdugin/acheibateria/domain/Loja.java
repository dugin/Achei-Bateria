package hmdugin.acheibateria.domain;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.json.JSONArray;

/**
 * Created by Rodrigo on 10/12/2015.
 */
@ParseClassName("loja")
public class Loja extends ParseObject {

    public String getNome() {
        return getString("nome");
    }

    public void setNome(String nome) {
        put("nome", nome);
    }

    public ParseGeoPoint getCoord() {
        return getParseGeoPoint("coord");
    }

    public void setCoord(ParseGeoPoint coord) {
        put("coord", coord);
    }

    public String getEnd() {
        return getString("end");
    }

    public void setEnd(String end) {
        put("end", end);
    }

    public ParseFile getImg() {
        return getParseFile("img");
    }

    public void setImg(ParseFile img) {
        put("img", img);
    }

    public JSONArray getHrOpen() {
        return getJSONArray("hr_open");
    }

    public JSONArray getHrClose() {
        return getJSONArray("hr_close");
    }

    public String getBairro() {
        return getString("bairro");
    }

    public void setBairro(String bairro) {
        put("bairro", bairro);
    }

    public boolean getIsWifiAvailable() {
        return getBoolean("wifi");
    }


}
