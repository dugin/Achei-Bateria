package eliteapps.SOSBattery.util;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.fragment.LoginFragment;

/**
 * Created by Rodrigo on 26/12/2015.
 */
public class NavigationDrawerUtil {

    static boolean isDrawer;
    private static Drawer drawer;
    private static AccountHeader headerNavigationLeft;
    private final String TAG = this.getClass().getSimpleName();
    PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("home");
    SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName("settings");

    public NavigationDrawerUtil(final Activity activity, Toolbar toolbar) {

        headerNavigationLeft = new AccountHeaderBuilder()
                .withActivity(activity)
                .withCompactStyle(true)
                .withOnlyMainProfileImageVisible(true)
                .withSelectionListEnabled(false)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(
                        new ProfileDrawerItem().withName("Visitante")
                )
                .withOnAccountHeaderProfileImageListener(new AccountHeader.OnAccountHeaderProfileImageListener() {
                    @Override
                    public boolean onProfileImageClick(View view, IProfile profile, boolean current) {


                        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();

                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack so the user can navigate back
                        transaction.replace(R.id.drawer_container, new LoginFragment(), "LoginFragment");
                        transaction.addToBackStack("MainFragment");

                        // Commit the transaction
                        transaction.commit();
                        return false;
                    }

                    @Override
                    public boolean onProfileImageLongClick(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withTextColor(Color.WHITE)
                .build();

        drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withRootView(R.id.drawer_container)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withSelectedItem(0)
                .withActionBarDrawerToggle(true)
                .withAccountHeader(headerNavigationLeft)

                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View view) {
                        Log.d(TAG, "onNavigationClickListener");
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        activity.findViewById(R.id.refresh_button).setVisibility(View.GONE);
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        activity.findViewById(R.id.refresh_button).setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {


                    }
                })
                .build();


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


