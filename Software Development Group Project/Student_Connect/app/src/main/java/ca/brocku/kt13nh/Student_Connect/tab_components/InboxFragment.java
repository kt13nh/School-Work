package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.List;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;


public class InboxFragment extends Fragment {
    private View view;

    private String title;//String for tab title

    private static RecyclerView recyclerView;

    private TextView Tv;

    private OnItemClickListener mListener;

    private RecyclerView_Adapter chatroomListAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatroomDatabaseReference;
    private DatabaseReference mUserDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseUser user;

    private List<String> coursesEnrolledList;
    private List<String> privateChatsList;
    private List<String> eventsJoinedList;
    private List<String> hobbiesList;
    private Map<String, Object> chatrooms;

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);

        public void onLongItemClick(View view, int position);
    }

    //public InboxFragment(){}
    @SuppressLint("ValidFragment")
    public InboxFragment(String title) {
        this.title = title;//Setting tab title
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dummy_fragment, container, false);

        coursesEnrolledList = new ArrayList<>();
        privateChatsList = new ArrayList<>();
        eventsJoinedList = new ArrayList<>();
        hobbiesList = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mChatroomDatabaseReference = mFirebaseDatabase.getReference().child("Chatrooms");
        mUserDatabaseReference = mFirebaseDatabase.getReference().child("User");
        user = FirebaseAuth.getInstance().getCurrentUser();

        setRecyclerView();
        attachDatabaseReadListeners();
        return view;

    }

    //Setting recycler view
    private void setRecyclerView() {

        recyclerView = (RecyclerView) view
                .findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));//Linear Items

        chatroomListAdapter = new RecyclerView_Adapter(getActivity());
        recyclerView.setAdapter(chatroomListAdapter);// set adapter on recyclerview

    }

    /**
     * Attaches listener to chatrooms table. When a chatroom is added, that chatroom appears in
     * the inbox
     */
    private void attachDatabaseReadListeners() {
        //Chatroom table listener that gets a list of all chatrooms whenever any
        //new chatroom is added
        mChatroomDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                chatrooms = (Map<String, Object>) dataSnapshot.getValue();
                attachUserTableListener();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void attachUserTableListener(){
        mUserDatabaseReference.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> enrolledMapping = new HashMap<>();

                //Gets all enrolled courses, events joined, hobbies added and private chats joined for
                //this user from the DB
                if(dataSnapshot.child("enrolled").getValue() != null &&
                        !dataSnapshot.child("enrolled").getValue().toString().equals(""))
                    enrolledMapping= (Map<String, String>) dataSnapshot.child("enrolled").getValue();

                Map<String, String> privateChatsMapping = new HashMap<>();
                if(dataSnapshot.child("private_chats").getValue() != null &&
                        !dataSnapshot.child("private_chats").getValue().toString().equals(""))
                    privateChatsMapping = (Map<String, String>) dataSnapshot.child("private_chats").getValue();

                Map<String, String> joinedEventsMapping = new HashMap<>();
                if(dataSnapshot.child("events").getValue() != null &&
                        !dataSnapshot.child("events").getValue().toString().equals("")) {
                    joinedEventsMapping = (Map<String, String>) dataSnapshot.child("events").getValue();
                }
                Map<String, String> hobbiesMapping = new HashMap<>();
                if(dataSnapshot.child("hobbies").getValue() != null &&
                        !dataSnapshot.child("hobbies").getValue().toString().equals("")) {
                    hobbiesMapping = (Map<String, String>) dataSnapshot.child("hobbies").getValue();
                }
                //Makes list of enrolled courseIDs, joined private chat IDs, joined event IDs,
                // and hobbies
                coursesEnrolledList.clear();
                if(enrolledMapping != null) {
                    for (String courseID : enrolledMapping.keySet()) {
                        coursesEnrolledList.add(courseID);
                    }
                }

                privateChatsList.clear();
                if(privateChatsMapping != null) {
                    for (String privateChat : privateChatsMapping.keySet()) {
                        privateChatsList.add(privateChat);
                    }
                }

                eventsJoinedList.clear();
                if(joinedEventsMapping != null) {
                    for (String event : joinedEventsMapping.keySet()) {
                        eventsJoinedList.add(event);
                    }
                }

                hobbiesList.clear();
                if(hobbiesMapping != null) {
                    for (String hobby : hobbiesMapping.keySet()) {
                        hobbiesList.add(hobby);
                    }
                }

                chatroomListAdapter.clearChatrooms();
                for (Map.Entry<String, Object> chatroom : chatrooms.entrySet()) {
                    String chatID = chatroom.getKey();
                    Map<String, Object> chatroomObject = (Map<String, Object>) chatroom.getValue();
                    String chatName = (String) chatroomObject.get("ChatName");
                    boolean isPublic = (boolean) chatroomObject.get("isPublic");

                    String admin = "";
                    if(!isPublic)
                        admin = (String) chatroomObject.get("admin");

                    //Only displays the chatroom if user has enrolled in the course/joined the
                    // event/been invited to the private chat
                    if (coursesEnrolledList.contains(chatName) || privateChatsList.contains(chatID)
                            || eventsJoinedList.contains(chatID) || hobbiesList.contains(chatID)) {
                        Map<String, Object> chatroomData = new HashMap<>();
                        chatroomData.put("ChatID", chatID);
                        chatroomData.put("ChatName", chatName);
                        chatroomData.put("isPublic", isPublic);
                        chatroomData.put("admin", admin);
                        chatroomListAdapter.addChatroom(chatroomData);
                        recyclerView.setAdapter(chatroomListAdapter);
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}







