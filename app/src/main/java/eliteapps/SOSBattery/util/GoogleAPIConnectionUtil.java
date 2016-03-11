package eliteapps.SOSBattery.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

import de.greenrobot.event.EventBus;
import eliteapps.SOSBattery.eventBus.MessageEB;


public class GoogleAPIConnectionUtil implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    static String nome;
    static Location mCurrentLocation;
    private static boolean locationChanged = false;
    private final String TAG = this.getClass().getSimpleName();
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private Activity mActivity;

    public GoogleAPIConnectionUtil() {

    }

    public GoogleAPIConnectionUtil(Context context) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(2500);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();


        } else
            mGoogleApiClient.reconnect();

    }

    public GoogleAPIConnectionUtil(Activity activity) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mActivity = activity;
        if (mGoogleApiClient == null) {

            mGoogleApiClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        } else
            mGoogleApiClient.reconnect();
    }

    public static void setNomeDaClasse(String nome) {
        GoogleAPIConnectionUtil.nome = nome;

    }

    public static boolean isLocationChanged() {
        return locationChanged;
    }

    public static void setLocationChanged(boolean locationChanged) {
        GoogleAPIConnectionUtil.locationChanged = locationChanged;
    }

    @Override
    public void onConnected(Bundle bundle) {

        startLocationUpdates();


    }
    // The rest of this code is all about building the error dialog

    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {

                mResolvingError = true;
                connectionResult.startResolutionForResult(mActivity, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());

            mResolvingError = true;
        }
    }

    public Location getLastKnownLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }



    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(mActivity.getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    public PendingResult<LocationSettingsResult> mudaSettingsLocation() {

        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder()
                        .setAlwaysShow(true)
                        .addLocationRequest(mLocationRequest);


        return LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                builder.build());

    }

    public Location minhaLocalizacao() {
        return mCurrentLocation;
    }

    public void setMinhaLocalizacao(Location l) {
        mCurrentLocation = l;
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    @Override
    public void onLocationChanged(Location location) {


        locationChanged = true;

        mCurrentLocation = location;
        if (nome != null) {
            if (nome.equals("LocationService")) {
                MessageEB m = new MessageEB("LocationService");
                m.setLocation(location);
                EventBus.getDefault().post(m);
            }

        }

        if (mGoogleApiClient.isConnected())
            stopLocationUpdates();


    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            GoogleAPIConnectionUtil googleAPIConnectionUtil = new GoogleAPIConnectionUtil();
            googleAPIConnectionUtil.onDialogDismissed();
        }
    }
}
