package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileSettings extends AppCompatActivity {
    private static final String TAG = "PROFILE_SETTINGS";

    private String user_id;


    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private DatabaseReference searchRef;

    private ValueEventListener userListener;

    private CircleImageView profileImage;

    private TextView username;
    private Spinner countrySpinner;
    private TextView profession;
    private String uri = "";

    private User userInstance = null;

    private Button update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Log.d(TAG, "OnCreate");

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<String>();
        String country;
        for( Locale loc : locale ){
            country = loc.getDisplayCountry();
            if( country.length() > 0 && !countries.contains(country) ){
                countries.add( country );
            }
        }
        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        countrySpinner = findViewById(R.id.spinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, countries);
        countrySpinner.setAdapter(adapter);
        countrySpinner.setSelection(adapter.getPosition("Thailand"));

        profileImage = findViewById(R.id.friend_profile_image);
        username = findViewById(R.id.settingsUsername);
        profession = findViewById(R.id.settingsProfession);
        update = findViewById(R.id.settingsUpdate);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        storageReference = FirebaseStorage.getInstance().getReference().child(user_id + "/profileImage");

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userInstance = dataSnapshot.getValue(User.class);
                username.setText(userInstance.getDisplayName());
                countrySpinner.setSelection(adapter.getPosition(userInstance.getCountry()));
                profession.setText(userInstance.getProfession());
                if(!uri.equals(userInstance.getUri())) {
                    uri = userInstance.getUri();
                    setupImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "FAIL TO READ VALUE");
            }
        };
        databaseReference.addValueEventListener(userListener);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(ProfileSettings.this);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "UPDATING INFO");
                final String u = username.getText().toString();
                final String p = profession.getText().toString();
                final String c = countrySpinner.getSelectedItem().toString();

                if(!ValidityChecker.isValidUsername(u)){ // if not valid, do something
                    Log.d(TAG, "Invalid display name");
                }

                if(!ValidityChecker.isValidProfession(p)){
                    Log.d(TAG, "Invalid Profession");
                }

                if(ValidityChecker.isValidUsername(u) && ValidityChecker.isValidProfession(p)){
                    if(!u.equals(userInstance.getUsername()))
                        databaseReference.child("displayname").setValue(u);
                    if(!c.equals(userInstance.getCountry()))
                        databaseReference.child("country").setValue(c);
                    if(!p.equals(userInstance.getProfession()))
                        databaseReference.child("profession").setValue(p);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri sourceUri = data.getData();
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Crop.of(sourceUri, destinationUri).asSquare().start(this);
            } else if(requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode, data);
            }
        }
    }

    private void handle_crop(int code, Intent result){
        if(code == RESULT_OK){
            Uri output = Crop.getOutput(result);
            update_image(output);
            Picasso.get().invalidate(output);
            setupImage();
        }
    }

    private void update_image(Uri URI){
        storageReference.putFile(URI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "IMAGE UPLOADED SUCCESSFULLY");
                Log.d(TAG, uri);
                Picasso.get().invalidate(uri);
                ProfileSettings.this.setupImage();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "IMAGE UPLOAD FAILED");
            }
        });
    }

    private void setupImage(){
        Log.d(TAG, "LOADING IMAGE");
        Picasso.get()
                .load(uri)
                .fit()
                .centerCrop()
                .into(profileImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(userListener);
    }
}
