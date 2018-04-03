package ca.brocku.kt13nh.Student_Connect.drawer_components;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.util.ArrayList;

import ca.brocku.kt13nh.Student_Connect.R;

/**
 * This class used for course page in nav drawer, user can manage their courses on this page.
 *
 * If the user wants to add a course then they type in the course code and click add.
 *
 * If the user wants to delete a course, then they select one or more courses to remove then click
 * delete button at the bottom, and if they do this then a confirmation dialog will pop up to make
 * sure they want to delete the course, if yes then proceed with deletion.
 */

public class CourseFragmentPage extends ListFragment {

    //courses entered by user stored here, this should change to appropriate type when we want to use
    // the course code
    private FragmentActivity activity;
    private ArrayList<String> list = new ArrayList<String>();
    ArrayList<String> courses;
    ArrayList<String> courseNames;
    ArrayList<String> user_courses;
    private ArrayAdapter<String> adapter;
    private AutoCompleteTextView edit;
    private SparseBooleanArray checkedItemPositions;
    private int itemCount;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_courses = mFirebaseDatabase.getReference().child("Courses");
    private DatabaseReference table_user= mFirebaseDatabase.getReference().child("User");
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    private ArrayAdapter<String> courseAdapter;
    private ListView courseListView;

    //override create view to initialize listeners and database information
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.courses_fragment_page, container, false);
        getDatabaseInfo();
        initializeComponents();
        setListView();
        setListeners(v);
        setListAdapter(adapter);

        return v;

    }//onCreateView

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Courses");

    }//on ViewCreated

    private void initializeComponents(){
        activity = getActivity();
        adapter = new ArrayAdapter<String>(getActivity().getApplicationContext()
                ,android.R.layout.simple_list_item_multiple_choice,list);   //used to set courses to ListView

    }

    /*
    * Set button listeners and add constraints for the user in case they have entered the wrong
    * information
    * */
    private void setListeners(View v){
        ImageButton btnAdd = (ImageButton) v.findViewById(R.id.btnAddCourse);  //add course button
        Button btnDel = (Button) v.findViewById(R.id.btnDel); //delete course button
        //On click listener for Add course button
        View.OnClickListener listenerAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String UID = currUser.getUid().toString();
                String courseCode = edit.getText().toString().toUpperCase();
                int courseNameIndex = getIndex(courseCode);
                String courseName = courseNames.get(courseNameIndex).toString();
                if(user_courses.size()<6) {
                    if (courses.contains(courseCode) && !user_courses.contains(courseCode)) {
                        table_user.child(UID).child("enrolled").child(courseCode).setValue(courseName);
                        edit.setText("");
                        adapter.notifyDataSetChanged();
                        Toast.makeText(v.getContext(), "Course added!", Toast.LENGTH_SHORT)
                                .show();
                    } else if (user_courses.contains(courseCode)) {
                        Toast.makeText(v.getContext(), "You are already enrolled in that " +
                                "course!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(v.getContext(), "Course does not exist in database" +
                                "!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(v.getContext(), "You are already enrolled in 6 courses!" +
                            "", Toast.LENGTH_SHORT).show();

                }
            }
        };

        //OnClick listener for Delete course/courses button
        View.OnClickListener listenerDel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItemPositions = getListView().getCheckedItemPositions();
                itemCount = getListView().getCount();

                //alert dialog pops up to confirm if user wants to delete selected course
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete Course");
                alert.setMessage("Are you sure you want to delete this course?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //remove course from their list of courses in database
                        //continue with delete from list view
                        for(int i=itemCount-1; i >= 0; i--){
                            if(checkedItemPositions.get(i)){
                                String courseName = adapter.getItem(i).toString().substring(0,8);
                                deleteCourse(courseName);
                            }
                        }
                        checkedItemPositions.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                //if user hits no, return to courses fragment page
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        };
        btnAdd.setOnClickListener(listenerAdd); //set event listener for add course button
        btnDel.setOnClickListener(listenerDel); //set even listener for delete course button
    }

    //set the list view of the enrolled courses, making sure to display the course code, course name
    private void setListView() {
        String UID = currUser.getUid().toString();
        //set table listener for the enrolled table to ensure data is consistent in real time
        table_user.child(UID).child("enrolled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String[] values = new String[(int)dataSnapshot.getChildrenCount()];
                courseListView = (ListView)activity.findViewById(android.R.id.list);
                int pos=0;
                //for each datasnap shot get information and add to the adapter
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String code = snapshot.getKey().toString();
                    String name = snapshot.getValue().toString();
                    System.out.println(code);
                    values[pos]=code+"\n"+name;
                    pos++;
                }
                adapter.clear();
                adapter.addAll(values);
                courseListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
    * Initialize the database information and add an event listener for the courses in able to
    * update auto complete for the user in realtime.
    * */
    private void getDatabaseInfo(){

        table_courses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                courses = new ArrayList<String>();
                courseNames = new ArrayList<String>();
                edit = (AutoCompleteTextView) getActivity().findViewById(R.id.courseEditItem);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    courses.add(snapshot.getKey().toString());
                    courseNames.add(snapshot.child("CourseName").getValue().toString());
                    System.out.println("noooooooooooooo"+snapshot.getValue().toString());
                }
                //set the adapter for the components within the adapters
                courseAdapter = new ArrayAdapter<String>
                        (CourseFragmentPage.this.getActivity(),android.R.layout.simple_list_item_1,courses);
                courseAdapter.addAll(courses);
                edit.setAdapter(courseAdapter);
                initUserCoursesInfo();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    * set the user courses info to obtain a list of the user's enrolled courses to be stored within
    * arraylist user_courses
    * */
    private void initUserCoursesInfo(){
        table_user.child(currUser.getUid()).child("enrolled").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_courses = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user_courses.add(snapshot.getKey().toString());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /*
    * Delete course from user's table
    * */
    private void deleteCourse(String courseName){
        String UID = currUser.getUid().toString();
        table_user.child(UID).child("enrolled").child(courseName).removeValue();
    }


    /*
    * Get the index of the course in the array list courses. This will be used to obtain
    * the course names rather than the course codes
    * */
    private int getIndex(String courseCode){
        for(int i=0;i<courses.size();i++){
            if(courses.get(i).toString().equals(courseCode)){
                return i;
            }
        }
        return 0;
    }

}//CourseFragmentPage