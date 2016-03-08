package eliteapps.SOSBattery.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.crashlytics.android.Crashlytics;
import com.firebase.client.Firebase;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.adapter.ViewPagerAdapter;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.domain.ListaDeEstabelecimentos;
import eliteapps.SOSBattery.domain.ListaMarker;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.GoogleAPIConnectionUtil;
import eliteapps.SOSBattery.util.InternetConnectionUtil;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;
import io.fabric.sdk.android.Fabric;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CHECK_SETTINGS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private final String TAG = this.getClass().getSimpleName();
    boolean doubleBackToExitPressedOnce = false;
    CustomViewPager pager;
    ViewPagerAdapter adapter;
    PagerSlidingTabStrip tabs;
    int Numboftabs = 2;
    ImageButton refreshBtn;
    PrefManager prefManager;
    Tracker mTracker;
    private GoogleAPIConnectionUtil googleAPIConnectionUtil;
    private boolean firstUse = false;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        App application = (App) getApplication();
        mTracker = application.getDefaultTracker();

        Fabric.with(this, new Crashlytics());


        Firebase.setAndroidContext(this);
        prefManager = new PrefManager(MainActivity.this, TAG);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getBoolean("from_notification_low_battery")) {

                    // Build and send an Event.
                    mTracker.send(new HitBuilders.EventBuilder()
                            .setCategory("Notification")
                            .setAction("Notification Low Battery")
                            .setLabel("Low Battery")
                            .build());


                }

            }
        }

        googleAPIConnectionUtil = new GoogleAPIConnectionUtil(this);

        if (!InternetConnectionUtil.isNetworkAvailable(MainActivity.this) &&
                !NotificationUtils.isAppIsInBackground(MainActivity.this))
            teste();



        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
        toolbar.setTitle("");
        setSupportActionBar(toolbar);



        ProgressBar progressBar = (ProgressBar) findViewById(R.id.barraLoading);

        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        refreshBtn = (ImageButton) findViewById(R.id.refresh_button);

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tabs.getVisibility() == View.GONE)
                    tabs.setVisibility(View.VISIBLE);
                pager.removeAllViews();
                googleAPIConnectionUtil.setMinhaLocalizacao(null);
                googleAPIConnectionUtil.startLocationUpdates();
                changeSettings();


                // Build and send an Event.
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Button")
                        .setAction("Refresh")
                        .setLabel("Refresh")
                        .build());
                new DialogoDeProgresso(MainActivity.this, "Carregando lojas...");


                firstUse = false;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                else
                    new MyTask().execute();


            }
        });

        TextView t1 = (TextView) findViewById(R.id.toolbar_title);
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Leelawadee.ttf");
        t1.setTypeface(type);

        new NavigationDrawerUtil(MainActivity.this, toolbar);

    }


    protected void onStart() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getPermission();
        } else {
            googleAPIConnectionUtil.getmGoogleApiClient().connect();

            changeSettings();
        }
        super.onStart();

    }

    protected void onStop() {
        googleAPIConnectionUtil.getmGoogleApiClient().disconnect();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    googleAPIConnectionUtil.getmGoogleApiClient().connect();

                    changeSettings();

                } else {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Erro crítico!")
                            .setMessage("Sem acesso a sua localização o app não funcionará")
                            .setPositiveButton("Configurações", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                    intent.setData(uri);
                                    MainActivity.this.startActivity(intent);

                                }
                            })
                            .setIcon(R.drawable.ic_error_red_48dp)
                            .show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onDestroy() {

        finish();


        System.exit(1);
        super.onDestroy();

    }


    @Override
    protected void onPause() {

        Location location = googleAPIConnectionUtil.minhaLocalizacao();
        if (location != null)
        prefManager.setMinhaCoord(location.getLatitude() + "_" + location.getLongitude());


        super.onPause();


    }


    @Override
    public void onBackPressed() {

        // fecha o teclado
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            NavigationDrawerUtil.setIsDrawer(true);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getFragmentManager().popBackStack();
            if (tabs != null) {
                Log.println(Log.ASSERT, TAG, "tabs != null");
                Log.println(Log.ASSERT, TAG, "Current item: " + pager.getCurrentItem());
                tabs.setViewPager(pager);

                mudaCorTab(pager.getCurrentItem());
            }
            findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);
            findViewById(R.id.filter_list).setVisibility(View.VISIBLE);
            NavigationDrawerUtil.getDrawer().getDrawerLayout().setVisibility(View.VISIBLE);
            NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);



        } else {
            if (pager != null) {
                if (NavigationDrawerUtil.getDrawer().isDrawerOpen())
                    NavigationDrawerUtil.getDrawer().closeDrawer();

                else if (pager.getCurrentItem() == 1)
                    pager.setCurrentItem(0);

                else if (doubleBackToExitPressedOnce) {
                    finish();
                    super.onBackPressed();

                } else {

                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Pressione VOLTAR de novo para sair", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);


                }
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    super.onBackPressed();
                    return;
                } else {
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Pressione VOLTAR de novo para sair", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);

                }

            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {

            case android.R.id.home:
                if (!NavigationDrawerUtil.isDrawer())
                    onBackPressed();
                else if (!NavigationDrawerUtil.getDrawer().isDrawerOpen())
                    NavigationDrawerUtil.getDrawer().openDrawer();

                else
                    NavigationDrawerUtil.getDrawer().closeDrawer();

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

    public void comecaListaLojasFragment() {

        Localizacao.getInstance().setLocation(googleAPIConnectionUtil.minhaLocalizacao());




        if (findViewById(R.id.main_container) != null && !firstUse) {

            ListaDeEstabelecimentos.getInstance().getListaDeEstabelecimentos().clear();
            ListaMarker.getInstance().getListaMarker().clear();
            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new ViewPagerAdapter(MainActivity.this, getFragmentManager(), Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (CustomViewPager) findViewById(R.id.pager);
            pager.setPagingEnabled(true);
            pager.setAdapter(adapter);
            // Assiging the Sliding Tab Layout View
            tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);

            tabs.setViewPager(pager);


            mudaCorTab(pager.getCurrentItem());


            firstUse = true;
        }

    }

    protected void changeSettings() {


        PendingResult<LocationSettingsResult> result = googleAPIConnectionUtil.mudaSettingsLocation();
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {

                final Status status = result.getStatus();
                final LocationSettingsStates locationSettingsStates = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:


                        if (googleAPIConnectionUtil.minhaLocalizacao() != null)
                            comecaListaLojasFragment();
                        else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                new MyTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            else
                                new MyTask().execute();
                        }


                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        new MyTask().execute();

                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        acessoLocationNegada();
                        break;
                    default:
                        break;
                }


        }
    }

    protected void estaConectado() {

        alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Falha na conexão")
                .setMessage("Verifique se o seu dispositivo está conectado à rede e tente novamente")
                .setPositiveButton("Reconectar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(R.drawable.ic_no_signal)
                .show();


    }

    protected void acessoLocationNegada() {
        new AlertDialog.Builder(this)
                .setTitle("Atenção")
                .setMessage("Precisamos da sua localização para acharmos lojas ao seu redor")
                .setPositiveButton("Permitir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        changeSettings();
                    }
                })
                .setIcon(R.drawable.ic_warning_yellow_48dp)
                .show();
    }


    protected void teste() {

        TimerTask mTimerTask = new TimerTask() {

            @Override
            public void run() {


                if (!InternetConnectionUtil.isNetworkAvailable(MainActivity.this)
                        && !NotificationUtils.isAppIsInBackground(MainActivity.this)) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog != null && alertDialog.isShowing())
                                alertDialog.dismiss();

                            estaConectado();
                        }
                    });
                } else if (!NotificationUtils.isAppIsInBackground(MainActivity.this)) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onStart();
                        }
                    });

                }
            }
        };
        Timer mTimer = new Timer();
        mTimer.scheduleAtFixedRate(mTimerTask, 1000, 8000);
    }

    protected void getPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.

        } else {
            googleAPIConnectionUtil.getmGoogleApiClient().connect();

            changeSettings();

        }


    }

    protected void mudaCorTab(final int pos) {
        Field field = null;
        try {
            field = PagerSlidingTabStrip.class.getDeclaredField("tabsContainer");
            field.setAccessible(true);
            LinearLayout tabsContainer = (LinearLayout) field.get(tabs);
            tabsContainer.getChildAt(pos).setSelected(true);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentPageSelected = pos;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                Field field = null;
                try {
                    field = PagerSlidingTabStrip.class.getDeclaredField("tabsContainer");
                    field.setAccessible(true);
                    LinearLayout tabsContainer = (LinearLayout) field.get(tabs);
                    tabsContainer.getChildAt(currentPageSelected).setSelected(false);
                    currentPageSelected = position;
                    tabsContainer.getChildAt(position).setSelected(true);

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            if (!googleAPIConnectionUtil.getmGoogleApiClient().isConnected())
                googleAPIConnectionUtil.getmGoogleApiClient().reconnect();


            while (googleAPIConnectionUtil.minhaLocalizacao() == null) {

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            GoogleAPIConnectionUtil.setLocationChanged(false);

            comecaListaLojasFragment();

        }
    }

}


