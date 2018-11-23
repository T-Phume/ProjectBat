package telecommunication.alliance.bat.com.projectbat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

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

    private CircleImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        profileImage = findViewById(R.id.profile_image);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = firebaseUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        storageReference = FirebaseStorage.getInstance().getReference().child(user_id + "/profileImage");

        setupImage();

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

    }

    private void setupImage(){
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri.toString())
                        .resize(500, 500)
                        .into(profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "FAILED");
            }
        });
    }
}
