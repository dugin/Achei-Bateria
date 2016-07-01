package eliteapps.SOSBattery.util;


import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.activities.MainActivity;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.domain.Cabo;
import eliteapps.SOSBattery.domain.Estabelecimentos;
import eliteapps.SOSBattery.json.Address_components;
import eliteapps.SOSBattery.json.Periods;
import eliteapps.SOSBattery.json.Response;

/**
 * Created by Rodrigo on 26/02/2016.
 */
public class StoreLocationUtil {

    private final String TAG = this.getClass().getSimpleName();
    HttpURLConnectionUtil httpURLConnectionUtil;
    String URL;
    Handler handler;
    Boolean isWifi;
    Boolean isCabo;
    String wifiSenha;
    Context mContext;
    Estabelecimentos e;
    String lat;
    String lon;
    String nomeWifi;

    public StoreLocationUtil(Context context, String search, Boolean isWifi, Boolean isCabo, String wifiSenha, String nomeWifi) {
        this.mContext = context;
        this.isCabo = isCabo;
        this.isWifi = isWifi;
        this.wifiSenha = wifiSenha;
        this.nomeWifi = nomeWifi;


        buildAndInitiateSearchTask(search);
    }


    public void saveToFirebase() {


        final Firebase myFirebaseRef = new Firebase("https://sosbattery-1198.firebaseio.com/estabelecimentosTemp");

        GeoFire geoFire = new GeoFire(new Firebase("https://sosbattery-1198.firebaseio.com/coordenadasTemp"));

        Firebase dbRef = myFirebaseRef.push();
        e.setId(dbRef.getKey());


        dbRef.setValue(e);


        geoFire.setLocation(dbRef.getKey(), new GeoLocation(Double.parseDouble(lat), Double.parseDouble(lon)),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, FirebaseError error) {
                        if (DialogoDeProgresso.getDialog() != null)
                            DialogoDeProgresso.getDialog().dismiss();

                        new AlertDialog.Builder(mContext)
                                .setTitle("Obrigado!")
                                .setMessage("Sugestão enviada com sucesso!")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity mainActivity = (MainActivity) mContext;
                                        CustomViewPager pager = (CustomViewPager) mainActivity.findViewById(R.id.pager);
                                        pager.setCurrentItem(0);
                                        mainActivity.onBackPressed();

                                    }
                                })
                                .setIcon(R.drawable.ic_success)
                                .show();
                    }
                });


    }


    void buildAndInitiateSearchTask(String searchType) {


        StringBuilder searchURL = new StringBuilder();
        searchURL.append("https://maps.googleapis.com/maps/api/place/textsearch/");
        searchURL.append("json?");
        searchURL.append("query=" + searchType);
        searchURL.append("&key=AIzaSyBLudM2nuyHxqpMI_I9kaE0Zj5H3lxmsrg");

        URL = searchURL.toString();
        new PlaceSearchAPITask().execute();
    }


    private class PlaceSearchAPITask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... placesURL) {

            httpURLConnectionUtil = new HttpURLConnectionUtil(mContext, URL);
            int result = httpURLConnectionUtil.connectToURL();

            if (result == -1)
                return null;

            return httpURLConnectionUtil.getJSON();


        }

        @Override
        protected void onPostExecute(String result) {
            try {
                if (result != null) {


                    Log.println(Log.ASSERT, TAG, "" + result);


                    JSONObject resultObject = new JSONObject(result);


                    if (resultObject.getString("status").equals("OK")) {

                        JSONArray jsonArray = resultObject.getJSONArray("results");


                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        if (jsonObject.has("place_id")) {

                            buildAndInitiateSearchTask(jsonObject.getString("place_id"));

                        }


                    } else {
                        if (DialogoDeProgresso.getDialog() != null)
                            DialogoDeProgresso.getDialog().dismiss();

                        handler = new Handler(mContext.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(mContext)
                                        .setTitle("Dados Incorretos")
                                        .setMessage("Nenhum estabelecimento encontrado. Por favor, verifique os dados inseridos")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setIcon(R.drawable.ic_error_red_48dp)
                                        .show();
                            }
                        });

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        void buildAndInitiateSearchTask(String searchType) {


            StringBuilder searchURL = new StringBuilder();
            searchURL.append("https://maps.googleapis.com/maps/api/place/details/");
            searchURL.append("json?");
            searchURL.append("placeid=" + searchType);
            searchURL.append("&key=AIzaSyBLudM2nuyHxqpMI_I9kaE0Zj5H3lxmsrg");
            URL = searchURL.toString();
            new PlaceDetailsSearchAPITask().execute();
        }
    }

    private class PlaceDetailsSearchAPITask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... placesURL) {

            httpURLConnectionUtil = new HttpURLConnectionUtil(mContext, URL);
            httpURLConnectionUtil.connectToURL();

            return httpURLConnectionUtil.getJSON();

        }


        @Override
        protected void onPostExecute(String result) {

            Log.println(Log.ASSERT, TAG, "" + result);

            e = new Estabelecimentos();

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            Response response = gson.fromJson(result, Response.class);
            Address_components[] address = response.getResult().getAddress_components();
            Cabo c = new Cabo(isCabo, true);
            e.setCabo(c);
            e.setWifi_senha(wifiSenha);
            e.setWifi_SSID(nomeWifi);
            String bairro = null;
            String cidade = null;
            e.setWifi(isWifi);
            String estado = null;
            String[] hr_open = {"00:00", "00:00", "00:00"};
            String[] hr_close = {"00:00", "00:00", "00:00"};
            String[] coord = {"", ""};
            String numero = null;
            String nomeLoja;
            String end = null;

            String tipo = null;

            if (response.getResult().getTypes() != null) {
                tipo = response.getResult().getTypes()[0];
                e.setTipo(tipo);
            }

            for (int i = 0; i < address.length; i++) {
                String[] campo = address[i].getTypes();

                for (int j = 0; j < campo.length; j++) {

                    if (campo[j].equals("sublocality") || campo[j].equals("sublocality_level_1"))
                        bairro = address[i].getLong_name();

                    else if (campo[j].equals("administrative_area_level_1")) {
                        estado = address[i].getShort_name();
                        cidade = address[i].getLong_name();
                    } else if (campo[j].equals("street_number")) {
                        numero = address[i].getLong_name();
                    } else if (campo[j].equals("route")) {
                        end = address[i].getLong_name();
                    }

                }
            }
            nomeLoja = response.getResult().getName();

            e.setNome(nomeLoja);
            e.setEnd(end + ", " + numero);

            e.setBairro(bairro);
            e.setEstado(estado);
            e.setCidade(cidade);

            if (response.getResult().getOpening_hours() != null) {
                Periods[] weekday = response.getResult().getOpening_hours().getPeriods();


                for (int i = 0; i < weekday.length; i++) {
                    if (weekday[i].getOpen() != null) {
                        if (weekday[i].getOpen().getDay().equals("0"))
                            hr_open[2] = weekday[i].getOpen().getTime().substring(0, 2) + ":" + weekday[i].getOpen().getTime().substring(2, 4);

                        else if (weekday[i].getOpen().getDay().equals("1"))
                            hr_open[0] = weekday[i].getOpen().getTime().substring(0, 2) + ":" + weekday[i].getOpen().getTime().substring(2, 4);

                        else if (weekday[i].getOpen().getDay().equals("6"))
                            hr_open[1] = weekday[i].getOpen().getTime().substring(0, 2) + ":" + weekday[i].getOpen().getTime().substring(2, 4);
                    }
                    if (weekday[i].getClose() != null) {

                        if (weekday[i].getClose().getDay().equals("0"))
                            hr_close[2] = weekday[i].getClose().getTime().substring(0, 2) + ":" + weekday[i].getClose().getTime().substring(2, 4);

                        else if (weekday[i].getClose().getDay().equals("1"))
                            hr_close[0] = weekday[i].getClose().getTime().substring(0, 2) + ":" + weekday[i].getClose().getTime().substring(2, 4);

                        else if (weekday[i].getClose().getDay().equals("6"))
                            hr_close[1] = weekday[i].getClose().getTime().substring(0, 2) + ":" + weekday[i].getClose().getTime().substring(2, 4);
                    }
                }
            }

            e.setHr_open(hr_open);
            e.setHr_close(hr_close);

            lat = response.getResult().getGeometry().getLocation().getLat();
            lon = response.getResult().getGeometry().getLocation().getLng();

            coord[0] = response.getResult().getGeometry().getLocation().getLat();
            coord[1] = response.getResult().getGeometry().getLocation().getLng();

            e.setCoordenadas(coord);

            DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy - H:m", Locale.FRANCE);
            Date date = new Date();
            dateFormat.setTimeZone(TimeZone.getTimeZone(getTimeZone(TimeZone.getAvailableIDs())));
            e.setCreatedAt(dateFormat.format(date));

            if (response.getResult().getPhotos() != null)
                buildAndInitiateSearchTask(response.getResult().getPhotos()[0].getPhoto_reference());
            else {
                e.setImgURL("");
                saveToFirebase();
            }


        }

        void buildAndInitiateSearchTask(String searchType) {


            StringBuilder searchURL = new StringBuilder();
            searchURL.append("https://maps.googleapis.com/maps/api/place/");
            searchURL.append("photo?maxwidth=200");
            searchURL.append("&photoreference=" + searchType);
            searchURL.append("&key=AIzaSyBLudM2nuyHxqpMI_I9kaE0Zj5H3lxmsrg");
            URL = searchURL.toString();

            new PlacePhotoSearchAPITask().execute();
        }

        private String getTimeZone(String[] TimeZoneID) {

            for (int i = 0; i < TimeZoneID.length; i++) {

                if (TimeZoneID[i].contains("Sao_Paulo"))
                    return TimeZoneID[i];

            }

            return null;
        }
    }

    private class PlacePhotoSearchAPITask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... placesURL) {

            httpURLConnectionUtil = new HttpURLConnectionUtil(mContext, URL);
            httpURLConnectionUtil.connectToURL();

            return httpURLConnectionUtil.getURL();
        }

        @Override
        protected void onPostExecute(String result) {


            e.setImgURL(result);


            saveToFirebase();

        }


    }
}
