package hmdugin.acheibateria.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.activities.MainActivity;
import hmdugin.acheibateria.domain.Localizacao;
import hmdugin.acheibateria.fragment.ListaLojasFragment;

/**
 * Created by Rodrigo on 22/12/2015.
 */
public class ConnectionUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CHECK_SETTINGS = 1;
    private final String TAG = this.getClass().getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;

    public ConnectionUtil(Context context) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void mudaSettingsLocation(final Context context) {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.d(TAG, "LocationSettingsStatusCodes.SUCCESS");


                        comecaListaLojasFragment();

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    (Activity) context,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.

                        break;
                }
            }
        });
    }


    protected Location minhaLocalizacao() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    protected void comecaListaLojasFragment() {
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        new Localizacao(mLocation);

        if (findViewById(R.id.main_layout) != null) {

            if (getFragmentManager().findFragmentByTag("ListaLojasFragment") == null
                    && getFragmentManager().findFragmentByTag("MapaFragment") == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.main_layout, l, "ListaLojasFragment")
                        .addToBackStack("ListaLojasFragment")
                        .commit();
            }

        }

    }


    protected void estaConectado(final Context context) {
        if (!InternetConnectionUtil.isNetworkAvailable(context)) {
            new AlertDialog.Builder(context)
                    .setTitle("Falha na conexão")
                    .setMessage("Verifique se o seu dispositivo está conectado a rede e tente novamente")
                    .setPositiveButton("Reconectar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //// TODO: 18/12/2015 fazer o retry
                            // urgentTodosAdapter.loadObjects();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            onBackPressed();
                        }
                    })
                    .setIcon(R.drawable.no_signal)
                    .show();
        }
    }

}
