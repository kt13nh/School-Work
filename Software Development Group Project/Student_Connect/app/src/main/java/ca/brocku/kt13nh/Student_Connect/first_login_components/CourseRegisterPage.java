package ca.brocku.kt13nh.Student_Connect.first_login_components;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import ca.brocku.kt13nh.Student_Connect.R;

/**
 * This is for the registering courses page(activity), only seen one time per user.
 */

public class CourseRegisterPage extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_courses = mFirebaseDatabase.getReference().child("Courses");
    private ArrayList<String> courses = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_register_page);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Student Connect");
        setSupportActionBar(toolbar);
        getDatabaseInfo();
        setListeners();

    }//onCreate

    //disable back button
    @Override
    public void onBackPressed() {}

    //initialize database info into the courses arraylist.
    private void getDatabaseInfo(){
        table_courses.addListenerForSingleValueEvent(new ValueEventListener() {
            //add courses into the courses arraylist
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    courses.add(snapshot.getKey().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set the button listeners for next and cancel
    private void setListeners(){
        Button next = (Button) findViewById(R.id.nextButton);
        Button previous = (Button) findViewById(R.id.previousButton);

        final MaterialEditText editCourse1, editCourse2, editCourse3, editCourse4, editCourse5, editCourse6;
        editCourse1 = (MaterialEditText) findViewById(R.id.editCourse1);
        editCourse2 = (MaterialEditText) findViewById(R.id.editCourse2);
        editCourse3 = (MaterialEditText) findViewById(R.id.editCourse3);
        editCourse4 = (MaterialEditText) findViewById(R.id.editCourse4);
        editCourse5 = (MaterialEditText) findViewById(R.id.editCourse5);
        editCourse6 = (MaterialEditText) findViewById(R.id.editCourse6);

        //Button listener on Next button
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //go to next activity (register interested hobbies page)
                //startActivity(new Intent(CourseRegisterPage.this,HobbiesRegisterPage.class));
                String course1 = editCourse1.getText().toString().toUpperCase();
                String course2 = editCourse2.getText().toString().toUpperCase();
                String course3 = editCourse3.getText().toString().toUpperCase();
                String course4 = editCourse4.getText().toString().toUpperCase();
                String course5 = editCourse5.getText().toString().toUpperCase();
                String course6 = editCourse6.getText().toString().toUpperCase();
                //check to see if any of the courses are not valid. If they are, then don't proceed
                if (checkValidCourses(course1, course2, course3, course4, course5, course6)) {
                    Intent courseRegisterPage = new Intent(getBaseContext(),HobbiesRegisterPage.class);
                    courseRegisterPage.putExtra("course1",course1);
                    courseRegisterPage.putExtra("course2",course2);
                    courseRegisterPage.putExtra("course3",course3);
                    courseRegisterPage.putExtra("course4",course4);
                    courseRegisterPage.putExtra("course5",course5);
                    courseRegisterPage.putExtra("course6",course6);
                    getBaseContext().startActivity(courseRegisterPage);
                    finish();
                }

            }
        });

        //Button listener on Previous button
        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //go to previous activity
                FirebaseAuth.getInstance().signOut();
                finish();   //go to previous activity that called this register page
            }
        });
    }

    /*
    * Simple method to check if any of the supplied courses are valid courses
    * */
    private boolean checkValidCourses(String c1, String c2, String c3, String c4, String c5, String c6){
        if(!c1.trim().isEmpty()){
            if(!courses.contains(c1)){
                return false;
            }
        }
        if(!c2.trim().isEmpty()){
            if(!courses.contains(c2)){
                return false;
            }
        }
        if(!c3.trim().isEmpty()){
            if(!courses.contains(c3)){
                return false;
            }
        }
        if(!c4.trim().isEmpty()){
            if(!courses.contains(c4)){
                return false;
            }
        }
        if(!c5.trim().isEmpty()){
            if(!courses.contains(c5)){
                return false;
            }
        }
        if(!c6.trim().isEmpty()){
            if(!courses.contains(c6)){
                return false;
            }
        }
        return true;
    }



}//CourseRegisterPage
