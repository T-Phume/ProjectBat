package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

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

    private Button editButton;

    private EditText username;
    private EditText country;
    private EditText profession;
    private EditText email;

    private ImageView logout;

    private DatabaseReference mDatabaseRef;
    private ValueEventListener userListener;
    private FirebaseUser userAuth;

    private ImageView profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "OnCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        Log.d(TAG, "OnCreateView");

        logout = view.findViewById(R.id.profileLogout);
        editButton = view.findViewById(R.id.profileEdit);


        username = view.findViewById(R.id.profileUsername);
        country = view.findViewById(R.id.profileCountry);
        profession = view.findViewById(R.id.profileProfession);
        email = view.findViewById(R.id.profileEmail);
        profileImage = view.findViewById(R.id.profile_image);

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(userAuth.getUid());

        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DATA CHANGE");
                try {
                    User user = dataSnapshot.getValue(User.class);
                    username.setText(user.getUsername());
                    country.setText(user.getCountry());
                    profession.setText(user.getProfession());
                    email.setText(user.getEmail());
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

        mDatabaseRef.addValueEventListener(userListener);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    String username_s = username.getText().toString();
                    String country_s = country.getText().toString();
                    String profession_s = profession.getText().toString();

                    if(isValidUsername(username_s)){
                        if(isValidCountryName(country_s)){
                            if(isValidProfession(profession_s)){

                            } else{
                                Log.d(TAG, "Invalid Profession format");
                            }
                        } else{
                            Log.d(TAG, "Invalid Country name format");
                        }
                    } else{
                        Log.d(TAG, "Invalid Username format");
                    }

                    username.setEnabled(false);
                    country.setEnabled(false);
                    profession.setEnabled(false);
                    editButton.setText("Edit");
                    update = false;
                } else{
                    editButton.setText("Update");
                    username.setEnabled(true);
                    country.setEnabled(true);
                    profession.setEnabled(true);
                    update = true;
                }
            }
        });

        return view;
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



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDatabaseRef.removeEventListener(userListener);
    }
}
