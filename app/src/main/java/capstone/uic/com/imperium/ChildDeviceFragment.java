package capstone.uic.com.imperium;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class ChildDeviceFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String passemails = "";
    private Button enable, disable;
    private TextView childname, status;
    final List<String> names = new ArrayList<String>();
    private FirebaseAuth auth;
    private DatabaseReference ref, ref1, ref2, ref3;
    private Spinner spinname;
    String user = "";
    String email = "";
    String parent = "";
    String values = "";
    String array[];

    private OnFragmentInteractionListener mListener;

    public ChildDeviceFragment() {

    }

    public static ChildDeviceFragment newInstance(String param1, String param2) {
        ChildDeviceFragment fragment = new ChildDeviceFragment();
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

        return inflater.inflate(R.layout.fragment_child_device, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        spinname = (Spinner) view.findViewById(R.id.namespin1);
        enable = (Button) view.findViewById(R.id.enable);
        disable = (Button) view.findViewById(R.id.disable);
        status = (TextView) view.findViewById(R.id.status);
        childname = (TextView) view.findViewById(R.id.childname);

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

                        if(nameSnapshot!=null){

                            String FName = nameSnapshot.getKey();
                            names.add(FName);
                            System.out.println(FName);

                        }
                        else{

                            Toast.makeText(getActivity(), "No child has been added yet!", Toast.LENGTH_LONG).show();

                        }

                    }

                    ArrayAdapter<String> namesAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, names);
                    namesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinname.setAdapter(namesAdapter);
                    spinname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                            TextView tv = (TextView)view;
                            values = tv.getText().toString();
                            System.out.println(values);
                            ref = FirebaseDatabase.getInstance().getReference("Users");
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot != null){

                                        passemails = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                                        try{

                                            System.out.println("I am" + " " + passemails);
                                            ref2 = FirebaseDatabase.getInstance().getReference("Users");
                                            ref2.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    if(dataSnapshot != null){

                                                        String val = dataSnapshot.child(user).child("Children").child(passemails).child("BlockDevice").getValue(String.class);
                                                        if(val!=null){

                                                            if(val.equals("1")){

                                                                childname.setVisibility(View.VISIBLE);
                                                                status.setVisibility(View.VISIBLE);
                                                                childname.setText("Your child, " +values+ "'s device is currently:");
                                                                status.setText("BLOCKED");
                                                                disable.setVisibility(View.VISIBLE);
                                                                enable.setVisibility(View.GONE);

                                                            }
                                                            else if (val.equals("0")){


                                                                childname.setVisibility(View.VISIBLE);
                                                                status.setVisibility(View.VISIBLE);
                                                                childname.setText("Your child, " +values+ "'s device is currently:");
                                                                status.setText("UNBLOCKED");
                                                                enable.setVisibility(View.VISIBLE);
                                                                disable.setVisibility(View.GONE);

                                                            }

                                                        }
                                                        else{

                                                            Toast.makeText(getActivity(), "No data has been specified!", Toast.LENGTH_LONG).show();

                                                        }


                                                    }

                                                    else {

                                                        Toast.makeText(getActivity(), "No data retrieved!", Toast.LENGTH_LONG).show();

                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }

                                        catch(Exception e){

                                            Log.e("New Pin Registration", e.getMessage(), e);

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

        disable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    ref3 = FirebaseDatabase.getInstance().getReference("Users");
                    ref3.child(user).child("Children").child(passemails).child("BlockDevice").setValue("0");

                }

                catch(Exception e){

                    Log.e("New Pin Registration", e.getMessage(), e);

                }


            }
        });

        enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    ref3 = FirebaseDatabase.getInstance().getReference("Users");
                    ref3.child(user).child("Children").child(passemails).child("BlockDevice").setValue("1");

                }

                catch(Exception e){

                    Log.e("New Pin Registration", e.getMessage(), e);

                }


            }
        });

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
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

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
