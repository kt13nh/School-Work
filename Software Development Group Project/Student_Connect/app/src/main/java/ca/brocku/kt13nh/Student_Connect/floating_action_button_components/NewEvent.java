package ca.brocku.kt13nh.Student_Connect.floating_action_button_components;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.tab_components.EventsFragment;


/**
 * Author: Goal Diggers
 * This class is used for the pop up window when user wants to create a new Event
 * Able to use FragmentActiviy instead of Fragment for Nav Drawer?
 *
 */

public class NewEvent extends AppCompatActivity{

    private DatePicker date;
    private TimePicker time;
    private EditText editTitleField, editDescriptionField, editLocationField;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_events = mFirebaseDatabase.getReference().child("Events");
    private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");
    private DatabaseReference table_chatrooms = mFirebaseDatabase.getReference().child("Chatrooms");
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_event_window);

        //Used for adjusting popup window based on user's cellphone screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.7));//if we want window screen to be 80% of our phone's screen, multiply by 0.8

        //for drop down menu(in the pop up window)
        addListenersOnButtons();

    }//onCreate

    //Used for button on pop up window
    public void addListenersOnButtons(){

        //for each part of form (Spinner, buttons, edittext fields, checkbox)
        //spinnerEvents = (Spinner) findViewById(R.id.spinnerEvents);
        Button createButton = (Button) findViewById(R.id.createButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);

        date = (DatePicker) findViewById(R.id.datePicker);
        time = (TimePicker) findViewById(R.id.timePicker);
        time.setIs24HourView(true);
        editTitleField = (EditText) findViewById(R.id.editEventTitle);
        editDescriptionField = (EditText) findViewById(R.id.editEventDescription);
        editLocationField = (EditText) findViewById(R.id.editLocation);

        //Add listener on cancel button
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Add listener on post button, prints out course selected and that create button was pressed
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //everything below is only read and saved AFTER user clicks "Create"
                int day = date.getDayOfMonth(); //get selected day of the month
                int month = date.getMonth(); //get selected month
                int year = date.getYear(); //get selected year

                int hour = time.getHour();
                int minute = time.getMinute();
                //convert values from form to string
                //final String category = spinnerEvents.getSelectedItem().toString();
                final String eventTitle = editTitleField.getText().toString();
                final String description = editDescriptionField.getText().toString();
                final String location = editLocationField.getText().toString();

                //If checkbox checked, user wants to post anonymously (true if checked, false otherwise)
                //final boolean anonymous = anonymousCheckBox.isChecked();

                final String time = ""+hour+":"+minute;
                final String date = ""+day+"/"+month+"/"+year;
                final String eventID = UUID.randomUUID().toString();
                final String UID = currUser.getUid();
                final String email = currUser.getEmail();

                final Map<String, String> eventData = new HashMap<>();

                if(!eventTitle.equals("")&&!location.equals("")) {
                    table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            final String firstName = dataSnapshot.child(UID).child("first_name").getValue().toString();
                            final String lastName = dataSnapshot.child(UID).child("last_name").getValue().toString();
                            final String fullName = firstName + " " + lastName;

                            eventData.put("title", eventTitle);
                            eventData.put("date", date);
                            eventData.put("description", description);
                            eventData.put("location", location);
                            eventData.put("time", time);
                            eventData.put("email", email);
                            eventData.put("creator", fullName);
                            eventData.put("joined","");
                            addChatRoom(eventID,eventTitle);
                            table_events.child(eventID).setValue(eventData);
                            table_events.child(eventID).child("joined").child(UID).setValue(email);
                            table_user.child(UID).child("events").child(eventID).setValue(eventTitle);
                            Toast.makeText(NewEvent.this, "Event created!", Toast.LENGTH_SHORT).show();

                            finish();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else{
                    Toast.makeText(NewEvent.this, "Please make sure to include" +
                            " an event title and a location for the event!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }//addListenerOnButton

    private void addChatRoom(String eventID, String title){
        final Map<String, Object> eventData = new HashMap<>();
        eventData.put("ChatName", (Object)title);
        eventData.put("isPublic",true);
        table_chatrooms.child(eventID).setValue(eventData);
    }

}//PopUpAdd