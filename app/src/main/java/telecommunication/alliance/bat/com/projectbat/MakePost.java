package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
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

public class MakePost extends AppCompatActivity {
    private ImageButton imageButton;
    private static final int GALLERY_REQUEST = 1;

    private EditText desc;
    DatabaseReference userReference;

    private String u;
    private long num;

    private boolean uploaded;
    private StorageReference ref;
    private FirebaseUser usr;

    private TextView title;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post);

        imageButton = findViewById(R.id.makePostImage);
        desc = findViewById(R.id.makePostDesc);
        usr = FirebaseAuth.getInstance().getCurrentUser();

        uploaded = false;

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        u = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        userReference = FirebaseDatabase.getInstance().getReference().child("feed").child(u);

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                num = dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        findViewById(R.id.makePostButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String d = desc.getText().toString();
                if(uploaded){
                    ref = FirebaseStorage.getInstance().getReference().child(u + "/" + num);  // path of our storage in firebase
                    title = findViewById(R.id.makePostTitle);

                    ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Piece piece = new Piece(uri.toString(), d, title.getText().toString());
                                    userReference.child(Long.toString(num)).setValue(piece);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            uri = data.getData();
            imageButton.setImageURI(imageUri);
            uploaded = true;
        }
    }
}
