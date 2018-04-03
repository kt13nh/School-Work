package ca.brocku.kt13nh.Student_Connect.login_reg_java_v2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import ca.brocku.kt13nh.Student_Connect.R;

/**
 * THis class is for the users to register their account. Upon registration a verification code
 * should be sent to their email where that needs to be authenticated in order for the
 * account to be active.
 */

public class activity_register extends AppCompatActivity {
    Button registerButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerButton = (Button)findViewById(R.id.registerButton);

        setRegisterButton();
    }

    //set listener for register button
    public void setRegisterButton(){
        final MaterialEditText firstNameText = (MaterialEditText)findViewById(R.id.editFirstName);
        final MaterialEditText lastNameText = (MaterialEditText)findViewById(R.id.editLastName);
        final MaterialEditText emailText = (MaterialEditText)findViewById(R.id.editEmail);
        final MaterialEditText confirmEmailText = (MaterialEditText)findViewById(R.id.editConfirmEmail);
        final MaterialEditText passwordText = (MaterialEditText)findViewById(R.id.editPassword);
        final MaterialEditText confirmPasswordText = (MaterialEditText)findViewById(R.id.editConfirmPassword);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                table_user.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String firstName=firstNameText.getText().toString().toLowerCase();
                        final String lastName=lastNameText.getText().toString().toLowerCase();
                        final String email = emailText.getText().toString().toLowerCase();
                        String confirmEmail = confirmEmailText.getText().toString().toLowerCase();
                        String password = passwordText.getText().toString();
                        String confirmPassword = confirmPasswordText.getText().toString();
                        //check to see if first and last name contain only letters
                        if(!firstName.matches("^[a-zA-Z]+$") || !lastName.matches("^[a-zA-Z]+$") ){
                            Toast.makeText(activity_register.this, "First name and Last name can only include letters!", Toast.LENGTH_SHORT).show();
                        }
                        //check to see if it is a brocku email
                        else if(!email.matches("^[a-zA-Z0-9._%+-]+@brocku.ca$")){
                            Toast.makeText(activity_register.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                        }
                        //check to see if emails are equal
                        else if(!email.equals(confirmEmail)){
                            Toast.makeText(activity_register.this, "Emails do not match", Toast.LENGTH_SHORT).show();
                        }
                        //check to see if passwords are the same
                        else if(!password.equals(confirmPassword)){
                            Toast.makeText(activity_register.this ,"Passwords do not match", Toast.LENGTH_SHORT).show();
                        }
                        //check to see if password is at least 8 characters
                        else if(password.length()<8){
                            Toast.makeText(activity_register.this, "Your password must have at least 8 characters.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //attempt to register
                            final ProgressDialog progressDialog = new ProgressDialog(activity_register.this);
                            progressDialog.setMessage("Registering, Please Wait...");
                            progressDialog.show();
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(activity_register.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            //checking if success
                                            if(task.isSuccessful()){
                                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                                user.sendEmailVerification();
                                                //call function to write user information into database
                                                writeDBInformation(user,table_user);
                                                UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().build();
                                                user.updateProfile(update).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task){}
                                                });
                                                Toast.makeText(activity_register.this,"A verification email has been sent!",Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                finish();
                                            }else{
                                                Toast.makeText(activity_register.this,"This email may already be registered!",Toast.LENGTH_LONG).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    //write database information, and set them to blank to start for the user
    public void writeDBInformation(FirebaseUser user,DatabaseReference table_user){
        final MaterialEditText firstNameText = (MaterialEditText)findViewById(R.id.editFirstName);
        final MaterialEditText lastNameText = (MaterialEditText)findViewById(R.id.editLastName);
        final MaterialEditText emailText = (MaterialEditText)findViewById(R.id.editEmail);
        final String firstName=firstNameText.getText().toString().toLowerCase();
        final String lastName=lastNameText.getText().toString().toLowerCase();
        final String email = emailText.getText().toString().toLowerCase();
        table_user.child(user.getUid().toString()).child("email").setValue(email.toLowerCase());
        table_user.child(user.getUid().toString()).child("first_name").setValue(firstName.toLowerCase());
        table_user.child(user.getUid().toString()).child("last_name").setValue(lastName.toLowerCase());
        table_user.child(user.getUid().toString()).child("report_count").setValue("0");
        table_user.child(user.getUid().toString()).child("enrolled").setValue("");
        table_user.child(user.getUid().toString()).child("hobbies").setValue("");
        table_user.child(user.getUid().toString()).child("qa").setValue("");
        table_user.child(user.getUid().toString()).child("events").setValue("");
        table_user.child(user.getUid().toString()).child("private_chats").setValue("");
        table_user.child(user.getUid().toString()).child("first_login").setValue("true");
    }
}