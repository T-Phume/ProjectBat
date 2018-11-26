package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "PROFILE_FRAGMENT";

    private Boolean update = false;

    private CircleImageView profileImage;

    private TextView username;
    private TextView country;
    private TextView profession;
    private TextView email;
    private TextView settingUsername;

    private ImageView logout;

    private DatabaseReference mDatabaseRef;
    private ValueEventListener userListener;
    private FirebaseUser userAuth;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    private String user_id;
    private String uri = "";

    List<Post> postList;
    RecyclerView recyclerView;

    private UserPost adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        user_id = userAuth.getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(user_id);

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DATA CHANGE");
                try {
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getDisplayName());
                    country.setText(user.getCountry());
                    profession.setText(user.getProfession());
                    email.setText(user.getEmail());
                    if(!uri.equals(user.getUri())) {
                        uri = user.getUri();
                        setUpImage();
                    }
                }
                catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "Failed to read value");
            }
        };

        postList = new ArrayList<>();
        populateRecyclerView();
        adapter = new UserPost(getActivity(), postList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "OnCreateView");

        //widget initialization
        username = view.findViewById(R.id.profileUsername);
        country = view.findViewById(R.id.profileCountry);
        profession = view.findViewById(R.id.profileProfession);
        email = view.findViewById(R.id.profileEmail);
        profileImage = view.findViewById(R.id.friend_image);
        logout = view.findViewById(R.id.profileLogout);
        ImageView settingImage = view.findViewById(R.id.profileEdit);
        settingUsername = view.findViewById(R.id.settingsUsername);


        mDatabaseRef.addValueEventListener(userListener);

        //Populating recycler


        // widget listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        settingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileSettings.class);
                startActivity(intent);
            }
        });


        //initializing recycler and adapter
        recyclerView = view.findViewById(R.id.profileRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    private void setUpImage(){
        Log.d(TAG, uri);
        Picasso.get()
                .load(uri)
                .resize(250, 250)
                .into(profileImage);
    }

    private boolean isValidProfession(String s){
        String countryRegex = "^[a-zA-Z0-9]{4,30}$";
        Pattern pat = Pattern.compile(countryRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private boolean isValidCountryName(String s){
        String countryRegex = "^[a-zA-Z0-9]{4,20}$";
        Pattern pat = Pattern.compile(countryRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private boolean isValidUsername(String s){
        String usernameRegex = "^[a-zA-Z0-9._-]{4,16}$";
        Pattern pat = Pattern.compile(usernameRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private void populateRecyclerView(){
        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("feed").child(user_id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
    public void onDestroyView() {
        super.onDestroyView();
        mDatabaseRef.removeEventListener(userListener);
    }
}
