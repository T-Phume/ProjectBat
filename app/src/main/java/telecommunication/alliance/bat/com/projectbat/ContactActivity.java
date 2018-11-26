package telecommunication.alliance.bat.com.projectbat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "CONTACTTTTTT";
    private Button button;
    private EditText text;

    private DatabaseReference userRef;
    private DatabaseReference searchRef;
    private String user;
    private DatabaseReference friend;

    private FirebaseUser firebaseUser;
    private String username;

    private User us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userRef = FirebaseDatabase.getInstance().getReference().child("friend").child(firebaseUser.getUid());
        friend = FirebaseDatabase.getInstance().getReference().child("friend");
        searchRef = FirebaseDatabase.getInstance().getReference().child("search");

        button = findViewById(R.id.contactSearch);
        text = findViewById(R.id.contactUsername);

        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                us = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!text.getText().toString().equals(username)) {
                    searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(text.getText().toString())) {
                                Friend f = dataSnapshot.child(text.getText().toString()).getValue(Friend.class);
                                userRef.child(text.getText().toString()).setValue(f);
                                Friend i = new Friend(us.getUsername(), us.getUri(), firebaseUser.getUid());
                                Log.d(TAG, "onDataChange: " + f.getName());
                                String temp = f.getName();
                                friend.child(f.getRef()).child(us.getUsername()).setValue(i);
                            } else {
                                Toast.makeText(ContactActivity.this, "Already Friend with this person.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else{
                    Toast.makeText(ContactActivity.this, "Username does not exist.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
