package telecommunication.alliance.bat.com.projectbat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.content.Intent;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LOGIN_ACTIVITY_TAG";

    private FirebaseAuth.AuthStateListener mAuthListener;

    private TextView inputEmail;
    private TextView inputPassword;
    private TextView forgotPassword;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate");
        setupFirebaseAuth();

        TextView registerButton = findViewById(R.id.loginRegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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
                    Log.d(TAG, "Current User: " + user.getEmail());
                } else {
                    Log.d(TAG, "Current User: None");
                }
            }
        };
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
}
