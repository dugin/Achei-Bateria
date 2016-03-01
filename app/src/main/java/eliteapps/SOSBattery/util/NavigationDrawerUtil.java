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

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.fragment.InsereEstabelecimentoFragment;

/**
 * Created by Rodrigo on 26/12/2015.
 */
public class NavigationDrawerUtil {

    static boolean isDrawer;
    private static Drawer drawer;
    private static AccountHeader headerNavigationLeft;
    private final String TAG = this.getClass().getSimpleName();

    public NavigationDrawerUtil(final Activity activity, Toolbar toolbar) {

        headerNavigationLeft = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(true)
                .withOnlyMainProfileImageVisible(true)
                .withSelectionListEnabled(false)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(
                        new ProfileDrawerItem().withName("Olá visitante")
                )

                .withTextColor(Color.WHITE)
                .build();

        drawer = new DrawerBuilder(activity)

                .withRootView(R.id.main_container)
                .withActionBarDrawerToggleAnimated(true)
                .withToolbar(toolbar)
                .withAccountHeader(headerNavigationLeft)


                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

                        switch (position) {
                            case 2:
                                transaction.replace(R.id.drawer_container, new InsereEstabelecimentoFragment(), "InsereEstabelecimentoFragment");
                                transaction.addToBackStack("MainFragment");
                                transaction.commit();
                                break;
                            case 3:
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                        "mailto", "hmdugin@gmail.com", null));
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Sugestão/Comentário para SOS Battery");
                                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                                activity.startActivity(Intent.createChooser(emailIntent, "Enviando Email..."));
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

        drawer.addItem(new PrimaryDrawerItem()
                .withName("Termos e condições")
                .withIcon(R.drawable.terms)
                .withTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/Leelawadee.ttf")));

        drawer.addItem(new PrimaryDrawerItem()
                .withName("Sobre nós")
                .withIcon(R.drawable.about_us)
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


