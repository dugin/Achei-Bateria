package eliteapps.SOSBattery.activities;

import android.Manifest;
import android.app.Activity;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.parse.ParseAnalytics;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.adapter.CustomViewPager;
import eliteapps.SOSBattery.adapter.ViewPagerAdapter;
import eliteapps.SOSBattery.domain.ListaDeLojas;
import eliteapps.SOSBattery.domain.ListaMarker;
import eliteapps.SOSBattery.domain.Localizacao;
import eliteapps.SOSBattery.fragment.LoginFragment;
import eliteapps.SOSBattery.util.DialogoDeProgresso;
import eliteapps.SOSBattery.util.GoogleAPIConnectionUtil;
import eliteapps.SOSBattery.util.InternetConnectionUtil;
import eliteapps.SOSBattery.util.NavigationDrawerUtil;
import eliteapps.SOSBattery.util.NotificationUtils;
import eliteapps.SOSBattery.util.PrefManager;


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
    Toolbar toolbar;
    private GoogleAPIConnectionUtil googleAPIConnectionUtil;
    private boolean firstUse = false;
    private AlertDialog alertDialog;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(MainActivity.this, TAG);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                if (getIntent().getExtras().getBoolean("from_notification_low_battery")) {
                    ParseAnalytics.trackEventInBackground("Sem_Bateria_Clicado");

                }

            }
        }

        googleAPIConnectionUtil = new GoogleAPIConnectionUtil(this);

        if (!InternetConnectionUtil.isNetworkAvailable(MainActivity.this) &&
                !NotificationUtils.isAppIsInBackground(MainActivity.this))
            teste();
        ParseAnalytics.trackAppOpenedInBackground(getIntent());


        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        toolbar = (Toolbar) findViewById(R.id.app_bar); // Attaching the layout to the toolbar object
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
                ParseAnalytics.trackEventInBackground("Refresh");
                new DialogoDeProgresso(MainActivity.this);


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

    public boolean onCreateOptionsMenu(Menu menu) {


        menu.add("Fale Conosco").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ParseAnalytics.trackEventInBackground("Menu_Email");

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "hmdugin@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
                return false;
            }
        });


        return true;
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
                            .setIcon(R.drawable.erro)
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
    protected void onResume() {

        if (googleAPIConnectionUtil.minhaLocalizacao() == null && prefManager.getVeiodoPause()) {

            if (googleAPIConnectionUtil.getLastKnownLocation() != null)
                new Localizacao(googleAPIConnectionUtil.getLastKnownLocation());

            else {
                if (prefManager.getMinhaCoord() != null) {
                    Double lat = Double.parseDouble(prefManager.getMinhaCoord().substring(0, prefManager.getMinhaCoord().lastIndexOf('_') - 1));
                    Double lon = Double.parseDouble(prefManager.getMinhaCoord().substring(prefManager.getMinhaCoord().lastIndexOf('_') + 1));

                    Location minhaLocalizacao = new Location("");
                    minhaLocalizacao.setLatitude(lat);
                    minhaLocalizacao.setLongitude(lon);

                    new Localizacao(minhaLocalizacao);
                }


            }

            prefManager.apagar();

        }


        super.onResume();
    }

    @Override
    protected void onPause() {


        prefManager.setVeiodoPause(true);
        Location location = googleAPIConnectionUtil.minhaLocalizacao();
        if (location != null)
            prefManager.setMinhaCoord(location.getLatitude() + "_" + location.getLongitude());

        super.onPause();


    }


    @Override
    public void onBackPressed() {
        Log.println(Log.ASSERT, TAG, "" + getFragmentManager().getBackStackEntryCount());

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            NavigationDrawerUtil.setIsDrawer(true);
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getFragmentManager().popBackStack();
            if (tabs != null)
                tabs.setViewPager(pager);
            NavigationDrawerUtil.getDrawer().getDrawerLayout().setVisibility(View.VISIBLE);
            NavigationDrawerUtil.getDrawer().getActionBarDrawerToggle().setDrawerIndicatorEnabled(true);



        } else {
            if (pager != null) {
                if (pager.getCurrentItem() == 1)
                    pager.setCurrentItem(0);
                else if (doubleBackToExitPressedOnce) {
                    finish();
                    ListaDeLojas.getInstance().getListaDeCompras().clear();
                    super.onBackPressed();

                } else {
                    if (NavigationDrawerUtil.getDrawer().isDrawerOpen())
                        NavigationDrawerUtil.getDrawer().closeDrawer();
                    else {
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
            } else {
                if (doubleBackToExitPressedOnce) {
                    finish();
                    ListaDeLojas.getInstance().getListaDeCompras().clear();
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

        new Localizacao(googleAPIConnectionUtil.minhaLocalizacao());


        if (findViewById(R.id.main_layout) != null && !firstUse) {

            ListaDeLojas.getInstance().getListaDeCompras().clear();
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


            mudaCorTab();


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

        final LoginFragment fragment = (LoginFragment) getFragmentManager().findFragmentById(R.id.login_fragment);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

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
                .setIcon(R.drawable.no_signal)
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
                .setIcon(R.drawable.attention)
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

    protected void mudaCorTab() {
        Field field = null;
        try {
            field = PagerSlidingTabStrip.class.getDeclaredField("tabsContainer");
            field.setAccessible(true);
            LinearLayout tabsContainer = (LinearLayout) field.get(tabs);
            tabsContainer.getChildAt(0).setSelected(true);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int currentPageSelected = 0;

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


            while (true) {
                if (googleAPIConnectionUtil.minhaLocalizacao() != null)
                    break;
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


