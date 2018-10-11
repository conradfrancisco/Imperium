package capstone.uic.com.imperium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Conrad Francisco Jr on 6/1/2018.
 */
public class register extends AppCompatActivity {

    private FirebaseAuth auth;
    private ConstraintLayout constraint;
    private EditText inputuser, inputemail, inputpassword, inputcpass, inputname;
    private Button register, login1;
    private ProgressBar progressBar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private boolean monitoringConnectivity = false;
    boolean isConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        inputuser = (EditText) findViewById(R.id.username);
        inputemail = (EditText) findViewById(R.id.emailadd);
        inputpassword = (EditText) findViewById(R.id.pass);
        inputcpass = (EditText) findViewById(R.id.cpass);
        inputname = (EditText) findViewById(R.id.fullname);
        register = (Button) findViewById(R.id.register);
        login1 = (Button) findViewById(R.id.log1);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        constraint = (ConstraintLayout) findViewById(R.id.constraintsignup);

        FirebaseApp.initializeApp(register.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        login1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputemail.getText().toString().trim();
                final String user = inputuser.getText().toString().trim();
                final String password = inputpassword.getText().toString().trim();
                final String cpass = inputcpass.getText().toString().trim();
                final String full = inputname.getText().toString().trim();

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(user) && TextUtils.isEmpty(password) && TextUtils.isEmpty(cpass)) {
                    Toast.makeText(getApplicationContext(), "Please Enter some Valid Information!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please Enter a Valid Email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(user)) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Username!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please Enter your Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                else if (TextUtils.isEmpty(cpass)) {
                    Toast.makeText(getApplicationContext(), "Please confirm your Password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password is too short, Enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getApplicationContext(), "Enter a Valid Email Address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
                builder.setTitle("Please confirm action!");
                builder.setMessage("Are all the information provided True and Correct?");
                builder.setIcon(R.drawable.icon);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        try{

                            if (password.equals(cpass)) {

                                progressBar.setVisibility(View.VISIBLE);
                                auth.createUserWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(register.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressBar.setVisibility(View.GONE);

                                                if(task.getException() instanceof FirebaseAuthUserCollisionException) {

                                                    Toast.makeText(register.this, "Account is already registered!", Toast.LENGTH_SHORT).show();
                                                    inputcpass.setText(null);
                                                    inputemail.setText(null);
                                                    inputpassword.setText(null);
                                                    inputuser.setText(null);
                                                    inputname.setText(null);

                                                }
                                                else if(task.isSuccessful()) {


                                                    createNewUser(task.getResult().getUser(), user, full);
                                                    auth.signOut();
                                                    Toast.makeText(register.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(register.this, login.class));
                                                    finish();


                                                }
                                            }
                                        });
                            }
                            else
                            {

                                Toast.makeText(getApplicationContext(), "Passwords do not Match!", Toast.LENGTH_SHORT).show();
                                inputemail.setText(null);
                                inputuser.setText(null);
                                inputname.setText(null);
                                inputcpass.setText(null);
                                inputpassword.setText(null);


                            }
                        }

                        catch(Exception e){

                            Log.e("Registration", e.getMessage(), e);

                        }

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        Toast.makeText(getApplicationContext(), "Registration Cancelled!", Toast.LENGTH_SHORT).show();
                        inputcpass.setText(null);
                        inputname.setText(null);
                        inputemail.setText(null);
                        inputpassword.setText(null);
                        inputuser.setText(null);

                    }
                });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });

    }

    private void createNewUser(FirebaseUser userFromRegistration, String name, String full) {

        String username = name;
        String email = userFromRegistration.getEmail();
        User u = new User();
        u.setEmail(email);
        u.setFullname(full);
        u.setUsername(username);
        u.setPin("New");

        databaseReference.child("Users").child(username).setValue(u);

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
