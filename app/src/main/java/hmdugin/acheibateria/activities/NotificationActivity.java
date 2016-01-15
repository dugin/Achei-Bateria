package hmdugin.acheibateria.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.parse.ParseAnalytics;

import hmdugin.acheibateria.R;
import hmdugin.acheibateria.fragment.CarregueiFragment;

public class NotificationActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        if (getIntent() != null)
            if (getIntent().getExtras() != null)
                if (getIntent().getExtras().getBoolean("from_notification_charging")) {
                    ParseAnalytics.trackEventInBackground("Carregando_Clicado");
                    Log.println(Log.ASSERT, TAG, "ParseAnalytics Carregando_Clicado");
                }

        getFragmentManager().beginTransaction()
                .add(R.id.notification_layout, new CarregueiFragment())
                .addToBackStack(null)
                .commit();


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.home:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case R.id.homeAsUp:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            case android.R.id.home:
                startActivity(new Intent(NotificationActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;

    }

}
