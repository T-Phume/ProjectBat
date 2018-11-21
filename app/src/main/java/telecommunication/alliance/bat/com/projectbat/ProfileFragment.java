package telecommunication.alliance.bat.com.projectbat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Boolean update = false;

    private Button editButton;

    private EditText username;
    private EditText country;
    private EditText profession;
    private EditText email;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editButton = view.findViewById(R.id.feedEdit);
        username = view.findViewById(R.id.feedUsername);
        country = view.findViewById(R.id.feedCountry);
        profession = view.findViewById(R.id.feedProfession);
        email = view.findViewById(R.id.feedEmail);


        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        name = name.equals("") ? "Username" : name;
        username.setText(name);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    username.setEnabled(false);
                    country.setEnabled(false);
                    profession.setEnabled(false);
                    email.setEnabled(false);
                    editButton.setText("Edit");
                    update = false;
                } else{
                    editButton.setText("Update");
                    username.setEnabled(true);
                    country.setEnabled(true);
                    profession.setEnabled(true);
                    email.setEnabled(true);
                    update = true;
                }
            }
        });

        return view;
    }
}
