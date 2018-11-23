package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.flags.Flag;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER_ACTIVITY_TAG";

    private FirebaseUser user;

    private TextView email;
    private TextView password1;
    private TextView password2;
    private Button registerButton;
    private TextView username;

    private FirebaseStorage firebaseStorage;
    private StorageReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate");

        email = findViewById(R.id.registerEmailInput);
        password1 = findViewById(R.id.registerPasswordInput1);
        password2 = findViewById(R.id.registerPasswordInput2);
        registerButton = findViewById(R.id.registerButton);
        username = findViewById(R.id.registerUsername);

        firebaseStorage = FirebaseStorage.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check for null valued EditText fields
                if(!isEmpty(email.getText().toString())
                        && !isEmpty(password1.getText().toString())
                        && !isEmpty(password2.getText().toString())
                        && !isEmpty(username.getText().toString())){

                    //check if user has a valid email address
                    if(isValidEmail(email.getText().toString())){
                        //check if passwords match
                        if(doStringsMatch(password1.getText().toString(), password2.getText().toString())){
                            if(isValidUsername(username.getText().toString())){
                                registerEmail(email.getText().toString(), password1.getText().toString(), username.getText().toString());
                            } else{
                                Toast.makeText(RegisterActivity.this, "Invalid username format", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(RegisterActivity.this, "Passwords do not Match", Toast.LENGTH_SHORT).show();
                        }
                    }else
                        Toast.makeText(RegisterActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(RegisterActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerEmail(final String email_s, String password, final String username_s){
        Log.d(TAG, "Registering Email");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_s, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "REGISTER SUCCESSFUL");
                    sendVerificationEmail();
                    writeNewUser(email_s, username_s);
                    FirebaseAuth.getInstance().signOut();
                    redirectLoginScreen();
                } else{
                    Log.d(TAG, "REGISTER UNSUCCESSFUL");
                }
            }
        });
    }

    private void writeNewUser(String email, String username_s){
        Log.d(TAG, "UPLOADING IMAGE");
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseStorage.getInstance().getReference().child(user_id + "/profileImage");  // path of our storage in firebase

        Uri uri = Uri.parse("android.resource://telecommunication.alliance.bat.com.projectbat/" + R.drawable.profile_bat);
        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "UPLOADED");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                });

        Log.d(TAG, "WRITING DATABASE");
        User databaseUser = new User(username_s, email);
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(user.getUid()).setValue(databaseUser);
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "EMAIL VERIFICATION SENT");
                        Toast.makeText(RegisterActivity.this, "Sent email verification",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Log.d(TAG, "EMAIL VERIFICATION NOT SENT");
                        Toast.makeText(RegisterActivity.this, "Verification not sent",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidEmail(String s){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+  // from OWASP regex documentation
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private void redirectLoginScreen(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean isValidUsername(String s){
        String usernameRegex = "^[a-zA-Z0-9._-]{4,16}$";
        Pattern pat = Pattern.compile(usernameRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }
}
