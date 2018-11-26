package telecommunication.alliance.bat.com.projectbat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class friendProfile extends AppCompatActivity {
    private String ref;
    private DatabaseReference databaseReference;

    private TextView username, email, country, profession;
    private CircleImageView imageView;

    private User user;

    List<Post> postList;
    RecyclerView recyclerView;

    private UserPost adapter;

    private ValueEventListener mListener;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ref = getIntent().getExtras().getString("FRIEND");

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(ref);

        username = findViewById(R.id.friendUsername);
        email = findViewById(R.id.friendEmail);
        country = findViewById(R.id.friendCountry);
        profession = findViewById(R.id.friendProfession);
        imageView = findViewById(R.id.friend_profile_image);

        //Listener
        mListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                username.setText(user.getDisplayName());
                email.setText(user.getEmail());
                country.setText(user.getCountry());
                profession.setText(user.getProfession());
                Picasso.get().load(user.getUri()).fit().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        //Friend's properties init
        databaseReference.addValueEventListener(mListener);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        //Recycler init
        postList = new ArrayList<>();
        populateRecyclerView();
        adapter = new UserPost(friendProfile.this, postList);

        recyclerView = findViewById(R.id.friendRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(friendProfile.this));

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void populateRecyclerView(){
        DatabaseReference r =FirebaseDatabase.getInstance().getReference().child("feed").child(ref);
        r.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot postData: dataSnapshot.getChildren()){
                    postList.add(0, postData.getValue(Post.class));
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(mListener);
    }
}
