package ca.brocku.kt13nh.Student_Connect.chatroom_components;

/**
 * Author: Goal Diggers
 * Container class for message objects in DB
 */

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class ChatroomSettings extends AppCompatActivity {
    private boolean postAsAnonymous;
    private boolean isPublic;
    private String chatID;
    private String chatName;
    private String admin;
    private String userEmail;

    private List<Map<String, Object>> userInfoList;
    private ArrayAdapter registeredUserListAdapter;
    private List<String> allUsersList;
    private CheckBox anonymousCheckBox;
    private Intent intent;

    private TextView membersTxt;
    private ListView usersListView;
    private Button addUserBtn;
    private Button leaveBtn;

    private DatabaseReference mUsersReference;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra("Settings"));

        membersTxt = (TextView) findViewById(R.id.memberTxt);
        usersListView = (ListView) findViewById(R.id.privateChatUsersListView);
        addUserBtn = (Button) findViewById(R.id.addStudentBtn);
        leaveBtn = (Button) findViewById(R.id.leaveBtn);

        //Gets all data that was passed from previous activity
        intent = getIntent();
        chatID = intent.getStringExtra("chatID");
        chatName = intent.getStringExtra("chatName");
        admin = intent.getStringExtra("admin");
        userEmail = intent.getStringExtra("userEmail");
        isPublic = intent.getBooleanExtra("isPublic", true);

        anonymousCheckBox = (CheckBox) findViewById(R.id.anonymousCheck);
        anonymousCheckBox.setChecked(getIntent().getBooleanExtra("anonymous",
                                                                    false));

        currentUser  = FirebaseAuth.getInstance().getCurrentUser();
        userInfoList = new ArrayList<>();
        allUsersList = new ArrayList<>();

        //Shows list of members and Add User and Leave Chat button if it is a private chat
        if(!isPublic) {
            leaveBtn.setVisibility(View.VISIBLE);
            addUserBtn.setVisibility(View.VISIBLE);
            membersTxt.setVisibility(View.VISIBLE);
            usersListView.setVisibility(View.VISIBLE);
            displayUsers();
            mUsersReference = FirebaseDatabase.getInstance().getReference().child("User");

            attachDatabaseListener();
        }
    }

    /**
     * Attaches an adapter to userListView
     * UserListView will show all members of the private chat
     */
    private void displayUsers(){
        List<String> privateChatUsers = new ArrayList<>();
        registeredUserListAdapter = new ArrayAdapter(this,
                                        android.R.layout.simple_list_item_1, privateChatUsers);
        usersListView.setAdapter(registeredUserListAdapter);
    }

    /**
     * Attaches listener to Users table that creates a list of all users and their information
     * as well as adding all users that are members of the private chat to the listview adapter.
     * These users will show up on the listview after being added to the adapter attached to the
     * ListView
     */
    private void attachDatabaseListener(){
        mUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> usersMap = (Map<String, Object>) dataSnapshot.getValue();
                allUsersList.clear();
                userInfoList.clear();
                registeredUserListAdapter.clear();

                for(Map.Entry<String, Object> user : usersMap.entrySet()){
                    String userID = user.getKey();
                    Map<String, Object> userInfoMap = (Map<String, Object>) user.getValue();
                    String email = userInfoMap.get("email").toString();
                    String name = userInfoMap.get("first_name").toString() + " " +
                            userInfoMap.get("last_name").toString();
                    allUsersList.add(name + " (" + email + ")");

                    Map<String, Object> userData = new HashMap<String, Object>();
                    userData.put("userID", (Object) userID);
                    userData.put("email", (Object) email);
                    userData.put("privateChatNum", 0);

                    if(!userInfoMap.get("private_chats").toString().equals("")) {
                        Map<String, Object> privateChats = (Map<String, Object>)
                                                                userInfoMap.get("private_chats");
                        userData.remove("privateChatNum");
                        userData.put("privateChatNum", privateChats.size());

                        for (String privateChatID : privateChats.keySet()) {
                            if (privateChatID.equals(chatID)) {
                                registeredUserListAdapter.add(name + " (" + email + ")");

                            }
                        }
                    }

                    userInfoList.add(userData);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    /**
     * Listener for when Add Student is clicked
     * Opens dialog where user can add other users/students to chat
     * @param view
     */
    public void addUser(View view){
        final View settingsView = view;
        LayoutInflater li = LayoutInflater.from(view.getContext());
        final View addUserView = li.inflate(R.layout.add_user_dialog,null);
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        dialog.setView(addUserView);

        //Sets autocomplete to show users in userListAdapter
        final AutoCompleteTextView autoCompleteView = (AutoCompleteTextView)
                                                        addUserView.findViewById(R.id.addUserField);
        final ArrayAdapter<String> userListAdapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,allUsersList);
        autoCompleteView.setThreshold(1);
        autoCompleteView.setAdapter(userListAdapter);

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Adds specified user to the private chat
        //Notifies user if no user has been entered, if user has been entered incorrectly
        //or if user does not exist
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userToAdd = autoCompleteView.getText().toString();
                if(!userToAdd.equals("")) {
                    if(userToAdd.matches("(([a-z]+[\\s][a-z]+[\\s]*\\(" +
                            "[a-z][a-z][0-9][0-9][a-z][a-z]@brocku.ca\\)),*[\\s]*)+")) {
                        String userToAddEmail = userToAdd.split("\\(")[1]
                                                            .split("\\)")[0];
                        boolean isInUsersTable = false;
                        for(Map<String, Object> user : userInfoList){
                            //If user exists, adds adds user to the chat by
                            //saving the chatID to private chats section in the user's table
                            if (user.get("email").toString().equals(userToAddEmail)) {
                                String userToAddID = user.get("userID").toString();
                                Map<String, Object> privateChatInfo = new HashMap<>();
                                privateChatInfo.put(chatID, (Object) (chatName));

                                mUsersReference.child(userToAddID).child("private_chats")
                                        .updateChildren(privateChatInfo);
                                Toast.makeText(addUserView.getContext(),
                                        userToAdd + " has been added to this chatroom",
                                        Toast.LENGTH_SHORT).show();

                                isInUsersTable = true;
                            }
                        }
                        if(!isInUsersTable) {
                            Toast.makeText(addUserView.getContext(),
                                    "This user does not exist",Toast.LENGTH_SHORT).show();

                        }
                    }
                    else{
                        Toast.makeText(addUserView.getContext(),
                                        "User must be entered in the correct format, " +
                                                "ie. FirstName LastName (aa11bb@brocku.ca)",
                                             Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(addUserView.getContext(), "Please enter a user",
                            Toast.LENGTH_SHORT).show();
                }
                addUser(settingsView);
            }
        });
        dialog.show();
    }

    /**
     * Listener for leave chat button
     * Opens confirmation dialog asking user whether they are sure they want to leave
      * @param view
     */
    public void leaveChat(View view){
        AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext());
        dialog.setTitle("Leave Confirmation");
        if(admin.equals(userEmail)) {
            dialog.setMessage("Leaving this chat will delete this chatroom from the database. " +
                    "Are you sure you want to proceed?");
        }
        else{
            dialog.setMessage("Are you sure you want to leave?");
        }

        //Sets right button to "Cancel" and attaches a listener for it
        //When clicked, dialog is closed
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Sets right button to "Ok" and attaches a listener for it
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                //If leaving as admin, Removes the private chat from all users that
                // were participants in chat and delete chatroom itself from the db
                if(admin.equals(userEmail)){
                    for(Map<String, Object> userInfo : userInfoList){
                        if((int) (userInfo.get("privateChatNum")) - 1 == 0)
                            dbRef.child("User").child(userInfo.get("userID").toString())
                                    .child("private_chats").setValue("");
                        else
                            dbRef.child("User").child(userInfo.get("userID").toString())
                                    .child("private_chats").child(chatID).removeValue();
                    }
                    dbRef.child("Chatrooms").child(chatID).removeValue();
                }
                //If not admin, simply deletes the private chat from current user's
                //private chat list in DB
                else {
                    for(Map<String, Object> userInfo : userInfoList){
                        if(userInfo.get("userID").toString().equals(currentUser.getUid())){
                            if((int) (userInfo.get("privateChatNum")) - 1 == 0) {
                                dbRef.child("User").child(userInfo.get("userID").toString())
                                        .child("private_chats").setValue("");
                            }
                            else{
                                dbRef.child("User").child(currentUser.getUid())
                                        .child("private_chats").child(chatID).removeValue();
                            }
                        }
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("left", true);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        dialog.show();
    }

    /**
     * Listener for when Post as Anonymous is checked/unchecked
     * @param view
     */
    public void anonymousCheckClicked(View view){
        if(anonymousCheckBox.isChecked())
            postAsAnonymous = true;
        else
            postAsAnonymous = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                //Destroys current activity and sends whether the user selected "Post as Anonymous"
                //as the result
                Intent intent = new Intent();
                intent.putExtra("anonymous", postAsAnonymous);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
