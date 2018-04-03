package ca.brocku.kt13nh.Student_Connect.qa_components;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

public class QaDisplay extends AppCompatActivity {
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 2048;

    private static final int RC_PHOTO_PICKER =  2;
    private static final int RC_FILE_PICKER = 1212;

    private ListView mAnswerListView;
    private AnswerAdapter mAnswerAdapter;
    private ImageButton mPhotoPickerButton;
    private ImageButton mFilePickerButton;
    private EditText mAnswerEditText;
    private Button mSendButton;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mCurrentUserReference;
    private DatabaseReference mAnswersDatabaseReference;
    private ChildEventListener mChildEventListener;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mImagesStorageReference;
    private StorageReference mFilesStorageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qa_display);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        setTitle(intent.getStringExtra("title"));

        initializeFirebaseComponents();
        attachDatabaseReadListener();

        initializeComponents();
        attachListeners();
    }

    @Override
    /**
     * shows report button when an answer is selected
     */
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.answersListView) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.report_menu, menu);
        }
    }

    @Override
    /**
     * incrementing report counter for the user whose commented was reported
     */
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String userToReport = ((Answer) mAnswerListView.getItemAtPosition((int)info.id))
                .getUser();

        switch(item.getItemId()) {
            case R.id.report:
                if(mUsername.equals(userToReport))
                    Toast.makeText(QaDisplay.this, "You cannot report yourself",
                            Toast.LENGTH_SHORT);
                else{
                    String userToReportFirstName = userToReport.split(" ")[0];
                    mCurrentUserReference.getParent()
                            .child("first_name").equalTo(userToReportFirstName).getRef()
                            .child("last_name");
                }

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    /**
     * Opens picture/file selecter when file upload or picture upload is pressed
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == RC_PHOTO_PICKER){
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
                                Answer answer = new Answer(null, mUsername,
                                        downloadUrl.toString(),null, null);
                                mAnswersDatabaseReference.push().setValue(answer);
                            }
                        });
            }
            else if(requestCode == RC_FILE_PICKER){
                Uri selectedFileUri = data.getData();
                String uriString = selectedFileUri.toString();

                //Find the name of the file selected by the user
                File myFile = new File(uriString);
                String displayName = null;

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

                //Add answer object containing the user, name of the selected file, and the url to
                // its location in Firebase Storage to the database
                fileRef.putFile(selectedFileUri).addOnSuccessListener(this,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                Answer answer = new Answer(null, mUsername, null,
                                        fileName, downloadUrl.toString());
                                mAnswersDatabaseReference.push().setValue(answer);
                            }
                        });
            }
        }
    }

    /**
     * Initializes all Firebase components like DatabaseReferences, FirebaseStorage etc.
     */
    private void initializeFirebaseComponents(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        mFirebaseStorage = FirebaseStorage.getInstance();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mCurrentUserReference = mDatabaseReference.child("User").child(currentUser.getUid());

        String questionID = getIntent().getStringExtra("questionID");

        mAnswersDatabaseReference = mDatabaseReference.child("Questions")
                .child(questionID).child("answers");

        mImagesStorageReference = mFirebaseStorage.getReference().child("Images");
        mFilesStorageReference = mFirebaseStorage.getReference().child("Files");
    }

    /**
     * Initializes all TextViews, ListViews, Buttons etc
     */
    private void initializeComponents(){
        //Set question details at top of activity
        TextView mAskerTextView = (TextView) findViewById(R.id.askerTextView);
        TextView mQuestionTimeTextView = (TextView) findViewById(R.id.questionTimeTextView);
        TextView mQuestionTextView = (TextView) findViewById(R.id.questionTextView);

        Intent intent = getIntent();
        mAskerTextView.setText(intent.getStringExtra("askedBy"));
        mQuestionTimeTextView.setText(intent.getStringExtra("time"));
        mQuestionTextView.setText(intent.getStringExtra("description"));

        // Initialize references to views
        mAnswerListView = (ListView) findViewById(R.id.answersListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.qaPhotoPickerButton);
        mFilePickerButton = (ImageButton) findViewById(R.id.qaFilePickerButton);
        mAnswerEditText = (EditText) findViewById(R.id.answerEditText);
        mSendButton = (Button) findViewById(R.id.qaSendButton);

        // Initialize answers ListView and its adapter
        List<Answer> answers = new ArrayList<>();
        mAnswerAdapter = new AnswerAdapter(this, R.layout.item_answer, answers);
        mAnswerListView.setAdapter(mAnswerAdapter);
    }

    /**
     * Attaches the child event listener to the answers table in the database.
     * When a new answer is added to the answers for the current question, the qa view is
     * updated to contain the new answer
     */
    private void attachDatabaseReadListener(){
        if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Answer answer = dataSnapshot.getValue(Answer.class);
                    mAnswerAdapter.add(answer);
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
            mAnswersDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    /**
     * Attaches all other listeners to components in the activity
     */
    private void attachListeners(){
        mCurrentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsername = dataSnapshot.child("first_name").getValue().toString() + " " +
                        dataSnapshot.child("last_name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Selecting an answer in the answer list view will give the option to report
        mAnswerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });

        // ImagePickerButton shows an image picker to upload a image for an answer
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
        mAnswerEditText.addTextChangedListener(new TextWatcher() {
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
        //limit answer to character limit defined by DEFAULT_MSG_LENGTH_LIMIT
        mAnswerEditText.setFilters(new InputFilter[]
                {new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a answer and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Answer answer = new Answer(mAnswerEditText.getText().toString(), mUsername,
                        null, null, null);
                mAnswersDatabaseReference.push().setValue(answer);
                // Clear input box
                mAnswerEditText.setText("");
            }
        });
    }
}
