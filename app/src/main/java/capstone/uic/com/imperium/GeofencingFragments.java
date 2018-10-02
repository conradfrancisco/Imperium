package capstone.uic.com.imperium;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
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

public class GeofencingFragments extends Fragment implements OnMapReadyCallback{

    private OnFragmentInteractionListener mListener;
    GoogleMap maps;
    private Spinner mapspin;
    final List<String> names = new ArrayList<String>();
    private EditText address;
    private Button save;
    private FirebaseAuth auth;
    private DatabaseReference ref1;
    String user = "";
    String email = "";
    String values = "";
    String addresses = "";
    LatLng set;
    String lat = "";
    String lng = "";
    LatLng setloc;
    double l, lg;


    public GeofencingFragments() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v  = inflater.inflate(R.layout.fragment_geofencing_fragments, container, false);
        return v;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapspin = (Spinner) view.findViewById(R.id.mapspin);
        address = (EditText) view.findViewById(R.id.address);
        save = (Button) view.findViewById(R.id.save);
        addresses = address.getText().toString().trim();

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if(users!=null){

            email = users.getEmail();
            mainmenu act = (mainmenu) getActivity();
            user = act.getMyUser();


        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new GetCoordinates().execute(address.getText().toString().replace(" ","+"));
                System.out.println(lat + " "+lng);


            }
        });
        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref1.child(user).child("AllChildren").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {
                    String FName = nameSnapshot.getKey();
                    names.add(FName);
                    System.out.println(FName);

                    ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                    namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mapspin.setAdapter(namesAdapter);
                    mapspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            TextView tv = (TextView)view;
                            values = tv.getText().toString();
                            System.out.println(values);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
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

                if(dialog.isShowing())
                    dialog.dismiss();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        maps = googleMap;
        LatLng l = new LatLng(-31, 151);
        maps.addMarker(new MarkerOptions().position(l).title("Current Location"));
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(l, 15));


    }

    private void moveCamera(LatLng latLng, String title){
        maps.clear();
        maps.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        maps.addMarker(new MarkerOptions().position(latLng).title(title));
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

