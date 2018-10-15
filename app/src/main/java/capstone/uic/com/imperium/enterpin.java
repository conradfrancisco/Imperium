package capstone.uic.com.imperium;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class enterpin extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    private Button oldsubmit;
    private Pinview oldpinView;
    private ConstraintLayout constraint;
    private ProgressBar oldpb;
    private String oldpin = " ";
    private String oldcpin = " ";
    private String olduser = " ";
    private boolean monitoringConnectivity = false;
    boolean isConnected = true;
    private String email = "";

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpin);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            email = users.getEmail();

        }
        getCurrentUser();

        oldpinView = (Pinview) findViewById(R.id.oldPinView);
        constraint = (ConstraintLayout) findViewById(R.id.constraintenterpin);
        oldsubmit = (Button) findViewById(R.id.oldsubmit);
        oldpb = (ProgressBar) findViewById(R.id.oldprogressbar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        Intent intent = new Intent(enterpin.this, GeofenceService.class);
        stopService(intent);
        oldsubmit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                try{

                    oldpin = oldpinView.getValue();
                    System.out.println(oldpin);
                    oldpb.setVisibility(View.VISIBLE);
                    confirmUser();

                }

                catch(Exception e){

                    Log.e("onSubmitPin", e.getMessage(), e);
                }
            }
        });


    }

    public void confirmUser(){

        try{

            oldpb.setVisibility(View.GONE);
            DatabaseReference getpin = FirebaseDatabase.getInstance().getReference().child("Users").child(olduser);
            getpin.child("Pin").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        try{

                            oldcpin = dataSnapshot.getValue(String.class);

                            if(oldcpin!=null){

                                if(oldpin.equals(oldcpin)){

                                    Toast.makeText(getApplicationContext(), "Success!, Identity Verified", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(enterpin.this, mainmenu.class);
                                    startActivity(intent);
                                    finish();

                                }

                                else {

                                    Toast.makeText(getApplicationContext(), "PIN does not MATCH!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(enterpin.this, enterpin.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }

                            else {

                                Toast.makeText(getApplicationContext(), "No PIN Retrieved or No Internet Connection", Toast.LENGTH_SHORT).show();

                            }

                        }

                        catch(Exception e){

                            Log.e("Confirm Pin", e.getMessage(), e);

                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        catch(Exception e){

            Log.e("onConfirmUser", e.getMessage(), e);
        }


    }


    public void getCurrentUser(){

        try{

            String[] dab = email.split("@");
            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
            getuser.child(dab[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if( dataSnapshot != null){

                        String oldusers = dataSnapshot.getValue(String.class);
                        try{

                            if(oldusers!=null){

                                olduser = oldusers;
                                System.out.println(olduser);

                            }
                            else{

                                Log.d("getParentUser", "No Parent User Retrieved");

                            }

                        }
                        catch(Exception e){

                            Log.e("Confirm Pin User", e.getMessage(), e);

                        }


                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });

        }

        catch(Exception e){

            Log.e("onConfirmUser", e.getMessage(), e);

        }
    }
    public void onBackPressed(){

        super.onBackPressed();
        finish();

    }

    @Override
    protected void onResume() {

        super.onResume();
        checkConnectivity();

    }

    @Override
    protected void onPause() {

        if (monitoringConnectivity) {

            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;

        }

        super.onPause();

    }

    private ConnectivityManager.NetworkCallback connectivityCallback
            = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;

            Snackbar sn = Snackbar.make(constraint, "Connected!", Snackbar.LENGTH_SHORT);
            sn.show();

        }
        @Override
        public void onLost(Network network) {
            isConnected = false;

            Snackbar sn = Snackbar.make(constraint, "No Connection!", Snackbar.LENGTH_INDEFINITE);
            sn.show();
        }
    };

    private void checkConnectivity() {

        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {

            Snackbar sn = Snackbar.make(constraint, "No Connection!", Snackbar.LENGTH_INDEFINITE);
            sn.show();
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;
        }
        else {

            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;

        }

    }
}
