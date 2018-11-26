package telecommunication.alliance.bat.com.projectbat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


import java.util.regex.Pattern;

public class ForgotPassword extends AppCompatActivity {
    private static final String TAG = "FORGOT_PASSWORD_TAG";
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Log.d(TAG, "onCreate");


        email = findViewById(R.id.forgotPasswordEmailInput);
        Button sendEmail = findViewById(R.id.forgotPasswordButton);

        sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_s = email.getText().toString();
                if(!email_s.equals("")){
                    if(isValidEmail(email_s)){
                        Log.d(TAG, "Sending password reset email");
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email_s).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    try {
                                        throw task.getException();
                                    }
                                    catch(FirebaseAuthInvalidUserException noEmail){
                                        Log.d(TAG, "Email not registered");
                                        Toast.makeText(ForgotPassword.this, "Email not Registered", Toast.LENGTH_SHORT).show();
                                    }
                                    catch(Exception e){
                                        Log.d(TAG, e.getMessage());
                                    }
                                }else{
                                    Log.d(TAG, "Email Sent Successful");
                                    Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                    } else{
                        Log.d(TAG, "Invalid email format");
                        Toast.makeText(ForgotPassword.this, "Invalid format", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Invalid email format");
                    Toast.makeText(ForgotPassword.this, "Email not provided", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "END");
            }
        });
    }

    private boolean isValidEmail(String s){
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+  // from OWASP regex documentation
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        return !(s == null) && pat.matcher(s).matches();
    }
}
