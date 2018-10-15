package capstone.uic.com.imperium;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class mainmenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    ProfileFragment.OnFragmentInteractionListener,
                    AddChildFragment.OnFragmentInteractionListener,
                    GeofencingFragments.OnFragmentInteractionListener,
                    ChildDeviceFragment.OnFragmentInteractionListener,
                    ChildTasksFragment.OnFragmentInteractionListener,
                    ChildActivitiesFragment.OnFragmentInteractionListener
    {

    private String user = " ";
    private String name = " ";
    private String email = " ";
    private String urladd = " ";
    private String useremail = "";
    private CircleImageView imageViewNav;
    private int image = 0;
    private TextView fname,femail;
    private FirebaseAuth auth;
    private int clickedNavItem = 0;
    private DrawerLayout drawer;
    private GeofenceService geo;
    Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);
        geo = new GeofenceService(this);
        mServiceIntent = new Intent(this, geo.getClass());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            useremail = users.getEmail();

        }
        getCurrentUser();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        fname = (TextView) header.findViewById(R.id.username);
        femail = (TextView) header.findViewById(R.id.useremail);
        imageViewNav = (CircleImageView)header.findViewById(R.id.imageViewNav);

        if (!isMyServiceRunning(geo.getClass())) {

            startService(mServiceIntent);

        }

        ProfileFragment p = new ProfileFragment();
        FragmentManager f = getSupportFragmentManager();
        f.beginTransaction().replace(R.id.mainLayout, p).commit();


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

    @Override
    public void onStart(){

        super.onStart();

    }

    @Override
    protected void onDestroy() {

        stopService(mServiceIntent);
        super.onDestroy();

    }

    public String getMyUser() {

        return user;

    }


    @Override
    public void onFragmentInteraction(Uri uri){

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.profile) {

            ProfileFragment p = new ProfileFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

        }

        else if (id == R.id.add){

            AddChildFragment p = new AddChildFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

        }

        else if (id == R.id.device){

            ChildDeviceFragment p = new ChildDeviceFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

        }

        else if (id == R.id.activities) {

            ChildActivitiesFragment p = new ChildActivitiesFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

        }

        else if (id == R.id.tasks) {

            ChildTasksFragment p = new ChildTasksFragment();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();

        }

        else if (id == R.id.location) {

            GeofencingFragments p = new GeofencingFragments();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.mainLayout, p).addToBackStack(null).commit();


        }

        else if (id == R.id.logout){

            AlertDialog.Builder builder = new AlertDialog.Builder(mainmenu.this);
            builder.setTitle("Please confirm action!");
            builder.setMessage("Are you sure you want to Logout?");
            builder.setIcon(R.drawable.icon);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    signOut();

                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                    Toast.makeText(getApplicationContext(), "Cancelled!", Toast.LENGTH_SHORT).show();


                }
            });
            android.support.v7.app.AlertDialog alert = builder.create();
            alert.show();

        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void getProfile(){

        try{

            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Users").child(user);
            getuser.child("ProfilePicture").child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        String urladds = dataSnapshot.getValue(String.class);
                        try{

                            if(urladds!=null){

                                urladd = urladds;
                                Glide.with(mainmenu.this).load(urladd).into(imageViewNav);

                            }
                            else{

                                Log.d("getProfile", "No Profile was retrieved");

                            }

                        }

                        catch(Exception e){

                            Log.e("onGetProfile", e.getMessage(), e);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });
        }

        catch(Exception e){

            Log.e("onGetProfile", e.getMessage(), e);

        }

    }

    public void getCurrentUser(){

        try{

            final String[] app = useremail.split("@");
            System.out.println(app[0]);
            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
            getuser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        String users = dataSnapshot.child(app[0]).getValue(String.class);
                        try{

                            if(users!=null){

                                user = users;
                                getName();

                            }

                            else {

                                Log.d("onGetCurrent", "No Current User");

                            }

                        }

                        catch(Exception e){

                            Log.e("onGetCurrent", e.getMessage(), e);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });
        }

        catch(Exception e){

            Log.e("onGetCurrent", e.getMessage(), e);

        }


    }

    private void getName(){

        try{

            DatabaseReference getname = FirebaseDatabase.getInstance().getReference().child("Users");
            getname.child(user).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        String namea = dataSnapshot.getValue(String.class);
                        try{

                            if(namea!=null){

                                name = namea;
                                getEmail();

                            }

                            else{

                                Log.d("onGetName", "No Name Retrieved.");

                            }

                        }

                        catch(Exception e){

                            Log.e("onGetName", e.getMessage(), e);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        catch(Exception e){

            Log.e("onGetName", e.getMessage(), e);

        }

    }


    private void getEmail(){

        try{

            DatabaseReference getemail = FirebaseDatabase.getInstance().getReference().child("Users");
            getemail.child(user).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        String emailz = dataSnapshot.getValue(String.class);
                        try{

                            if(emailz!=null){

                                email = emailz;
                                System.out.println(user);
                                System.out.println(name);
                                System.out.println(email);
                                getProfile();
                                fname.setText(name);
                                femail.setText(email);

                            }

                            else {

                                Log.d("OnEmail", "No Email Address was found");

                            }
                        }

                        catch(Exception e){

                            Log.e("New Pin Registration", e.getMessage(), e);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        catch(Exception e){

            Log.e("onRetreveEmail", e.getMessage(), e);

        }

    }

    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {

                super.onBackPressed();

            }

            else {

                getSupportFragmentManager().popBackStack();
            }

        }

    public String getUsernames() {

        return this.user;
    }

    public void signOut() {

        try{

            auth.signOut();
            startActivity(new Intent(mainmenu.this, login.class));
            Intent i = new Intent(this, GeofenceService.class);
            this.stopService(i);
            finish();

        }

        catch(Exception e){

            Log.e("onSignOut", e.getMessage(), e);

        }
    }

}
