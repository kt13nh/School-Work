package ca.brocku.kt13nh.Student_Connect.chatroom_components;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ca.brocku.kt13nh.Student_Connect.R;

public class Chatroom extends AppCompatActivity {

    public static final String ANONYMOUS = "Anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 2048;

    private static final int RC_SETTINGS = 1;
    private static final int RC_PHOTO_PICKER =  2;
    private static final int RC_FILE_PICKER = 1212;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ImageButton mPhotoPickerButton;
    private ImageButton mFilePickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String chatID;
    private String chatName;
    private String admin;
    private String userEmail;
    private String mUsername;
    private String displayName;
    private boolean isPublic;
    //private int userReportCount;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mCurrentUserReference;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mImagesStorageReference;
    private StorageReference mFilesStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeComponents();
        initializeFirebaseComponents();
        attachListeners();
        attachDatabaseReadListener();
    }


    /**
     * Initializes all Firebase components like FirebaseStorage, DatabaseReferences etc
     */
    private void initializeFirebaseComponents(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserReference = mDatabaseReference.child("User").child(currentUser.getUid());

        mMessagesDatabaseReference = mDatabaseReference.child("Chatrooms")
                .child(chatID).child("messages");

        mImagesStorageReference = mFirebaseStorage.getReference().child("Images");
        mFilesStorageReference = mFirebaseStorage.getReference().child("Files");
    }

    /**
     * Initializes all other components such as views, arraylists, adapters
     */
    private void initializeComponents(){
        //Gets all data passed in from previous activity
        Intent currentIntent = getIntent();
        chatID = currentIntent.getStringExtra("chatID");
        isPublic = currentIntent.getBooleanExtra("isPublic", true);
        chatName = currentIntent.getStringExtra("chatroomName");
        admin = currentIntent.getStringExtra("admin");
        setTitle(chatName);

        // Initialize references to views
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.chatPhotoPickerButton);
        mFilePickerButton = (ImageButton) findViewById(R.id.chatFilePickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.chatSendButton);

        // Initialize message ListView and its adapter
        List<Message> messages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, messages);
        mMessageListView.setAdapter(mMessageAdapter);
    }

    /**
     * Attaches the child event listener to the message stable in the database.
     * When a new message is added to the messages table, the chatroom view is updated to contain
     * the new message
     */
    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Message message = dataSnapshot.getValue(Message.class);
                    mMessageAdapter.add(message);
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
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    /**
     * Attach all other listeners to components in the activity
     */
    private void attachListeners(){
        mCurrentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //userReportCount = (int)dataSnapshot.child("report_count").getValue();
                userEmail = dataSnapshot.child("email").getValue().toString();
                mUsername = dataSnapshot.child("first_name").getValue().toString() + " " +
                        dataSnapshot.child("last_name").getValue().toString();
                displayName = mUsername;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Long cLlcking a message in the message list view will give the option to report
        mMessageListView.setLongClickable(true);
        registerForContextMenu(mMessageListView);


        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                        RC_PHOTO_PICKER);
            }
        });

        //Shows a file picker to upload a file to the chatroom
        mFilePickerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                        RC_FILE_PICKER);
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //limit message to character limit defined by DEFAULT_MSG_LENGTH_LIMIT
        mMessageEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message message = new Message(mMessageEditText.getText().toString(), displayName,
                        null, null, null);
                mMessagesDatabaseReference.push().setValue(message);
                // Clear input box
                mMessageEditText.setText("");
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.messageListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.report_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String userToReport = ((Message) mMessageListView.getItemAtPosition((int)info.id))
                .getName();

        switch(item.getItemId()) {
            case R.id.report:
                if(mUsername.equals(userToReport))
                    Toast.makeText(getApplicationContext(), "You cannot report yourself",
                            Toast.LENGTH_SHORT).show();
                else{
                    if(!userToReport.equals(ANONYMOUS)) {
                        Toast.makeText(getApplicationContext(), "This user has been reported",
                                Toast.LENGTH_SHORT).show();
                        String userToReportFirstName = userToReport.split(" ")[0];
                        String userToReportLastName = userToReport.split(" ")[1];
                        DatabaseReference userToReportRef = mDatabaseReference.child("User")
                                                                .orderByChild("email")
                                                                .equalTo("kt13nh@brocku.ca")
                                                                .getRef();
                        //String userToReportUID = userToReportRef.getKey();
                        int i = 0;
                    }
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chatroom_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.chatSettings:
                //Goes to settings page
                Intent settingsIntent = new Intent(Chatroom.this,
                        ChatroomSettings.class);
                //Passes useful data like currentUserID to settings actvity
                settingsIntent.putExtra("chatID", chatID);
                settingsIntent.putExtra("isPublic", isPublic);
                settingsIntent.putExtra("chatName", chatName);
                settingsIntent.putExtra("admin", admin);
                settingsIntent.putExtra("userEmail", userEmail);
                if(displayName.equals(ANONYMOUS))
                    settingsIntent.putExtra("anonymous", true);
                else
                    settingsIntent.putExtra("anonymous", false);
                startActivityForResult(settingsIntent, RC_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RC_SETTINGS){
                //destroys the activity if the user has left the chat
                if(data.getBooleanExtra("left", false) == true){
                    finish();
                }
                //Sets display name to anonymous if user selected Post As Anonymous
                else {
                    if (data.getBooleanExtra("anonymous", false) == true)
                        displayName = ANONYMOUS;
                    else
                        displayName = mUsername;
                }
            }
            else if(requestCode == RC_PHOTO_PICKER){
                Uri selectedImageUri = data.getData();
                //Get a reference to store file at Images/<FILENAME>
                StorageReference imageRef = mImagesStorageReference.child(
                        selectedImageUri.getLastPathSegment());

                //Upload file to Firebase storage
                imageRef.putFile(selectedImageUri).addOnSuccessListener(this,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Message message = new Message(null, displayName,
                                        downloadUrl.toString(),null, null);
                                mMessagesDatabaseReference.push().setValue(message);
                            }
                        });
            }
            else if(requestCode == RC_FILE_PICKER){
                Uri selectedFileUri = data.getData();
                String uriString = selectedFileUri.toString();

                File myFile = new File(uriString);
                String displayName = null;

                //Find the name of the file selected by the user
                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = this.getContentResolver().query(selectedFileUri, null,
                                null, null,
                                null);
                        if (cursor != null && cursor.moveToFirst()) {
                            displayName = cursor.getString(cursor.getColumnIndex
                                    (OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    displayName = myFile.getName();
                }

                //Get a reference to store file at Files/<FILENAME>
                final String fileName = displayName;
                StorageReference fileRef = mFilesStorageReference.child(
                        selectedFileUri.getLastPathSegment());

                final String uploadedBy = displayName;
                //Add message object containing the user, name of the selected file, and the url to
                // its location in Firebase Storage to the database
                fileRef.putFile(selectedFileUri).addOnSuccessListener(this,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Message message = new Message(null, uploadedBy, null,
                                        fileName, downloadUrl.toString());
                                mMessagesDatabaseReference.push().setValue(message);
                            }
                        });
            }
        }
    }
}
