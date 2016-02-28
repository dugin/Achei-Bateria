package eliteapps.SOSBattery.domain;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.mikepenz.materialdrawer.model.ProfileDrawerItem;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import eliteapps.SOSBattery.util.NavigationDrawerUtil;

/**
 * Created by Rodrigo on 29/01/2016.
 */
public class FacebookInfo {

    private static String firtsName, lastName;
    private static Bitmap profileImage;


    public FacebookInfo(String id, String name) {

        getFacebookProfilePicture(id);
        FirstandLastName(name);
    }

    public static String getFirtsName() {
        return firtsName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static Bitmap getProfileImage() {
        return profileImage;
    }

    private void FirstandLastName(String name) {
        firtsName = name.substring(0, name.indexOf(' '));
        lastName = name.substring(name.lastIndexOf(' '));

    }

    private void getFacebookProfilePicture(String userID) {
        URL imageURL;
        try {
            imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            HttpURLConnection.setFollowRedirects(true);

            new MyTask(imageURL).execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        private URL imageURL;

        public MyTask(URL imageURL) {
            this.imageURL = imageURL;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                profileImage = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            NavigationDrawerUtil.getHeaderNavigationLeft().removeProfile(0);
            NavigationDrawerUtil.getHeaderNavigationLeft().addProfiles(
                    new ProfileDrawerItem().withName(firtsName + " " + lastName).withIcon(profileImage)
            );


        }
    }


}
