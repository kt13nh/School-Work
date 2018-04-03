package ca.brocku.kt13nh.Student_Connect.login_reg_java_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import ca.brocku.kt13nh.Student_Connect.R;

/**
 * Created by Tram
 */

public class ForgotPassword extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgotpassword);

    }

    //initialize forgot password button
    private void initForgotButton(){
        Button forgotPasswordButton = (Button)findViewById(R.id.forgotPasswordButton);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final MaterialEditText editEmail = (MaterialEditText)findViewById(R.id.editEmail);
        //set listener
        forgotPasswordButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                //ensure field is not empty
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Please Enter your registered email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //attempt to send a password reset email to the specified email address
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //listener to make toast if reset email was sent
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassword.this, "Instructions have been sent to your email!", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    //toast to notify user if it did not work.
                                    Toast.makeText(ForgotPassword.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
