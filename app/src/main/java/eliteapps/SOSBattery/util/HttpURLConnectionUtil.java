package eliteapps.SOSBattery.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import eliteapps.SOSBattery.R;

/**
 * Created by Rodrigo on 28/02/2016.
 */
public class HttpURLConnectionUtil {

    final static int CONNECTION_TIMEOUT = 10000;
    private final String TAG = this.getClass().getSimpleName();
    HttpURLConnection httpURLConnection = null;
    String URL;
    Context mContext;
    private Handler handler;

    public HttpURLConnectionUtil(Context context, String url) {
        this.URL = url;
        this.mContext = context;
    }


    public int connectToURL() {

        try {
            URL u = new URL(URL);
            httpURLConnection = (HttpURLConnection) u.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-length", "0");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setAllowUserInteraction(false);
            httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.setReadTimeout(CONNECTION_TIMEOUT);
            httpURLConnection.connect();

        } catch (MalformedURLException ex) {

            handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (DialogoDeProgresso.getDialog() != null)
                        DialogoDeProgresso.getDialog().dismiss();
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


        } catch (IOException ex) {


            handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (DialogoDeProgresso.getDialog() != null)
                        DialogoDeProgresso.getDialog().dismiss();
                    new AlertDialog.Builder(mContext)
                            .setTitle("Falha na conexão")
                            .setMessage("Verifique se o seu dispositivo está conectado à rede e tente novamente")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.ic_no_signal)
                            .show();
                }
            });
            return -1;

        }
        return 0;
    }

    public String getJSON() {

        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();

            switch (status) {
                case 200:
                case 201:
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    return sb.toString();
            }
        } catch (IOException e1) {
            handler = new Handler(mContext.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(mContext)
                            .setTitle("Falha na conexão")
                            .setMessage("Verifique se o seu dispositivo está conectado à rede e tente novamente")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(R.drawable.ic_no_signal)
                            .show();
                }
            });

        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public String getURL() {

        int status = 0;
        try {
            status = httpURLConnection.getResponseCode();
            switch (status) {
                case 200:
                case 201:

                    return httpURLConnection.getURL().toString();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                httpURLConnection.disconnect();
            } catch (Exception ex) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
}
