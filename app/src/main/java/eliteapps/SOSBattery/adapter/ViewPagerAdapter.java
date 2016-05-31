package eliteapps.SOSBattery.adapter;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;

import com.astuetz.PagerSlidingTabStrip;

import eliteapps.SOSBattery.R;
import eliteapps.SOSBattery.fragment.ListaLojasFragment;
import eliteapps.SOSBattery.fragment.MapaFragment;
import eliteapps.SOSBattery.fragment.WifiFragment;


public class ViewPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private final String TAG = this.getClass().getSimpleName();

    int switch_icons[] = {R.drawable.lista, R.drawable.mapa, R.drawable.wifi};

    Location myLocation;
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created
    Context context;

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(Context context, FragmentManager fm, int mNumbOfTabsumb, Location myLocation) {
        super(fm);


        this.context = context;
        this.myLocation = myLocation;

        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {


        Bundle args = new Bundle();
        Fragment f;
        args.putDouble("lat", myLocation.getLatitude());
        args.putDouble("lon", myLocation.getLongitude());

        if (position == 0) // if the position is 0 we are returning the First tab
        {
            f = new ListaLojasFragment();
            f.setArguments(args);

            return f;


        } else if (position == 1)        // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            f = new MapaFragment();
            f.setArguments(args);


            return f;


        } else return new WifiFragment();


    }

    // This method return the titles for the Tabs in the Tab Strip



    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }

    @Override
    public int getPageIconResId(int position) {


        return switch_icons[position];
    }
}