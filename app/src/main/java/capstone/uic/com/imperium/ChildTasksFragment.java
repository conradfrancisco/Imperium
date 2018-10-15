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
import android.widget.AutoCompleteTextView;
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

public class ChildTasksFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Button submit, verify;
    private TextView statusb1;
    private TextView statusb;
    private AutoCompleteTextView assigntask;
    private Spinner spinname;
    String passemails = "";
    final List<String> names = new ArrayList<String>();
    private FirebaseAuth auth;
    private DatabaseReference ref, ref1, ref2, ref3, ref4, ref5;
    String user = "";
    String email = "";
    String parent = "";
    String values = "";
    String array[];
    String status = "";
    String gets = "";

    private OnFragmentInteractionListener mListener;

    public ChildTasksFragment() {

    }

    public static ChildTasksFragment newInstance(String param1, String param2) {
        ChildTasksFragment fragment = new ChildTasksFragment();
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
    public void onViewCreated (View view, Bundle savedInstanceState) {

        spinname = (Spinner) view.findViewById(R.id.namespin2);
        assigntask = (AutoCompleteTextView) view.findViewById(R.id.assigntask);
        String[] tasks = getResources().getStringArray(R.array.tasks_array);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, tasks);
        assigntask.setAdapter(adapter);
        statusb = (TextView) view.findViewById(R.id.statusb);
        statusb1 = (TextView) view.findViewById(R.id.statusb1);
        submit = (Button) view.findViewById(R.id.submitb);
        verify = (Button) view.findViewById(R.id.verify);

        auth = FirebaseAuth.getInstance();
        FirebaseUser users = auth.getCurrentUser();
        if (users != null) {

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

                        try{

                            if(nameSnapshot!=null){

                                String FName = nameSnapshot.getKey();
                                names.add(FName);
                                System.out.println(FName);
                            }
                        }

                        catch(Exception e){

                            Log.e("getChildren", e.getMessage(), e);

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

                                    if(dataSnapshot != null) {

                                        passemails = dataSnapshot.child(user).child("AllChildren").child(values).getValue(String.class);
                                        try{

                                            if(passemails!=null){

                                                System.out.println("I am" + " " + passemails);
                                                getStatus(passemails);

                                            }

                                            else{

                                                Log.d("ChildReceive", "No Child has been added yet.");

                                            }

                                        }

                                        catch(Exception e){

                                            Log.e("Child Tasks", e.getMessage(), e);

                                        }


                                    }

                                    else {

                                        Log.d("ChildReceive", "No Child has been added yet.");

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

                    Log.d("ChildRecieve", "No Child has been added yet.");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try{

                    ref2 = FirebaseDatabase.getInstance().getReference("Users");
                    ref3 = FirebaseDatabase.getInstance().getReference("Users");
                    ref4 = FirebaseDatabase.getInstance().getReference("Users");
                    ref2.child(user).child("Children").child(passemails).child("Tasks").child("Status").setValue("1");
                    ref3.child(user).child("Children").child(passemails).child("Tasks").child("To-Do").setValue(assigntask.getText().toString());
                    ref4.child(user).child("Children").child(passemails).child("HardStatus").setValue("0");
                    assigntask.setText(null);
                    submit.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    if(status.equals("0")){

                        statusb1.setVisibility(View.VISIBLE);

                    }

                }

                catch(Exception e){

                    Log.e("onSubmitClick", e.getMessage(), e);

                }


            }
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    submit.setVisibility(View.VISIBLE);
                    verify.setVisibility(View.GONE);
                    statusb1.setVisibility(View.GONE);
                    ref4 = FirebaseDatabase.getInstance().getReference("Users");
                    ref4.child(user).child("Children").child(passemails).child("HardStatus").setValue("0");
                    ref5 = FirebaseDatabase.getInstance().getReference("Users");
                    ref5.child(user).child("Children").child(passemails).child("Tasks").child("Status").setValue("0");

                }

                catch(Exception e){

                    Log.e("onVerify", e.getMessage(), e);

                }

            }
        });


    }

    public void getStatus(final String passemails){

        try{

            ref5 = FirebaseDatabase.getInstance().getReference("Users");
            ref5.child(user).addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    status = dataSnapshot.child("Children").child(passemails).child("HardStatus").getValue(String.class);
                    try{

                        if(status!=null && status.equals("1")){

                            statusb.setVisibility(View.VISIBLE);
                            statusb1.setVisibility(View.GONE);
                            verify.setVisibility(View.VISIBLE);
                            System.out.println("Current Value: "+status);

                        }
                        else if(status.equals("0")){

                            statusb.setVisibility(View.GONE);
                            verify.setVisibility(View.GONE);
                            System.out.println("Current Value: "+status);

                        }
                        else{

                            Toast.makeText(getActivity(), "Tasks not found!", Toast.LENGTH_LONG).show();

                        }

                    }

                    catch(Exception e){

                        Log.e("Child Tasks", e.getMessage(), e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        catch(Exception e){

            Log.e("onGetStatus", e.getMessage(), e);

        }

    }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState){

            return inflater.inflate(R.layout.fragment_child_tasks, container, false);
        }

        public void onButtonPressed (Uri uri){
            if (mListener != null) {
                mListener.onFragmentInteraction(uri);
            }
        }

        @Override
        public void onAttach (Context context){
            super.onAttach(context);
            if (context instanceof OnFragmentInteractionListener) {
                mListener = (OnFragmentInteractionListener) context;
            } else {
                throw new RuntimeException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }

        @Override
        public void onDetach () {
            super.onDetach();
            mListener = null;
        }

        public interface OnFragmentInteractionListener {

            void onFragmentInteraction(Uri uri);
        }
}

