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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent1 = new Intent(Splash.this, GeofenceService.class);
        stopService(intent1);
        Intent intent = new Intent(this, login.class);
        startActivity(intent);
        finish();
    }

}






