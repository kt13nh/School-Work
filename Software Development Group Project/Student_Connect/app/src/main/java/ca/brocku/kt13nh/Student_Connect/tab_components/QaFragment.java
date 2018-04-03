package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;

/*
*
* This fragment is the tab that is to be display to the user to show them all of the courses they
* have registered for that have questions to be answered.
* */
public class QaFragment extends Fragment {
    private View view;
    //to hold the list of enrolled courses
    ArrayList<String> enrolled = new ArrayList<>();
    private String title;//String for tab title

    private static RecyclerView recyclerView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatroomDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    private String userId;
    private DatabaseReference table_user_enrolled;
   // private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");
    private QaAdapter qaAdapter;

    public QaFragment(){}
    @SuppressLint("ValidFragment")
    public QaFragment(String title) {
        this.title = title;//Setting tab title
    }

    //create view and initialize firebase
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dummy_fragment, container, false);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        if(currUser!=null) {
            userId = currUser.getUid();
            mChatroomDatabaseReference = mFirebaseDatabase.getReference().child("Questions");
            table_user_enrolled = mFirebaseDatabase.getReference().child("User").child(userId).child("enrolled");
        }
        setEnrolled();


        return view;

    }

    //read from data base and set the enrolled courses into the array list, and display
    private void setEnrolled(){
        table_user_enrolled.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    enrolled.add(snapshot.getKey());
                }
                setRecyclerView();
                attachDatabaseReadListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //Setting recycler view
    private void setRecyclerView() {


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items

        qaAdapter = new QaAdapter(getActivity());
        recyclerView.setAdapter(qaAdapter);// set adapter on recyclerview

    }

    /**
     * Attaches listener to questions table. When a question is added, that chatroom appears in
     * the inbox
     */
    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String questionID = dataSnapshot.getKey();
                    String questionTitle = (String) dataSnapshot.child("title").getValue().toString();
                    String course = (String) dataSnapshot.child("course").getValue().toString();
                    String description = (String) dataSnapshot.child("description").getValue().toString();
                    String askedBy = (String) dataSnapshot.child("user").getValue().toString();
                    String time = (String) dataSnapshot.child("date").getValue().toString();
                    String anonymous = (String) dataSnapshot.child("anonymous").getValue().toString();
                    String UID = currUser.getUid();

                    Map<String, String> questionData = new HashMap<>();
                    questionData.put("questionID", questionID);
                    questionData.put("title", questionTitle);
                    questionData.put("course", course);
                    questionData.put("description", description);
                    if(anonymous.equals("true")) {
                        questionData.put("askedBy", "anonymous");
                    }
                    else
                        questionData.put("askedBy",askedBy);
                    questionData.put("time", time);

                    System.out.println(enrolled.size());
                    System.out.println("course: "+course+" "+enrolled.contains(course));
                    if(enrolled.contains(course)) {
                        qaAdapter.addQuestion(questionData);
                        recyclerView.setAdapter(qaAdapter);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mChatroomDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }


}
