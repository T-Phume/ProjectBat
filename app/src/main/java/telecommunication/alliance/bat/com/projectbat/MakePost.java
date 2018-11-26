package telecommunication.alliance.bat.com.projectbat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class MakePost extends AppCompatActivity {
    private static final String TAG = "MAKEPOST";

    private ImageButton imageButton;
    private EditText title;
    private EditText feeling;

    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private long index;
    private Uri uploadUri;
    private String t;
    private String f;
    private String l;

    private TextView location;
    private ProgressBar mProgressBar;

    private int MAPCODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);


        title = findViewById(R.id.makePostTitle);
        feeling = findViewById(R.id.makePostFeeling);
        location = findViewById(R.id.makePostMap);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MakePost.this, Map.class);
                startActivityForResult(intent, MAPCODE);
            }
        });


        imageButton = findViewById(R.id.makePostImage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(MakePost.this);
            }
        });


        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("feed").child(user.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                index = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.makePostButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                t = title.getText().toString();
                f = feeling.getText().toString();
                l = location.getText().toString();

                if(t.length() == 0)
                    t = "None";
                if(f.length() == 0)
                    f = "Unknown";
                if(l.length() == 0)
                    l = "Somewhere over the rainbow.";

                Log.d(TAG, Long.toString(index));

                final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child(Long.toString(index));
                storageReference.putFile(uploadUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Post post = new Post(uri.toString(), t, f, l);
                                databaseReference.child(Long.toString(index)).setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });


            }
        });
    }

    private void handle_crop(int code, Intent result){
        if(code == RESULT_OK){
            Uri output = Crop.getOutput(result);
            uploadUri = output;
            Picasso.get().invalidate(output);
            Picasso.get().load(output).fit().into(imageButton);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri sourceUri = data.getData();
                Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped"));

                Crop.of(sourceUri, destinationUri).asSquare().start(this);
                Uri output = Crop.getOutput(data);
                uploadUri = output;

                Picasso.get().invalidate(output);
                Picasso.get().load(output).resize(250, 250).into(imageButton);
            } else if(requestCode == Crop.REQUEST_CROP){
                handle_crop(resultCode, data);
            }

            if(requestCode == MAPCODE){
                Uri d = data.getData();
                location.setText(d.toString());
            }
        }
    }

    private void showDialog(){
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideDialog(){
        if(mProgressBar.getVisibility() == View.VISIBLE)
            mProgressBar.setVisibility(View.INVISIBLE);
    }
}
