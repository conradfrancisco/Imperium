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
import android.widget.Toast;

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
    int z = 0;
    int y  = 0;
    Double lat1, lat2, lng1, lng2;
    String currentloc = "", savedloc = "";
    List<String> names = new ArrayList<>();
    private Timer timer, timer1;
    private TimerTask timerTask, timerTask1;
    long oldTime=0, oldTime1=0;

    public GeofenceService() {

    }

    public GeofenceService(Context applicationContext) {

        super();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        super.onStartCommand(intent, flags, startId);
        getCurrentUser();
        startTimer();

        return START_STICKY;
    }

    public void startTimer() {

        startTimer1();
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 10000, 10000);

    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                looper();

            }
        };
    }

    public void startTimer1() {
        timer1 = new Timer();
        initializeTimerTask1();
        timer1.schedule(timerTask1, 30000, 60000);
    }

    public void initializeTimerTask1() {
        timerTask1 = new TimerTask() {
            public void run() {

                getCurrentChildUser();

            }
        };
    }

    public void getCurrentChildUser() {

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
        getuser.child(user).orderByChild("CurrentLocation").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                    if(childSnapshot != null){

                        names.add(childSnapshot.getKey());
                        y = names.size();

                    }

                    else {

                        Toast.makeText(getApplicationContext(), "Please add a child to monitor first!", Toast.LENGTH_SHORT).show();

                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void looper(){

        childuser = names.get(z);
        if (childuser != null){

            System.out.println(childuser);

            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
            getuser.child(user).child(childuser).child("CurrentLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        currentloc = dataSnapshot.getValue(String.class);
                        System.out.println("Current Location: "+currentloc);
                        String split[] = currentloc.split(",");
                        lat1 = Double.parseDouble(split[0]);
                        lng1 = Double.parseDouble(split[1]);
                        System.out.println("Current Latitude: "+lat1+" and Current Longitude: "+lng1);
                        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Children");
                        getuser.child(user).child(childuser).child("SavedLocation").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot != null) {

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
                                    if(z == (y-1)){

                                        z = 0;
                                    }
                                    else {

                                        z++;
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });

        }
        else {

            z = 0;
            Toast.makeText(getApplicationContext(), "No child data has been retrieved!", Toast.LENGTH_SHORT).show();

        }

    }



    public void getCurrentUser(){

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
        getuser.child("currentuser").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    user = dataSnapshot.getValue(String.class);
                    System.out.println("Current Parent User: "+user);
                    getCurrentChildUser();
                }
                else {

                    Toast.makeText(getApplicationContext(), "Please create your account @ Imperium!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }
}