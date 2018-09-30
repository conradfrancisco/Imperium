package capstone.uic.com.imperium;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton fab, sab;
    private Button pass, pin;
    private TextView profile, name2, email2, hint;
    private EditText name;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private CircleImageView circleImageView;
    private StorageReference up;
    private DatabaseReference ref, ref1;
    private com.google.firebase.storage.UploadTask uploadTask;
    private ProgressBar progress;
    private Uri mImageUri;
    private String user = " ";
    private String namez = " ";
    private String namezz = " ";
    private String email = " ";
    private String urladd = " ";
    private String newnamez = " ";

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance(String param1, String param2) {

        ProfileFragment fragment = new ProfileFragment();
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

        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        getCurrentUser();
        circleImageView = (CircleImageView) view.findViewById(R.id.profilepic);
        hint = (TextView) view.findViewById(R.id.hint);
        fab = (FloatingActionButton) view.findViewById(R.id.editprofile);
        sab = (FloatingActionButton) view.findViewById(R.id.saveprofile);
        pass = (Button) view.findViewById(R.id.changepass);
        pin  = (Button) view.findViewById(R.id.changepin);
        name = (EditText) view.findViewById(R.id.changename);
        profile = (TextView) view.findViewById(R.id.changeprofile);
        name2 = (TextView) view.findViewById(R.id.name2);
        email2 = (TextView) view.findViewById(R.id.email2);
        up = FirebaseStorage.getInstance().getReference("ProfilePictures");
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref1 = FirebaseDatabase.getInstance().getReference("Users");
        progress = (ProgressBar) view.findViewById(R.id.progress);


        pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), reset.class));

            }
        });

        pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), newpin.class));

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fab.setVisibility(View.GONE);
                name2.setVisibility(View.GONE);
                email2.setVisibility(View.GONE);
                hint.setVisibility(View.VISIBLE);
                sab.setVisibility(View.VISIBLE);
                pass.setVisibility(View.VISIBLE);
                pin.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
                profile.setVisibility(View.VISIBLE);

                name.setText(namez);

                Snackbar sn = Snackbar.make(view, "UPDATE", Snackbar.LENGTH_SHORT);
                sn.show();

            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openFileChooser();

            }
        });

        sab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Please confirm action!");
                builder.setMessage("Are you sure you want to commit changes?");
                builder.setIcon(R.drawable.icon);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        newnamez = name.getText().toString();
                        setUserFullName(newnamez);
                        refreshName();
                        fab.setVisibility(View.VISIBLE);
                        name2.setVisibility(View.VISIBLE);
                        email2.setVisibility(View.VISIBLE);
                        hint.setVisibility(View.GONE);
                        sab.setVisibility(View.GONE);
                        pass.setVisibility(View.GONE);
                        pin.setVisibility(View.GONE);
                        name.setVisibility(View.GONE);
                        profile.setVisibility(View.GONE);
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


                    }
                });
                android.support.v7.app.AlertDialog alert = builder.create();
                alert.show();

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

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
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
    private void openFileChooser() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            circleImageView.setImageURI(mImageUri);

        }
        if (uploadTask != null && uploadTask.isInProgress()) {

            Snackbar sn = Snackbar.make(getView(), "Upload in Progress", Snackbar.LENGTH_LONG);
            sn.show();

        }

        else {

            progress.setVisibility(View.VISIBLE);
            uploadFile();

        }
    }
    public String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        System.out.println(mime.getExtensionFromMimeType(cR.getType(uri)));
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void getUrl(){

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Users").child(user);
        getuser.child("ProfilePicture").child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot != null){

                    urladd = dataSnapshot.getValue(String.class);
                    System.out.println(urladd);
                    Glide.with(getActivity()).load(urladd).into(circleImageView);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });

    }

    private void setUserFullName(String newname){

        ref1.child(user).child("fullname").setValue(newname);

    }

    private void uploadFile(){

        if(mImageUri != null){

            final StorageReference fileReference = up.child(user + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {

                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {

                        throw Objects.requireNonNull(task.getException());
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        progress.setVisibility(View.GONE);
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        UploadImage upload = new UploadImage(user, miUrlOk);
                        ref.child(user).child("ProfilePicture").setValue(upload);
                        getUrl();
                        startActivity(new Intent(getActivity(), mainmenu.class));

                    }

                    else {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }

        else {

        }
    }


    private void getCurrentUser(){

        DatabaseReference getuser = FirebaseDatabase.getInstance().getReference().child("Current");
        getuser.child("currentuser").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot != null){

                    user = dataSnapshot.getValue(String.class);
                    getName();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {



            }
        });

    }

    private void getName(){

        DatabaseReference getname = FirebaseDatabase.getInstance().getReference().child("Users");
        getname.child(user).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    namez = dataSnapshot.getValue(String.class);
                    getEmail();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void refreshName(){

        DatabaseReference getname = FirebaseDatabase.getInstance().getReference().child("Users");
        getname.child(user).child("fullname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    namezz = dataSnapshot.getValue(String.class);
                    name2.setText(namezz);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getEmail(){

        DatabaseReference getemail = FirebaseDatabase.getInstance().getReference().child("Users");
        getemail.child(user).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot != null){

                    email = dataSnapshot.getValue(String.class);
                    System.out.println(user);
                    name2.setText(namez);
                    email2.setText(email);
                    getUrl();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
