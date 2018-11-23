package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileSettings extends AppCompatActivity {
    private static final String TAG = "PROFILE_SETTINGS";

    private CircleImageView setUpImage;

    private String user_id;

    private Bitmap compressedImageFile;
    private CircleImageView imageView;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ValueEventListener userListener;

    private CircleImageView profileImage;

    private static final int GALLERY_INTENT = 1;

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

        profileImage = findViewById(R.id.profile_image);
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
                username.setText(userInstance.getUsername());
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
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "UPDATING INFO");
                String u = username.getText().toString();
                String p = profession.getText().toString();
                String c = countrySpinner.getSelectedItem().toString();

                if(!ValidityChecker.isValidUsername(u)){ // if not valid, do something
                    Log.d(TAG, "Invalid Username");
                }

                if(!ValidityChecker.isValidProfession(p)){
                    Log.d(TAG, "Invalid Profession");
                }

                if(ValidityChecker.isValidUsername(u) && ValidityChecker.isValidProfession(p)){
                    if(!u.equals(userInstance.getUsername()))
                        databaseReference.child("username").setValue(u);
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
        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){
            Uri URI = data.getData();

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
    }

    private void setupImage(){
        Log.d(TAG, "LOADING IMAGE");
        Picasso.get()
                .load(uri)
                .resize(500, 500)
                .into(profileImage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(userListener);
    }
}
