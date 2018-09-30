package capstone.uic.com.imperium;

/**
 * Created by Conrad Francisco Jr on 7/10/2018.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class reset extends AppCompatActivity{

    private EditText inputEmail;
    private Button btnReset;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    boolean isConnected = true;
    private boolean monitoringConnectivity = false;
    private ConstraintLayout constraint;

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        inputEmail = (EditText) findViewById(R.id.email);
        btnReset = (Button) findViewById(R.id.reset);
        progressBar = (ProgressBar) findViewById(R.id.progressb);
        constraint = (ConstraintLayout) findViewById(R.id.constraintreset);
        auth = FirebaseAuth.getInstance();

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Enter your registered Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(reset.this);
                    builder.setTitle("Please confirm action!");
                    builder.setMessage("Are you sure you want to Reset your Password?");
                    builder.setIcon(R.drawable.icon);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            progressBar.setVisibility(View.VISIBLE);
                            auth.sendPasswordResetEmail(email)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(reset.this, "We have sent you instructions on how to reset your Password! Please check your Email Inbox", Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                                inputEmail.setText("");
                                                Intent intent = new Intent(reset.this, login.class);
                                                startActivity(intent);
                                                finish();

                                            } else {
                                                Toast.makeText(reset.this, "Failed to send reset instructions to the Email Address!", Toast.LENGTH_SHORT).show();
                                                inputEmail.setText("");
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            Toast.makeText(getApplicationContext(), "Update User Password Cancelled!", Toast.LENGTH_SHORT).show();
                            inputEmail.setText("");
                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
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
