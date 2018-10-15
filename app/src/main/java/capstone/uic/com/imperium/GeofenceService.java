package capstone.uic.com.imperium;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GeofenceService extends Service {

    String value = "";
    String user = "";
    String childuser = "";
    String passval = "";
    String useremail = "";
    private FirebaseAuth auth;
    private String email = "";
    Double lat1, lat2, lng1, lng2;
    String currentloc = "", savedloc = "";
    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;

    public GeofenceService() {

    }

    public GeofenceService(Context applicationContext) {

        super();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        super.onStartCommand(intent, flags, startId);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            email = users.getEmail();

        }
        getCurrentUser();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("GeoRestart");
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    public void startTimer() {

        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 60000, 5000);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {


                getCurrentChildUser();
            }
        };
    }


    public void getCurrentChildUser() {

        try{

            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
            getuser.child(user).orderByChild("CurrentLocation").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        try{

                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                                if(childSnapshot != null){

                                    childuser = childSnapshot.getKey();
                                    getCurrentLoc(childuser);
                                    getCurrentTaskStatus(childuser);

                                }

                                else {

                                    Log.d("ChildMonitor", "Please add a child to monitor first!");


                                }

                            }

                        }

                        catch(Exception e){

                            Log.e("GeoService", e.getMessage(), e);

                        }

                    }
                    else {

                        Log.d("ChildMonitor", "Please add a child to monitor first!");

                    }

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        catch(Exception e){

            Log.e("onRetrieveChildren", e.getMessage(), e);

        }

    }

    public void getCurrentTaskStatus(final String childuser){

        try{

            DatabaseReference status = FirebaseDatabase.getInstance().getReference().child("Users");
            status.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot!=null){

                        try{

                            String tasks = dataSnapshot.child(user).child("Children").child(childuser).child("HardStatus").getValue(String.class);
                            if(tasks!=null){



                                if(tasks!=null && tasks.equals("1")){

                                    notifs1();

                                }

                                else {

                                    Log.d("GeoService", "No Tasks Yet");

                                }
                            }
                        }

                        catch(Exception e){

                            Log.e("GeoService", e.getMessage(), e);

                        }

                    }

                    else {

                        Log.d("GeoService", "No Tasks Yet");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        catch(Exception e){

            Log.e("onRetrieveStatus", e.getMessage(), e);
        }

    }


    public void getCurrentLoc(final String childuser){

        try{

            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
            getuser.child(user).child(childuser).child("CurrentLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        try{

                            currentloc = dataSnapshot.getValue(String.class);
                            if(currentloc!=null){

                                System.out.println("Current Location: " + currentloc);
                                String split[] = currentloc.split(",");
                                lat1 = Double.parseDouble(split[0]);
                                lng1 = Double.parseDouble(split[1]);
                                System.out.println("Current Latitude: " + lat1 + " and Current Longitude: " + lng1);
                                getSavedLoc(childuser);
                            }
                            else{

                                Log.d("onCurrentLoc", "No child current location was retrieved.");

                            }
                        }

                        catch(Exception e){

                            Log.e("GeoService", e.getMessage(), e);
                        }
                    }

                    else {

                        Log.d("onCurrentLoc", "No child current location was retrieved.");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        catch(Exception e){

            Log.e("onCurrentLoc", e.getMessage(), e);
        }
    }

    public void getSavedLoc(String childuser){

        try{

            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
            getuser.child(user).child(childuser).child("SavedLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot != null) {

                        try{

                            if(savedloc!=null){

                                savedloc = dataSnapshot.getValue(String.class);
                                System.out.println("Saved Location: " + savedloc);
                                String split[] = savedloc.split(",");
                                lat2 = Double.parseDouble(split[0]);
                                lng2 = Double.parseDouble(split[1]);
                                System.out.println("Saved Latitude: " + lat2 + " and Saved Longitude: " + lng2);
                                double earthRadius = 6371;
                                double dlat = Math.toRadians(lat2 - lat1);
                                double dlng = Math.toRadians(lng2 - lng1);

                                double a = Math.sin(dlat/2) * Math.sin(dlat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dlng/2) * Math.sin(dlng/2);
                                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

                                double distance = earthRadius * c * 1000;

                                if (distance > 100){

                                    notifs();
                                }

                                System.out.println("The distance is: "+distance);

                            }

                            else {

                                Log.d("SavedLoc", "No child saved location was retrieved.");

                            }
                        }

                        catch (Exception e){

                            Log.e("GeoService", e.getMessage(), e);

                        }

                    }

                    else {

                        Log.d("SavedLoc", "No child saved location was retrieved.");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {



                }
            });

        }

        catch (Exception e){

            Log.e("onSavedLoc", e.getMessage(), e);
        }
    }

    public void getCurrentUser(){

        try{

            String[] dab = email.split("@");
            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
            getuser.child(dab[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        try{

                            if(user!=null){

                                user = dataSnapshot.getValue(String.class);
                                System.out.println("Current Parent User: "+user);
                                startTimer();

                            }

                            else {

                                Toast.makeText(getApplicationContext(), "No Current User retrieved!", Toast.LENGTH_LONG).show();

                            }
                        }

                        catch(Exception e){

                            Log.e("GeoService", e.getMessage(), e);

                        }
                    }
                    else {

                        Toast.makeText(getApplicationContext(), "Please create your account @ Imperium!", Toast.LENGTH_LONG).show();
                        stoptimertask();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });
        }

        catch(Exception e){

            Log.e("onGetUser", e.getMessage(), e);
        }
    }

    public void notifs(){

        IntentFilter ifl = new IntentFilter();
        ifl.addAction("ok");

        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pit = PendingIntent.getActivity(getApplicationContext(), 0, it, 0);
        Context con = getApplicationContext();

        Notification.Builder build;

        build = new Notification.Builder (con)
                .setContentTitle("Imperium Monitoring")
                .setContentText("Your Child might be entering or leaving his/her location")
                .setContentIntent(pit)
                .setSmallIcon(R.drawable.icon)
                .setOngoing(false);


        Notification notifs = build.build();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifs);
    }

    public void notifs1(){

        IntentFilter ifl = new IntentFilter();
        ifl.addAction("ok");

        Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(""));
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pit = PendingIntent.getActivity(getApplicationContext(), 0, it, 0);
        Context con = getApplicationContext();

        Notification.Builder build;

        build = new Notification.Builder (con)
                .setContentTitle("Imperium Monitoring")
                .setContentText("Your Child Reported that he/she has finished the assigned task.")
                .setContentIntent(pit)
                .setSmallIcon(R.drawable.icon)
                .setOngoing(false);


        Notification notifs = build.build();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notifs);
    }

    public void stoptimertask() {
        if ((timer != null)){
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }
}
