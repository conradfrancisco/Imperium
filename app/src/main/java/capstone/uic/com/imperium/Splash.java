package capstone.uic.com.imperium;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Conrad Francisco Jr on 2/4/2018.
 */

public class Splash extends AppCompatActivity {

    private GeofenceService geo;
    Intent mServiceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geo = new GeofenceService(this);
        mServiceIntent = new Intent(this, geo.getClass());


        if (!isMyServiceRunning(geo.getClass())) {

            startService(mServiceIntent);

        }

        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }

        return false;
    }

}




