package capstone.uic.com.imperium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;


public class ChildActivitiesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ListView listViewApps;
    final List<String> names = new ArrayList<String>();
    final List<String> apps = new ArrayList<String>();
    private ArrayAdapter<String> arrayAdapterCrimes;
    private String mParam1;
    private FirebaseAuth auth;
    private DatabaseReference ref1, ref2, ref3;
    private String mParam2;
    private Spinner spinname;
    String passemails = "";
    String user = "";
    String email = "";
    String parent = "";
    String values = "";
    String array[];
    private OnFragmentInteractionListener mListener;

    public ChildActivitiesFragment() {

    }


    public static ChildActivitiesFragment newInstance(String param1, String param2) {
        ChildActivitiesFragment fragment = new ChildActivitiesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_child_activities, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        spinname = (Spinner) view.findViewById(R.id.namespin);
        listViewApps = (ListView) view.findViewById(R.id.listview);
        auth = FirebaseAuth.getInstance();
        final FirebaseUser users = auth.getCurrentUser();
        if(users!=null){

            email = users.getEmail();
            mainmenu act = (mainmenu) getActivity();
            user = act.getMyUser();


        }
        System.out.println(email);
        System.out.println(user);

        try{

            ref1 = FirebaseDatabase.getInstance().getReference("Users");
            ref1.child(user).child("AllChildren").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){


                        for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {

                            try{

                                if(nameSnapshot!=null){

                                    String FName = nameSnapshot.getKey();
                                    names.add(FName);
                                    System.out.println(FName);
                                }
                                else{

                                    Log.d("ChildSpinner", "No Child has been added yet.");
                                }

                            }

                            catch(Exception e){

                                Log.e("ChildRetrieve", e.getMessage(), e);

                            }

                        }

                        final ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                        namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinname.setAdapter(namesAdapter);
                        spinname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                TextView tv = (TextView)view;
                                values = tv.getText().toString();
                                System.out.println(values);
                                ref1 = FirebaseDatabase.getInstance().getReference("Users");
                                ref1.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if(dataSnapshot != null) {

                                            passemails = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                                            if(passemails!=null){

                                                System.out.println("I am" + " " + passemails);
                                                ref2 = FirebaseDatabase.getInstance().getReference("Users");
                                                ref2.child("Children").child(passemails).child("Apps").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        if(dataSnapshot != null){


                                                            for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {

                                                                if(nameSnapshot!=null){

                                                                    String AName = nameSnapshot.getKey();
                                                                    apps.add(AName);
                                                                    System.out.println(AName);

                                                                }
                                                                else{

                                                                    Log.d("ChildSpinner", "No Application has been added yet.");

                                                                }
                                                                arrayAdapterCrimes = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, apps);
                                                                listViewApps.setAdapter(arrayAdapterCrimes);
                                                            }
                                                        }

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }
                                            else{

                                                Log.d("ChildSpinner2", "No Child has been added yet.");

                                            }


                                        }

                                        else {

                                            Log.d("ChildSpinner1", "No Child exists.");

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });


                    }

                    else {

                        Log.d("ChildSpinner", "No Child has been added yet.");

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }


            });


        }

        catch(Exception e){

            Log.e("onViewCreated", e.getMessage(), e);

        }

        listViewApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                try{

                    final String appname = (String) listViewApps.getItemAtPosition(i);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Block Application Usage");
                    builder.setMessage("Please choose action.");
                    builder.setIcon(R.drawable.icon);
                    builder.setPositiveButton("Block Now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            if(appname.equals("Facebook")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.facebook.katana");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Instagram")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.instagram.android");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));
                            }
                            else if(appname.equals("Chrome")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.android,chrome");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));
                            }
                            else if(appname.equals("Youtube")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.google.android.youtube");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Twitter")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.twitter.android");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Messenger")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.facebook.orca");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Clash of Clans")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.supercell.clashofclans");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Rise of Civilizations")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.lilithgame.roc.gp");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Dragon Nest M - SEA（Dark Avenger")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.playfungame.ggplay.lzgsea");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));

                            }
                            else if(appname.equals("Mobile Legends: Bang Bang")){

                                ref3 = FirebaseDatabase.getInstance().getReference("Users");
                                ref3.child(user).child("Children").child(passemails).child("BlockedApps").setValue("com.mobile.legends");
                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getActivity(), mainmenu.class));
                            }
                            else{

                                Toast.makeText(getActivity(), "Application Not Supported!", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                    builder.setNegativeButton("Unblock Now", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ref3 = FirebaseDatabase.getInstance().getReference("Users");
                            ref3.child(user).child("Children").child(passemails).child("BlockedApps").removeValue();
                            startActivity(new Intent(getActivity(), mainmenu.class));


                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                }

                catch(Exception e){

                    Log.e("onClickListView", e.getMessage(), e);
                }


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
