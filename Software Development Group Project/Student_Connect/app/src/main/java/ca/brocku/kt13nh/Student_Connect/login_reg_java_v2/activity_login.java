package ca.brocku.kt13nh.Student_Connect.login_reg_java_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.provider.FirebaseInitProvider;
import com.rengwuxian.materialedittext.MaterialEditText;

import ca.brocku.kt13nh.Student_Connect.MainActivity;
import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.base_interface_java_v3.NavBar;
import ca.brocku.kt13nh.Student_Connect.first_login_components.CourseRegisterPage;

/*
* Login functions for Student connect
* */
public class activity_login extends AppCompatActivity {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference table_user = database.getReference("User");
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    //default Override to onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //initialize buttons
        initButtons();
    }

    /*
    * initialize login button and forgot password buttons
    * */
    private void initButtons(){
        //progress dialog to display progress to user
        final ProgressDialog progressDialog = new ProgressDialog(this);

        final EditText editEmail = (MaterialEditText)findViewById(R.id.editEmail);
        final EditText editPassword = (MaterialEditText)findViewById(R.id.editPassword);
        Button loginButton = (Button)findViewById(R.id.loginButton);
        Button forgotPasswordButton = (Button)findViewById(R.id.forgotPasswordButton);
        //for reset password button to display the forgot password screen
        forgotPasswordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent activity_forgotpassword = new Intent(activity_login.this, ForgotPassword.class);
                startActivity(activity_forgotpassword);
            }
        });
        //////////////////////////
        //for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = editEmail.getText().toString();
                final String password = editPassword.getText().toString();
                //ensure length of password is not empty, and email is of the format brocku.ca
                if(password.length()>0&&email.matches("^[a-zA-Z0-9._%+-]+@brocku.ca$")) {
                    progressDialog.setMessage("Logging in, Please Wait...");
                    progressDialog.show();
                    //attempt to sign in with user credentials
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {

                            mAuth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(activity_login.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                            if (task.isSuccessful()&&user.isEmailVerified()) {
                                                Toast.makeText(activity_login.this, "Welcome!", Toast.LENGTH_LONG).show();
                                                String first_login =dataSnapshot.child(user.getUid()).child("first_login").getValue().toString();
                                                if(first_login.equals("true")){
                                                    Intent activity_course_register = new Intent(activity_login.this, CourseRegisterPage.class);
                                                    startActivity(activity_course_register);
                                                }
                                                else{
                                                    Intent activity_home = new Intent(activity_login.this, NavBar.class);
                                                    startActivity(activity_home);
                                                }
                                                //return to the home screen if sign in is complete
                                                finish();
                                            }
                                            else if(task.isSuccessful()&&!user.isEmailVerified()){
                                                //if credentials are correct, but the user is not verified, log them out
                                                //and notify them through a toast
                                                mAuth.signOut();
                                                Toast.makeText(activity_login.this, "This Email is not Verified!", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(activity_login.this, "Incorrect login or password!", Toast.LENGTH_SHORT).show();
                                            }
                                            //dismiss dialog
                                            progressDialog.dismiss();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                //basic conditions to ensure user is inputting correct email and password format
                else if(!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+@brocku.ca$")){
                    Toast.makeText(activity_login.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(activity_login.this, "Please enter your password.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}