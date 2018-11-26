package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.internal.InternalTokenResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class FeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FEEEEEEEEED";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private FriendAdapter adapter;

    private ValueEventListener valueEventListener;

    private List<Friend> userList;

    public FeedFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
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
        Log.d(TAG, "onCreate: ");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("friend").child(firebaseUser.getUid());
        Log.d(TAG, "onCreate: " + firebaseUser.getUid());
        userList = new ArrayList<>();
        populateRecyclerView();
        adapter = new FriendAdapter(getActivity(), userList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feed, container, false);

        Button makePost = view.findViewById(R.id.feedMakePost);
        view.findViewById(R.id.feedFindContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactActivity.class);
                startActivity(intent);
            }
        });

        makePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MakePost.class);
                startActivity(intent);
            }
        });

        Log.d(TAG, "onCreateView: ");
        recyclerView = view.findViewById(R.id.feedRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new FriendAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Friend u = userList.get(position);
                Intent intent = new Intent(adapter.mCtx, friendProfile.class);
                intent.putExtra("FRIEND", u.getRef());
                adapter.mCtx.startActivity(intent);
            }
        });
        adapter.notifyDataSetChanged();
        Log.d(TAG, Integer.toString(adapter.getItemCount()));
        return view;
    }

    private void populateRecyclerView(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot childrenSnapshot: dataSnapshot.getChildren()) {
                    Friend temp = childrenSnapshot.getValue(Friend.class);
                    userList.add(temp);
                }
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
