package capstone.uic.com.imperium;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GeofencingFragments extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private DatabaseReference ref, ref1;
    private FirebaseAuth auth;
    GoogleMap maps;
    private TextView textView;
    private Spinner mapspin;
    final List<String> names = new ArrayList<String>();
    private EditText address;
    private Button save;
    private Circle mCircle;
    Marker marker1, marker2;
    String user = "";
    String email = "";
    String values = "";
    String addresses = "";
    String passemails = "";
    String lat = "";
    String lng = "";
    LatLng setloc;
    String retrievelat = "";
    double l, lg, newl, newlg;
    String emails = "";
    String newemails = "";
    String currentloc = "";
    String currentloc1[];


    public GeofencingFragments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_geofencing_fragments, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        mapspin = (Spinner) view.findViewById(R.id.mapspin);
        address = (EditText) view.findViewById(R.id.address);
        save = (Button) view.findViewById(R.id.save);
        textView = (TextView) view.findViewById(R.id.selectmap);
        addresses = address.getText().toString().trim();
        ref = FirebaseDatabase.getInstance().getReference();
        ref1 = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

            email = users.getEmail();
            mainmenu act = (mainmenu) getActivity();
            user = act.getMyUser();
            System.out.println(emails);
            System.out.println(user);


        }
        else {

            Toast.makeText(getActivity(), "Firebase User is null!", Toast.LENGTH_LONG).show();

        }

        startTimer();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                marker1.remove();
                mCircle.remove();
                new GetCoordinates().execute(address.getText().toString().replace(" ", "+"));
                getCurrentUser();
                startActivity(new Intent(getActivity(), mainmenu.class));
            }


        });

        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref1.child(user).child("AllChildren").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot == null){

                    Toast.makeText(getActivity(), "No Data Retrieved!, Add a Child First", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), mainmenu.class));

                }
                else {

                    try{

                        for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                            String FName = nameSnapshot.getKey();
                            if(nameSnapshot!=null){

                                if(FName != null){

                                    names.add(FName);
                                    System.out.println(FName);

                                    ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                                    namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    mapspin.setAdapter(namesAdapter);
                                    mapspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                            TextView tv = (TextView) view;
                                            values = tv.getText().toString();
                                            System.out.println("Ako si:" + " " + values);
                                            getUser();
                                            maps.clear();

                                            DatabaseReference getuser3 = FirebaseDatabase.getInstance().getReference().child("Users");
                                            getuser3.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                    if (dataSnapshot != null) {

                                                        passemails = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                                                        if(passemails!=null){

                                                            System.out.println("I am" + " " + passemails);

                                                            DatabaseReference getuser1 = FirebaseDatabase.getInstance().getReference().child("Children");
                                                            getuser1.addValueEventListener(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                                    if (dataSnapshot != null) {

                                                                        retrievelat = dataSnapshot.child(user).child(passemails).child("SavedLocation").getValue(String.class);
                                                                        if(retrievelat!=null){

                                                                            System.out.println("The Saved Location is: "+retrievelat);
                                                                            String split[] = retrievelat.split(",");
                                                                            Double l1 = Double.parseDouble(split[0]);
                                                                            Double l2 = Double.parseDouble(split[1]);
                                                                            LatLng newer = new LatLng(l1, l2);
                                                                            moveCamera(newer, "Saved Location");

                                                                        }

                                                                        else {

                                                                            Toast.makeText(getActivity(), "RetrieveLat is null.", Toast.LENGTH_SHORT).show();

                                                                        }
                                                                    }
                                                                    else {

                                                                        Toast.makeText(getActivity(), "Data is null.", Toast.LENGTH_SHORT).show();

                                                                    }

                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {


                                                                }
                                                            });

                                                        }
                                                        else {

                                                            Toast.makeText(getActivity(), "No Children Data Retrieved.", Toast.LENGTH_SHORT).show();

                                                        }

                                                    }
                                                    else {

                                                        Toast.makeText(getActivity(), "No Children Data Retrieved.", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {


                                                }
                                            });

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                                else {

                                    Toast.makeText(getActivity(), "No Children Data Retrieved.", Toast.LENGTH_SHORT).show();

                                }

                            }
                            else {

                                Toast.makeText(getActivity(), "No Children Data Retrieved.", Toast.LENGTH_SHORT).show();

                            }

                        }

                    }

                    catch(Exception e){

                        Log.e("GeoFragments", e.getMessage(), e);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }

    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Please wait....");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
                HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=AIzaSyAZ4nIYQcKGZgTee_ae9x_dIfKCNupUAkY",address);
                System.out.println(url);
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {
                Log.e("GeoFragments", ex.getMessage(), ex);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println(s);
            try{
                JSONObject jsonObject = new JSONObject(s);

                lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();


                System.out.println(String.format("Coordinates : %s / %s ",lat,lng));
                l = Double.parseDouble(lat);
                lg = Double.parseDouble(lng);
                setloc = new LatLng(l,lg);
                moveCamera(setloc, "Set Location");
                System.out.println("Latitude:"+lat+ "and Longitude:"+lng+ ","+"User:"+user+" and Email is:"+emails);
                ref.child("Children").child(user).child(emails).child("SavedLocation").setValue(lat+","+lng);

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override public void onMapReady(GoogleMap googleMap) {

        maps = googleMap;
        maps.clear();
        LatLng l = new LatLng(7.051400, 125.594772);
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 5));


    }

    private void moveCamera(LatLng latLng, String title){
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        marker1 = maps.addMarker(new MarkerOptions().position(latLng).title(title));
        drawMarkerWithCircle(latLng);
    }

    private void moveCamera1(Double a, Double b, String title){
        LatLng c = new LatLng(a,b);
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(c, 16));
        marker2 = maps.addMarker(new MarkerOptions().position(c).title(title));
    }

    private void drawMarkerWithCircle(LatLng position){

        double radiusInMeters = 100;
        int strokeColor = 0xffff0000;
        int shadeColor = 0x44ff0000;

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = maps.addCircle(circleOptions);

    }

    private void getCurrentLoc(){

        DatabaseReference getLoc = FirebaseDatabase.getInstance().getReference().child("Children");
        getLoc.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    try{

                        System.out.println("Newemails: "+newemails);
                        currentloc = dataSnapshot.child(user).child(newemails).child("CurrentLocation").getValue(String.class);
                        if(currentloc!=null){

                            System.out.println("Current Location"+ " "+currentloc);
                            currentloc1 = currentloc.split(",");
                            newl = Double.parseDouble(currentloc1[0]);
                            newlg = Double.parseDouble(currentloc1[1]);
                            moveCamera1(newl, newlg, "Current Location");
                        }
                        else {

                            Toast.makeText(getActivity(), "Current Location is NULL.", Toast.LENGTH_SHORT).show();

                        }

                    }

                    catch(Exception e){

                        Log.e("GeoFragments", e.getMessage(), e);

                    }



                }

                else {

                    Toast.makeText(getContext(), "No Current Location Data recieved!", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {



            }
        });

    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 5000, 10000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {

                getCurrentLoc();

            }
        };
    }

    private void getCurrentUser(){

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Users");
        getuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    try{

                        String emailz = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                        if(emailz!=null){

                            emails = emailz;
                            System.out.println("I am"+ " "+emails);
                        }
                        else{

                            Toast.makeText(getContext(), "No Current User found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    catch(Exception e){

                        Log.e("GeoFragments", e.getMessage(), e);
                    }



                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });

    }

    private void getUser(){

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Users");
        getuser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot != null){

                    try{

                        String newemailz = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                        if(newemailz!=null){

                            newemails = newemailz;
                            System.out.println("I am"+ " "+newemails);

                        }
                        else{

                            Toast.makeText(getContext(), "No Users found!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    catch(Exception e){

                        Log.e("GeoFragments", e.getMessage(), e);

                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}

