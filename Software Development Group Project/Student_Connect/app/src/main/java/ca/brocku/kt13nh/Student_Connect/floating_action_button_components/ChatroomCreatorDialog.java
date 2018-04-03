package ca.brocku.kt13nh.Student_Connect.floating_action_button_components;

/**
 * Author: Goal Diggers
 * Date: 3/20/2018
 * The dialog that is shown when users click add button to create new private chatrooms
 */

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;


public class ChatroomCreatorDialog extends Dialog {
    private List<Map<String, Object>> users;
    private ArrayAdapter<String> usersAdapter;

    private Context context;
    private MultiAutoCompleteTextView autoCompleteView;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatroomsReference;
    private DatabaseReference mUsersReference;

    private FirebaseUser currentUser;

    /**
     * Constructor for this Dialog that inherits from java's Dialog class
     *
     * @param context
     */
    public ChatroomCreatorDialog(final Context context) {
        super(context);
        // This is the layout XML file that describes your Dialog layout
        this.setContentView(R.layout.activity_chatroom_creator);
        this.context = context;
    }

    @Override
    /**
     * Creates the activity as well as initializes  all components used in the dialog
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_chatroom_creator);

        //Initializing all Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mChatroomsReference = mFirebaseDatabase.getReference().child("Chatrooms");
        mUsersReference = mFirebaseDatabase.getReference().child("User");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        //Sets autocomplete TextView to read from usersAdapter
        autoCompleteView = (findViewById(R.id.enterUsers));
        users = new ArrayList<>();

        List<String> userDisplayList = new ArrayList<>();
        usersAdapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_list_item_1, userDisplayList);
        autoCompleteView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        autoCompleteView.setThreshold(1);
        autoCompleteView.setAdapter(usersAdapter);
        addListeners();
        attachDatabaseListener();
    }


    /**
     * Attaches listeners to the 2 buttons in this dialog
     * Cancel button dismisses the dialog, whereas the create button adds a private chatroom to the
     * DB as long as the name specified is 1-16 chracters lng and users were entered in the correct
     * format
     */
    private void addListeners() {
        final EditText chatName = (EditText) findViewById(R.id.enterTitle);
        final MultiAutoCompleteTextView usersToAdd = (MultiAutoCompleteTextView)
                findViewById(R.id.enterUsers);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        Button createChatButton = (Button) findViewById(R.id.postButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        createChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Adds new chatroom to chatrooms table
                String usersToAddString = usersToAdd.getText().toString();
                if (!chatName.getText().toString().equals("")) {
                    if (usersToAddString.equals("") || usersToAddString.matches(
                            "(([a-zA-Z]+[\\s][a-zA-Z]+[\\s]*\\(" +
                                    "[a-z][a-z][0-9][0-9][a-z][a-z]@brocku.ca\\))+,*\\s*)+")) {

                        Map<String, Object> chatroomData = new HashMap<>();
                        chatroomData.put("ChatName", (Object) (chatName.getText().toString()));
                        chatroomData.put("isPublic", (Object) false);
                        chatroomData.put("admin", (Object) currentUser.getEmail());
                        DatabaseReference newChatRef = mChatroomsReference.push();
                        newChatRef.setValue(chatroomData);

                        //Adds reference to the private chat in the current user's
                        //list of private chat data
                        DatabaseReference currentUserReference = mUsersReference
                                .child(currentUser.getUid());
                        Map<String, Object> privateChatInfo = new HashMap<>();
                        privateChatInfo.put(newChatRef.getKey(),
                                (Object) (chatName.getText().toString()));
                        currentUserReference.child("private_chats").updateChildren(privateChatInfo);

                        //Adds the private chat reference to the private chat list of all the users that
                        //were invited to the chat
                        if (!usersToAdd.getText().toString().equals("")) {
                            String[] usersToAddList = usersToAdd.getText().toString().split(",");
                            boolean usersExistInTable = true;
                            for (String userToAdd : usersToAddList) {
                                if (!userToAdd.equals(" ")) {
                                    String userToAddEmail = userToAdd.split("\\(")[1]
                                            .split("\\)")[0];

                                    boolean userIsInTable = false;
                                    for (Map<String, Object> user : users) {
                                        if (user.get("email").toString().equals(userToAddEmail)) {
                                            mUsersReference.child(user.get("userID").toString())
                                                    .child("private_chats")
                                                    .updateChildren(privateChatInfo);

                                            userIsInTable = true;
                                        }
                                    }
                                    if (!userIsInTable) {
                                        usersExistInTable = false;
                                    }
                                }
                            }

                            //If some of the users entered are not in db, user is notified
                            if (!usersExistInTable) {
                                Toast.makeText(getContext(),
                                        "Some of the users you have entered does not exist",
                                        Toast.LENGTH_SHORT).show();

                            }

                        }

                        dismiss();
                    } else {
                        Toast.makeText(getContext(),
                                "User must be entered in the correct format, " +
                                        "ie. FirstName LastName (aa11bb@brocku.ca), ...",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Chatroom names must be between 1-16 characters",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Attaches listener to user table in DB that simply constructs a list of users to be used later
     * Also adds users to adapter to be displayed in autocomplete view
     */
    private void attachDatabaseListener() {
        mUsersReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Gets list of all users in db and adds these users to autocomplete textview for
                //entering users to invite
                String userID = dataSnapshot.getKey();
                String email = dataSnapshot.child("email").getValue().toString();
                String name = dataSnapshot.child("first_name").getValue().toString() + " " +
                        dataSnapshot.child("last_name").getValue().toString();
                Map<String, Object> user = new HashMap<>();
                user.put("userID", userID);
                user.put("email", email);
                user.put("name", name);
                users.add(user);
                usersAdapter.add(name + " (" + email + ")");
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
        });
    }
}
