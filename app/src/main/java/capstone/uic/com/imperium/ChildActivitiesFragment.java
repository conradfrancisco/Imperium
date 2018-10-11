package capstone.uic.com.imperium;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
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
    private DatabaseReference ref1, ref2;
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
        FirebaseUser users = auth.getCurrentUser();
        if(users!=null){

            email = users.getEmail();
            mainmenu act = (mainmenu) getActivity();
            user = act.getMyUser();


        }
        System.out.println(email);
        System.out.println(user);
        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref1.child(user).child("AllChildren").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){


                    for (DataSnapshot nameSnapshot : dataSnapshot.getChildren()) {

                        String FName = nameSnapshot.getKey();
                        names.add(FName);
                        System.out.println(FName);

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


                                    }

                                    else {

                                        Toast.makeText(getActivity(), "No child exists!", Toast.LENGTH_LONG).show();

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

                    Toast.makeText(getActivity(), "No child has been added yet!", Toast.LENGTH_LONG).show();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
        listViewApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String appn = (String) listViewApps.getItemAtPosition(i);
                Toast.makeText(getActivity(), appn, Toast.LENGTH_LONG).show();
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
