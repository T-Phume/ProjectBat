package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    private TextView country;
    private TextView profession;
    private String uri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);
        Log.d(TAG, "OnCreate");

        profileImage = findViewById(R.id.profile_image);
        username = findViewById(R.id.settingsUsername);
        country = findViewById(R.id.settingsCountry);
        profession = findViewById(R.id.settingsProfession);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        storageReference = FirebaseStorage.getInstance().getReference().child(user_id + "/profileImage");

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                country.setText(user.getCountry());
                profession.setText(user.getProfession());
                uri = user.getUri();
                setupImage();
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
