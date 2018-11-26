package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatMainActivity extends AppCompatActivity {
    private Button sendButton;
    private EditText textArea;
    private ListView chatRooms;
    private EditText descArea;
    private ArrayList<Person> allRoom = new ArrayList<>();
    private ArrayList<String> readNameList = new ArrayList<>();
    private ArrayList<String> readDescList = new ArrayList<>();

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("rooms");
    private RoomListAdaptor roomAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.onStart();

        descArea = (EditText) findViewById(R.id.descEnter);
        sendButton = (Button) findViewById(R.id.SendButton);
        textArea = (EditText) findViewById(R.id.textEnter);
        chatRooms = (ListView) findViewById(R.id.roomList);


        roomAdapter = new RoomListAdaptor(this, R.layout.roomlayout, allRoom);

        chatRooms.setAdapter(roomAdapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Person i : allRoom) {
                    if (i.getName() == textArea.getText().toString())
                        return;
                }
                Person p = new Person(textArea.getText().toString(), descArea.getText().toString());
                root.child(textArea.getText().toString()).setValue(p);
                DatabaseReference test = root.child(textArea.getText().toString());
                test.child("message");


                textArea.setText("");
                descArea.setText("");
            }
        });


        chatRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),ChatRoomActivity.class);
                intent.putExtra("roomName",((TextView)view.findViewById(R.id.nameLabel)).getText().toString());
                startActivity(intent);
            }
        });


//        textWriteRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                String newText = dataSnapshot.getValue(String.class);
//                chatArea.append(newText+"\n");
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allRoom.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    Person p = ds.getValue(Person.class);
                    allRoom.add(p);
                }
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}