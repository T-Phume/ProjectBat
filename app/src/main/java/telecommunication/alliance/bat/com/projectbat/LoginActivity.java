package telecommunication.alliance.bat.com.projectbat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN_ACTIVITY_TAG";

    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView email;
    private TextView password;
    private TextView forgotPassword;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate");
        setupFirebaseAuth();

        loginButton = findViewById(R.id.loginButton);
        email = findViewById(R.id.loginEmailInput);
        password = findViewById(R.id.loginPasswordInput);

        TextView registerButton = findViewById(R.id.loginRegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "CLICKED");

                //check for null valued EditText fields
                if(!isEmpty(email.getText().toString())
                        && !isEmpty(password.getText().toString())){
                    //check if user has a valid email address
                    Log.d(TAG, "CLICKED2");
                    if(isValidEmail(email.getText().toString())){
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful())
                                    Log.d(TAG, "AUTHENTICATION SUCCESSFUL");
                                else{
                                    Log.d(TAG, "AUTHENTICATION SUCCESSFUL");
                                }
                            }
                        });

                    }else
                        Toast.makeText( LoginActivity.this, "Invalid email format", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(LoginActivity.this, "You must fill out all the fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFirebaseAuth(){
        Log.d(TAG, "Started setupFirebaseAuth");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    if(user.isEmailVerified()) {
                        Log.d(TAG, "Current User: " + user.getEmail());
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else{
                        Toast.makeText(LoginActivity.this, user.getEmail().toString() + "Email is not verified",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "NOT VERIFIED");
                    }
                } else {
                    Log.d(TAG, "Current User: None");
                }
            }
        };
    }

    private void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "EMAIL VERIFICATION SENT");
                        Toast.makeText(LoginActivity.this, "Sent email verification",
                                Toast.LENGTH_SHORT).show();
                    } else{
                        Log.d(TAG, "EMAIL VERIFICATION NOT SENT");
                        Toast.makeText(LoginActivity.this, "Verification not sent",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        // Add our listener to the firebase
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener !=  null)
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        // Removes our listener from firebase
    }

    private boolean isValidEmail(String s){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+  // from OWASP regex documentation
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return !(s == null) && pat.matcher(s).matches();
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }
}
