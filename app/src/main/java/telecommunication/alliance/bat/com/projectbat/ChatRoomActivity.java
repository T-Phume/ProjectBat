package telecommunication.alliance.bat.com.projectbat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    protected Button sendButton;
    protected EditText textArea;
    protected ListView chatRecycler;
    protected DatabaseReference msgRef;
    protected String roomName;
    protected ArrayList<Messages> allMessages = new ArrayList<>();
    protected ChatListAdapter chatListAdapter;
    protected DatabaseReference roomRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        sendButton = (Button)findViewById(R.id.button);
        textArea = (EditText)findViewById(R.id.msgArea);
        chatRecycler = (ListView)findViewById(R.id.msgList);
        roomName = getIntent().getExtras().get("roomName").toString();
        msgRef = FirebaseDatabase.getInstance().getReference().child("rooms").child(roomName).child("message");
        setTitle(roomName);


        chatListAdapter = new ChatListAdapter(this,R.layout.chatbubblelayout,allMessages);


        chatRecycler.setAdapter(chatListAdapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String name =   "Jake";
                String message = textArea.getText().toString();
                String key = msgRef.push().getKey();
                Messages m = new Messages(name,message);
                msgRef.child(key).setValue(m);
                textArea.setText("");

            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMessages.clear();
                for(DataSnapshot sns : dataSnapshot.getChildren()){
                    Messages m = sns.getValue(Messages.class);
                    allMessages.add(m);
                }

                chatListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}