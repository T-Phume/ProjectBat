package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "REGISTER_ACTIVITY_TAG";

    private TextView email;
    private TextView password1;
    private TextView password2;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate");

        email = findViewById(R.id.registerEmailInput);
        password1 = findViewById(R.id.registerPasswordInput1);
        password2 = findViewById(R.id.registerPasswordInput2);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty(email.getText().toString())  // Check if all fields have been entered
                        && !isEmpty(password1.getText().toString())
                        && !isEmpty(password2.getText().toString())){
                    if(isValidEmail(email.getText().toString())){  // Check for valid email format
                        // Check if password match
                        if(doStringsMatch(password1.getText().toString(), password2.getText().toString())){
                            //Check length of password
                            if(password1.length() <= 16 && password1.length() <= 6){
                                registerEmail(email.getText().toString(), password1.getText().toString());
                            } else{
                                Toast.makeText(RegisterActivity.this, "Length of password should be between 6 and 16",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            Toast.makeText(RegisterActivity.this, "Password does not match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else{
                        Toast.makeText(RegisterActivity.this, "Invalid email format",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerEmail(String email, String password){
        Log.d(TAG, "Registering Email");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "REGISTER SUCCESSFUL");
                    sendVerificationEmail();
                    FirebaseAuth.getInstance().signOut();
                    redirectLoginScreen();
                } else{
                    Log.d(TAG, "REGISTER UNSUCCESSFUL");
                }
            }
        });
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

    private boolean doStringsMatch(String s1, String s2){
        return s1.equals(s2);
    }

    private boolean isEmpty(String string){
        return string.equals("");
    }
}
