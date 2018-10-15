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
import android.widget.TextView;
import android.widget.Toast;
import com.goodiebag.pinview.Pinview;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class newpin extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Button submit, submit2;
    private Pinview pinView, pinView1;
    private TextView instruction;
    private FirebaseAuth auth;
    private String email = "";
    private ProgressBar pb;
    private String pin = " ";
    private String cpin = " ";
    private String user = " ";
    private boolean monitoringConnectivity = false;
    boolean isConnected = true;
    private ConstraintLayout constraint;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newpin);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            email = users.getEmail();

        }
        getCurrentUser();

        pinView = (Pinview) findViewById(R.id.newPinView);
        pinView1 = (Pinview) findViewById(R.id.newPinView1);
        submit = (Button) findViewById(R.id.submit);
        submit2 = (Button) findViewById(R.id.submit2);
        instruction = (TextView) findViewById(R.id.instruction);
        pb = (ProgressBar) findViewById(R.id.progressbar2);
        FirebaseApp.initializeApp(newpin.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Users");
        constraint = (ConstraintLayout) findViewById(R.id.constraintnewpin);

        submit2.setVisibility(View.GONE);
        pinView1.setVisibility(View.GONE);
        Intent intent = new Intent(newpin.this, GeofenceService.class);
        stopService(intent);
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try{

                    pin = pinView.getValue();
                    System.out.println(pin);
                    pb.setVisibility(View.VISIBLE);
                    System.out.println(pinView);
                    submit.setVisibility(View.GONE);
                    pinView.setVisibility(View.GONE);
                    submit2.setVisibility(View.VISIBLE);
                    pinView1.setVisibility(View.VISIBLE);
                    confirmPin();
                }

                catch(Exception e){

                    Log.e("onSubmitPin", e.getMessage(), e);

                }
            }
        });

    }
    public void confirmPin(){

        try{

            instruction.setText("Please CONFIRM your NEW PIN!");
            pb.setVisibility(View.GONE);

            submit2.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    pb.setVisibility(View.VISIBLE);
                    cpin = pinView1.getValue();

                    try{

                        if(pin.equals(cpin)){

                            System.out.println(pin);
                            pb.setVisibility(View.GONE);
                            databaseReference.child(user).child("Pin").setValue(pin);
                            databaseReference.child(user).child("pin").setValue("Old");
                            Toast.makeText(newpin.this, "PIN Registration Successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(newpin.this, mainmenu.class));
                            finish();
                        }

                        else if (!pin.equals(cpin)){

                            Toast.makeText(newpin.this, "PIN does NOT MATCH!!", Toast.LENGTH_SHORT).show();
                            instruction.setText("Please ENTER your NEW PIN");

                        }

                        else {

                            Toast.makeText(newpin.this, "PIN does NOT MATCH!!", Toast.LENGTH_SHORT).show();

                        }

                    }

                    catch(Exception e){

                        Log.e("New Pin Registration", e.getMessage(), e);

                    }


                }
            });

        }

        catch(Exception e){

            Log.e("onConfirmPin", e.getMessage(), e);

        }


    }

    public void getCurrentUser(){

        try{

            String[] nap = email.split("@");
            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
            getuser.child(nap[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if( dataSnapshot != null){

                        String users = dataSnapshot.getValue(String.class);
                        if(users!=null){

                            user = users;
                            System.out.println(user);

                        }
                        else{

                            Toast.makeText(newpin.this, "No Current User Retrieved", Toast.LENGTH_SHORT).show();

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });

        }

        catch(Exception e){

            Log.e("onGetCurrentUser", e.getMessage(), e);

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
