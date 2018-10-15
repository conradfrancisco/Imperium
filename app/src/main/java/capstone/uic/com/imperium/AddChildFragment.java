package capstone.uic.com.imperium;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddChildFragment extends Fragment {

    private FirebaseAuth auth;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OnFragmentInteractionListener mListener;
    private ProgressBar progress;
    private DatabaseReference ref, ref1, ref2, ref3, ref4, ref5, ref6;
    private String textmessage = "*G DRIVE LINK OF IMPERIUM CHILD APP TO BE INSERTED*";
    private String subjected = "Welcome to Imperium: A Parental Control Application";
    private String username = "noreply.ImperiumMonitoring@gmail.com";
    private String password = "09976689868";
    private EditText edit1, edit2;
    private String email = " ";
    private String namesd = " ";
    private String pass = "imperium123";
    private String user = " ";
    private Button save1;
    private String mParam1;
    private String mParam2;
    private String passval;

    public AddChildFragment() {


    }

    public static AddChildFragment newInstance(String param1, String param2) {
        AddChildFragment fragment = new AddChildFragment();
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

        return inflater.inflate(R.layout.fragment_add_child, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        auth = FirebaseAuth.getInstance();
        final FirebaseUser usersz = auth.getCurrentUser();
        passval = usersz.getEmail();
        edit1 = (EditText) view.findViewById(R.id.editText1234);
        edit2 = (EditText) view.findViewById(R.id.editText134);
        save1 = (Button) view.findViewById(R.id.savebutton123);
        progress = (ProgressBar) view.findViewById(R.id.progressb123);
        FirebaseApp.initializeApp(getActivity());
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        ref2 = FirebaseDatabase.getInstance().getReference("Users");
        ref3 = FirebaseDatabase.getInstance().getReference();
        ref4 = FirebaseDatabase.getInstance().getReference("Users");
        ref5 = FirebaseDatabase.getInstance().getReference("Users");
        ref6 = FirebaseDatabase.getInstance().getReference();
        getCurrentUser();

        save1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    final String usersd = usersz.getEmail();
                    progress.setVisibility(View.VISIBLE);
                    email = edit2.getText().toString();
                    namesd = edit1.getText().toString();
                    System.out.println(email);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Please confirm action!");
                    builder.setMessage("Are you sure you want to Add this Child?");
                    builder.setIcon(R.drawable.icon);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    try {
                                        GMailSender sender = new GMailSender(username, password);
                                        sender.sendMail(subjected, textmessage, username, email);
                                        System.out.println("OK");
                                    }
                                    catch (Exception e) {
                                        Log.e("SendMail", e.getMessage(), e);
                                    }

                                }
                            }).start();

                            String split[] = email.split("@");
                            String split1[] = usersd.split("@");
                            ref.child(user).child("Children").child(split[0]).child("ChildName").setValue(namesd);
                            ref2.child(user).child("Children").child(split[0]).child("ChildPass").setValue(pass);
                            ref1.child(user).child("AllChildren").child(namesd).setValue(split[0]);
                            ref3.child("Children").child(user).child(split[0]).setValue(true);
                            ref4.child(user).child("Children").child(split[0]).child("BlockDevice").setValue("0");
                            ref5.child(user).child("Children").child(split[0]).child("Apps").child("Facebook").setValue(true);
                            ref6.child("CurrentParent").child(split[0]).setValue(user);
                            progress.setVisibility(View.GONE);
                            startActivity(new Intent(getActivity(), mainmenu.class));

                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            Snackbar sn = Snackbar.make(getView(), "Cancelled", Snackbar.LENGTH_SHORT);
                            sn.show();
                            edit1.setText(null);
                            edit2.setText(null);


                        }
                    });
                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();

                }

                catch(Exception e){

                    Log.e("onClickSave", e.getMessage(), e);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
//
//    }

    private void getCurrentUser(){

        try{

            String[] split = passval.split("@");
            DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
            getuser.child(split[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        String userz = dataSnapshot.getValue(String.class);
                        try{

                            if(userz!=null){

                                user = userz;

                            }
                            else{

                                Log.d("OnRetrieveUser", "No Current Users Found!");

                            }

                        }

                        catch(Exception e){

                            Log.e("getCurrentUser", e.getMessage(), e);

                        }

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });

        }

        catch(Exception e){

            Log.e("getCurrentUser", e.getMessage(), e);

        }

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
