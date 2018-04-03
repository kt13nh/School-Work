package ca.brocku.kt13nh.Student_Connect.floating_action_button_components;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ca.brocku.kt13nh.Student_Connect.R;

/**
 * This class used for when a user wants to post a new question.
 * Requires question title(editTitleField), question description (editDescriptionField), and whether
 * they want to post the question anonymously or not (anonymousCheckBox).
 * Upload image button handler still pending, Ronny has part for this***************
 */
public class NewQuestion extends Activity{

    private Spinner spinnerCourses;
    private EditText editTitleField, editDescriptionField;
    private CheckBox anonymousCheckbox;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_questions = mFirebaseDatabase.getReference().child("Questions");
    private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_question_window);

        //Used for adjusting popup window based on user's cellphone screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        //if we want window screen to be 80% of our phone's screen, multiply by 0.8
        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        //for drop down menu(in the pop up window)
        addListenerOnButton();
        initDropDownCourses();

    }//onCreate

    /**
     * Method below adds listener on the buttons and retrieves the information entered by the user.
     */
    public void addListenerOnButton(){

        //for each part of form (Spinner, buttons, edittext fields, checkbox)
        spinnerCourses = (Spinner) findViewById(R.id.spinnerCourses);

        Button postButton = (Button) findViewById(R.id.postButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        editTitleField = (EditText) findViewById(R.id.editQATitle);
        editDescriptionField = (EditText) findViewById(R.id.editQADescription);

        anonymousCheckbox = (CheckBox) findViewById(R.id.checkBoxQAAnonymous);

        //add listener on cancel button
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //add listener on post button
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(NewQuestion.this, "Post button pressed :" +
                                "\nCourse selected: " + String.valueOf(spinnerCourses.getSelectedItem()),
                        Toast.LENGTH_SHORT).show();
                //information stored after user clicks POST

                //convert values from form to string
                final String spinnerCourseSelected = spinnerCourses.getSelectedItem().toString();
                final String questionTitle = editTitleField.getText().toString();
                final String questionDescription = editDescriptionField.getText().toString();

                //if checkbox checked, user wants to post anonymously (true if checked, false otherwise)
                final boolean anonymous = anonymousCheckbox.isChecked();
                final Map<String, String> questionData = new HashMap<>();
                final String questionID = UUID.randomUUID().toString();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String UID = currUser.getUid();
                        String firstName = dataSnapshot.child(UID).child("first_name").getValue().toString();
                        String lastName = dataSnapshot.child(UID).child("last_name").getValue().toString();
                        String fullName = firstName + " " + lastName;
                        Date date = Calendar.getInstance().getTime();
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        String formattedDate = df.format(date);
                        String email = currUser.getEmail();

                        questionData.put("title", questionTitle);
                        questionData.put("date", formattedDate);
                        questionData.put("description", questionDescription);
                        questionData.put("course", spinnerCourseSelected);
                        questionData.put("user", fullName);
                        questionData.put("email",email);
                        if(anonymous==true)
                            questionData.put("anonymous","true");
                        else
                            questionData.put("anonymous","false");

                        table_questions.child(questionID).setValue(questionData);
                        table_user.child(UID).child("qa").child(questionID).setValue(questionTitle);
                        Toast.makeText(NewQuestion.this, "Question created!", Toast.LENGTH_SHORT).show();


                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

            }
        });

    }//addListenerOnButton

    /**
     * Adds listener to enrolled section in User's table that gets the courses the current user is
     * enrolled in and displays them in the dropdown
     */
    private void initDropDownCourses(){

        final String UID = currUser.getUid();
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.textcenter, R.id.name_text);

        table_user.child(UID).child("enrolled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] values = new String[(int)dataSnapshot.getChildrenCount()];
                int pos=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name = snapshot.getKey().toString();
                    values[pos]=name;
                    pos++;
                }
                adapter.addAll(values);
                spinnerCourses.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}//PopUpAdd