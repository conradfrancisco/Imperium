package capstone.uic.com.imperium;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by Conrad Francisco Jr on 6/12/2018.
 */

public class login extends AppCompatActivity {

    private FirebaseAuth auth;
    boolean isConnected = true;
    private boolean monitoringConnectivity = false;
    private EditText inputuser, inputpassword;
    private ConstraintLayout constraint;
    private Button signup, login, reset;
    private ProgressBar bar;
    private static String ems = " ";
    private static String pinned = " ";
    private static String fpass = " ";
    private static String users = " ";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        constraint = (ConstraintLayout) findViewById(R.id.coordinatorlogin);

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(login.this, mainmenu.class));
            finish();
        }
        Intent intent = new Intent(login.this, GeofenceService.class);
        stopService(intent);
        inputuser = (EditText) findViewById(R.id.user);
        inputpassword = (EditText) findViewById(R.id.pass);
        signup = (Button) findViewById(R.id.regis);
        login = (Button) findViewById(R.id.login);
        reset = (Button) findViewById(R.id.btnForgot);
        bar = (ProgressBar) findViewById(R.id.progressBar);

        FirebaseApp.initializeApp(login.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, register.class));
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, reset.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = inputpassword.getText().toString();
                fpass = password;
                String user = inputuser.getText().toString();
                users = user;

                if(TextUtils.isEmpty(user)) {
                    Toast.makeText(getApplicationContext(), "A Username is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "A Password is required!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isConnected){

                    Toast.makeText(login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    inputuser.setText("");
                    inputpassword.setText("");
                }
                else {

                    getEmail(user);
                }
            }
        });

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
    public void onBackPressed(){

        super.onBackPressed();
        finish();

    }
    private void getEmail(String user){

        DatabaseReference first = FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        first.child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {

                try{

                    if(dataSnapshot1 != null) {

                        String email = dataSnapshot1.getValue(String.class);
                        if(email!=null){

                            ems = email;
                            DatabaseReference second = FirebaseDatabase.getInstance().getReference().child("Users").child(users);
                            second.child("pin").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot2) {

                                    if(dataSnapshot2 != null){

                                        String pin = dataSnapshot2.getValue(String.class);
                                        if(pin!=null){

                                            pinned = pin;

                                            System.out.println(fpass);
                                            System.out.println(ems);
                                            System.out.println(pinned);
                                            bar.setVisibility(View.VISIBLE);
                                            auth.signInWithEmailAndPassword(ems, fpass)
                                                    .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            bar.setVisibility(View.GONE);
                                                            if(task.isSuccessful()){

                                                                if(pinned.equals("New")){
                                                                    System.out.println(pinned);
                                                                    databaseReference.child("Current").child("currentuser").setValue(users);
                                                                    Intent intent = new Intent(login.this, newpin.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                                else if(pinned.equals("Old")){
                                                                    System.out.println(pinned);
                                                                    databaseReference.child("Current").child("currentuser").setValue(users);
                                                                    Intent intent = new Intent(login.this, enterpin.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                            else {

                                                                Toast.makeText(login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                                                inputuser.setText("");
                                                                inputpassword.setText("");
                                                            }

                                                        }
                                                    });

                                        }
                                        else{

                                            Toast.makeText(login.this, "Register First.", Toast.LENGTH_LONG).show();

                                        }


                                    }
                                    else {

                                        Toast.makeText(getApplicationContext(), "NO PIN", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(login.this, register.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }


                            });

                        }
                        else{

                            Toast.makeText(login.this, "No Email Address Found for this certain Username, Register First.", Toast.LENGTH_LONG).show();

                        }

                    }
                    else {

                        Toast.makeText(getApplicationContext(), "Account Not Registered!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(login.this, register.class);
                        startActivity(intent);
                        finish();

                    }
                }

                catch(Exception e){

                    Log.e("getEmail", e.getMessage(), e);

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

