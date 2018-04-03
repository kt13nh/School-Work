package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.drawer_components.HobbiesFragmentPage;

/*
* This is the class to be display for the tabs of "Events". This will sort the list based on which
* events they have joined, and the date of the events.
* */
public class EventsFragment extends Fragment {

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_events = mFirebaseDatabase.getReference().child("Events");
    private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");

    private ChildEventListener mChildEventListener;
    private ChildEventListener eventJoinListener;
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    private Events_adapter events_adapter;
    private View view;
    private ArrayList<Map<String, String>> joined;
    private ArrayList<Map<String, String>> notJoined;
    private String title;//String for tab title

    private static RecyclerView recyclerView;

    public EventsFragment() {
    }

    @SuppressLint("ValidFragment")
    public EventsFragment(String title) {
        this.title = title;//Setting tab title
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                    Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dummy_fragment, container, false);

        setRecyclerView();
        attachDatabaseReadListener();
        return view;

    }
    //Setting recycler view
    private void setRecyclerView() {


        //String [] events= {"Event Title","Event Title","Event Title"};
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items


        //ArrayList<String> arrayList = new ArrayList<>();
        //for (int i = 0; i < events.length; i++) {
        //    arrayList.add(events[i]);//Adding items to recycler view
        //}


        events_adapter = new Events_adapter(getActivity());

        recyclerView.setAdapter(events_adapter);// set adapter on recyclerview

    }

    /**
     * Attaches listener to events table, add to list of events for user
     */
    private void attachDatabaseReadListener(){
        table_events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                events_adapter.clear();
                joined = new ArrayList<Map<String,String>>();
                notJoined = new ArrayList<Map<String,String>>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    load(snapshot);
                }
                for(Map<String, String> event: joined){
                    events_adapter.addEventData(event);
                }
                for(Map<String, String> event: notJoined){
                    events_adapter.addEventData(event);
                }
                recyclerView.setAdapter(events_adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    /*
    * Load all of the required data from the datasnapshot parameter into a map, and add that map
    * to the list of joined or not joined depending on if they have already joined the event
    * */
    private void load(DataSnapshot dataSnapshot){
        String eventID = dataSnapshot.getKey();
        String title = (String) dataSnapshot.child("title").getValue().toString();
        String date = (String) dataSnapshot.child("date").getValue().toString();
        String description = (String) dataSnapshot.child("description").getValue().toString();
        String location = (String) dataSnapshot.child("location").getValue().toString();
        String time = (String) dataSnapshot.child("time").getValue().toString();
        String email = (String) dataSnapshot.child("email").getValue().toString();
        String UID = currUser.getUid();
        //create a map to hold all of the event information
        Map<String, String> eventData = new HashMap<>();
        eventData.put("eventID", eventID);
        eventData.put("title", title);
        eventData.put("date", date);
        eventData.put("description", description);
        eventData.put("location", location);
        eventData.put("time", time);
        eventData.put("email",email);
        //if this is the creator then auto matically join
        if(dataSnapshot.child("joined").hasChild(UID)){
            eventData.put("joined","true");
            joined.add(eventData);
            sort(joined);
        }
        //if user has not already joined the event
        else {
            eventData.put("joined","false");
            notJoined.add(eventData);
            sort(notJoined);
        }
    }

    /*
    * Array list to sort the maps inside of the arraylist
    * */
    private void sort(ArrayList<Map<String,String>> map){

        Collections.sort(map, new Comparator<Map>() {
            @Override
            public int compare(Map map1, Map map2) {
                String date1String = map1.get("date").toString();
                String date2String = map2.get("date").toString();
                try {
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date1String);
                    Date date2 = new SimpleDateFormat("dd/MM/yyyy").parse(date2String);
                    return(date1.compareTo(date2));
                }
                catch(ParseException ex){
                    throw new IllegalArgumentException(ex);
                }

            }
        });

    }
}
