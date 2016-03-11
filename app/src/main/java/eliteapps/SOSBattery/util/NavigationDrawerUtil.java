package eliteapps.SOSBattery.util;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.application.App;
import eliteapps.SOSBattery.fragment.InsereEstabelecimentoFragment;

/**
 * Created by Rodrigo on 26/12/2015.
 */
public class NavigationDrawerUtil {

    static boolean isDrawer;
    private static Drawer drawer;
    private static AccountHeader headerNavigationLeft;
    private final String TAG = this.getClass().getSimpleName();
    Tracker mTracker;
    PrefManager prefManager;


    public NavigationDrawerUtil(final Activity activity, Toolbar toolbar) {


        App application = (App) activity.getApplication();
        mTracker = application.getDefaultTracker();

        prefManager = new PrefManager(activity, "FacebookUtil");

        headerNavigationLeft = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(false)
                .withOnlyMainProfileImageVisible(true)
                .withSelectionListEnabled(false)

                .withHeaderBackground(R.drawable.drawer_bg)

                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {


                        new FacebookUtil(activity);

                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })

                .withTextColor(Color.WHITE)
                .build();

        if (prefManager.getImgURLFacebook() == null || prefManager.getNomeFacebook() == null) {

            headerNavigationLeft.addProfiles(
                    new ProfileDrawerItem().withName("Olá visitante!")

            );

        } else {
            headerNavigationLeft.removeProfile(0);
            headerNavigationLeft.addProfiles(
                    new ProfileDrawerItem().withName(prefManager.getNomeFacebook())
                            .withIcon(prefManager.getImgURLFacebook())
                            .withTextColorRes(R.color.primary)
            );
        }










        drawer = new DrawerBuilder(activity)


                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerNavigationLeft)
                .withSelectedItemByPosition(1)
                .withSliderBackgroundColorRes(R.color.colorBackground)
                .withActionBarDrawerToggleAnimated(false)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {



                        switch (position) {
                            case 2:

                                FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.animator.enter_anim, R.animator.exit_anim,
                                        android.R.animator.fade_in, android.R.animator.fade_out);
                                transaction.replace(R.id.drawer_container, new InsereEstabelecimentoFragment(), "InsereEstabelecimentoFragment");
                                transaction.addToBackStack("MainFragment");
                                transaction.commit();
                                // Build and send an Event.
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Drawer")
                                        .setAction("Suggest Store")
                                        .setLabel("Sugest Store")
                                        .build());
                                break;
                            case 3:

                                // Build and send an Event.
                                mTracker.send(new HitBuilders.EventBuilder()
                                        .setCategory("Drawer")
                                        .setAction("Drawer Send Email")
                                        .setLabel("Drawer Send Email")
                                        .build());

                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "hmdugin@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                activity.startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
                                break;

                            case -1:
                                new FacebookUtil(activity);
                                break;

                            default:
                        }


                        return false;
                    }
                })


                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        Log.d(TAG, "onNavigationClickListener");
                        return false;
                    }
                })
                .build();


        drawer.addItem(new PrimaryDrawerItem()
                .withName("Início")
                .withIcon(R.drawable.inicio)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));


        drawer.addItem(new PrimaryDrawerItem()
                .withName("Sugerir estabelecimento")
                .withIcon(R.drawable.sugest_store)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));


        drawer.addItem(new PrimaryDrawerItem()
                .withName("Fale conosco")
                .withIcon(R.drawable.talk_to_us)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));


/*


        drawer.addItem(new PrimaryDrawerItem()
                .withName("Termos e condições")
                .withIcon(R.drawable.terms)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));



        drawer.addItem(new PrimaryDrawerItem()
                .withName("Sobre nós")
                .withIcon(R.drawable.about_us)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));



 */

        drawer.addStickyFooterItem(new PrimaryDrawerItem()
                .withName("Login com o Facebook")
                .withIcon(R.drawable.ic_social_facebook_box_blue)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));
    }

    public static boolean isDrawer() {
        return isDrawer;
    }

    public static void setIsDrawer(boolean isDrawer) {
        NavigationDrawerUtil.isDrawer = isDrawer;
    }

    public static Drawer getDrawer() {
        return drawer;
    }


    public static AccountHeader getHeaderNavigationLeft() {
        return headerNavigationLeft;
    }
}


