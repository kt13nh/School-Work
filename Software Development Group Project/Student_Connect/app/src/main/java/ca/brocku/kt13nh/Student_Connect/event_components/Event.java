package ca.brocku.kt13nh.Student_Connect.event_components;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import ca.brocku.kt13nh.Student_Connect.R;

/*
*
* This class is used for the purpose of enabling users to be able to join a created event
* along with the displaying the correct information of each of the events to the user in the
* form of textbox, and a listview of the names of the people joined
* */
public class Event extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_events = mFirebaseDatabase.getReference().child("Events");
    private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();

    //create view, set the toolbar and titles,
    //initialization
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_event);
        String eventTitle = getIntent().getStringExtra("title");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle((CharSequence) eventTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setList();
        setText();
        checkCreator();
        setToggleButtonListeners();
    }

    /*
    * Set toggle button listeners and display the correct status to the user based on if they
    * have already joined the event.
    * */
    private void setToggleButtonListeners(){
        final String eventID=getIntent().getStringExtra("eventID");
        final String eventTitle = getIntent().getStringExtra("title");
        final String UID = currUser.getUid();
        final String email = currUser.getEmail();
        ToggleButton toggleButton = (ToggleButton)findViewById(R.id.toggleButton);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    System.out.println("Joined");
                    table_events.child(eventID).child("joined").child(UID).setValue(email);
                    table_user.child(UID).child("events").child(eventID).setValue(eventTitle);
                    setList();
                   }
                else
                {
                    table_events.child(eventID).child("joined").child(UID).removeValue();
                    table_user.child(UID).child("events").child(eventID).removeValue();
                    setList();
                }

            }
        });
    }
    /*
    * set the arraylist adapters and populate the listview with the users that are currently
    * planning to attend the event
    * */
    private void setList() {
        Intent intent = getIntent();
        final String eventID = intent.getStringExtra("eventID");
        //this is the adapter to be used for the list display
        final ArrayAdapter<String> adapter = new ArrayAdapter(this, R.layout.textcenter, R.id.name_text);
        //listen for changes in the events table for the users that have joined to update realtime
        table_events.child(eventID).child("joined").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String[] values = new String[(int)dataSnapshot.getChildrenCount()];
                ListView listview = (ListView) findViewById(R.id.listview);
                int pos=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name = snapshot.getValue().toString();
                    values[pos]=name;
                    pos++;
                }
                adapter.clear();
                adapter.addAll(values);
                listview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /*
    * Set the correct text in the textviews and display the button correctly for whether or not the
    * person has already joined the vent
    * */
    private void setText() {

        //get extra strings from the intent for the strings to display
        String eventDescription = getIntent().getStringExtra("description");
        String location = getIntent().getStringExtra("location");
        String date = getIntent().getStringExtra("date");
        String time = getIntent().getStringExtra("time");
        String joined = getIntent().getStringExtra("joined");

        ((TextView) findViewById(R.id.description)).setText(eventDescription);
        ((TextView) findViewById(R.id.locationView)).setText("Location: "+location);
        ((TextView) findViewById(R.id.dateView)).setText(date);
        ((TextView) findViewById(R.id.timeView)).setText(time);

        //display button to be checked if use has already joined
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        if(joined.equals("true")){
            toggleButton.setChecked(true);
        }
        else{
            toggleButton.setChecked(false);
        }
    }

    //check if the current user of the app is also the creator of the viewed event
    private void checkCreator(){
        String eventEmail = getIntent().getStringExtra("email");
        String userEmail = currUser.getEmail();
        ToggleButton button = (ToggleButton)findViewById(R.id.toggleButton);
        if(eventEmail.equals(userEmail)){
            button.setText("Joined!");
            button.setEnabled(false);
        }
    }
}
