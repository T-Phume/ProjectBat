package telecommunication.alliance.bat.com.projectbat;

import android.arch.persistence.room.Database;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactActivity extends AppCompatActivity {
    private Button button;
    private EditText text;

    private DatabaseReference userRef;
    private DatabaseReference searchRef;
    private String user;
    private DatabaseReference friend;

    private FirebaseUser firebaseUser;
    private String username;

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

        FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                username = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(text.getText().toString())){
                            Log.d("CONTACT", "IN");
                            for(DataSnapshot childSnapshot: dataSnapshot.getChildren()){
                                String toFriend = childSnapshot.getKey();
                                Log.d("CONTACT", toFriend);
                                if(toFriend.equals(text.getText().toString())) {
                                    String help = childSnapshot.getValue(String.class);
                                    userRef.push().setValue(childSnapshot.getValue());
                                    DatabaseReference reffy = FirebaseDatabase.getInstance().getReference().child("friend").child(help);
                                    reffy.push().setValue(firebaseUser.getUid());
                                    Log.d("CONTACT", help);
                                    break;
                                }
                            }
                        } else{
                            Toast.makeText(ContactActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
