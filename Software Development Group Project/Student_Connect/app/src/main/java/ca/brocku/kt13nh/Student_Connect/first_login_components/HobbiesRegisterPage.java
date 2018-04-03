package ca.brocku.kt13nh.Student_Connect.first_login_components;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import ca.brocku.kt13nh.Student_Connect.MainActivity;
import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.base_interface_java_v3.NavBar;

/**
 * This class is for the Hobbies register page, only shown once to each user.
 */
public class HobbiesRegisterPage extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_user_hobbies;
    private DatabaseReference table_user_enrolled;
    private DatabaseReference table_courses;
    private DatabaseReference table_user;
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    private LinkedList<String> list = new LinkedList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies_register_page);
        //initialize the database
        table_user_hobbies = mFirebaseDatabase.getReference().child("User").child(currUser.getUid()).child("hobbies");
        table_user_enrolled = mFirebaseDatabase.getReference().child("User").child(currUser.getUid()).child("enrolled");
        table_courses = mFirebaseDatabase.getReference().child("Courses");
        table_user = mFirebaseDatabase.getReference().child("User");
        //set toolbar and titles
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Student Connect");
        setSupportActionBar(toolbar);
        addListeners();

    }//onCreate

    //add all of the listeners for the check boxes and buttons -- quite lengthy
    private void addListeners(){

        Button next = (Button) findViewById(R.id.nextButton);
        Button previous = (Button)findViewById(R.id.previousButton);

        //creative arts category checkboxes
        final CheckBox checkActing = (CheckBox)findViewById(R.id.artsCheckActing);
        final CheckBox checkSculp = (CheckBox)findViewById(R.id.artsCheckSculpting);
        final CheckBox checkPhoto = (CheckBox)findViewById(R.id.artsCheckPhotography);
        final CheckBox checkPaint = (CheckBox)findViewById(R.id.artsCheckPainting);

        //educational category checkboxes
        final CheckBox checkProgramming = (CheckBox)findViewById(R.id.eduCheckProgramming);
        final CheckBox checkMuseum = (CheckBox)findViewById(R.id.eduCheckMuseum);
        final CheckBox checkBookClub = (CheckBox)findViewById(R.id.eduCheckBookClub);

        //Games and sports category checkboxes
        final CheckBox checkSoccer = (CheckBox)findViewById(R.id.gamesCheckSoccer);
        final CheckBox checkHockey = (CheckBox)findViewById(R.id.gamesCheckHockey);
        final CheckBox checkBasketball = (CheckBox)findViewById(R.id.gamesCheckBasketball);
        final CheckBox checkVolleyball = (CheckBox)findViewById(R.id.gamesCheckVolleyball);
        final CheckBox checkRugby = (CheckBox)findViewById(R.id.gamesCheckRugby);
        final CheckBox checkBadminton = (CheckBox)findViewById(R.id.gamesCheckBadminton);
        final CheckBox checkCycling = (CheckBox)findViewById(R.id.gamesCheckCycling);
        final CheckBox checkTennis = (CheckBox)findViewById(R.id.gamesCheckTennis);
        final CheckBox checkSwimming = (CheckBox)findViewById(R.id.gamesCheckSwimming);

        //outdoor recreation category checkboxes
        final CheckBox checkField = (CheckBox)findViewById(R.id.outdoorCheckField);
        final CheckBox checkHike = (CheckBox)findViewById(R.id.outdoorCheckHikes);
        final CheckBox checkBird = (CheckBox)findViewById(R.id.outdoorCheckBird);

        //other category checkboxes
        final CheckBox checkEat = (CheckBox)findViewById(R.id.otherCheckEating);
        final CheckBox checkKaraoke = (CheckBox)findViewById(R.id.otherCheckKaraoke);
        final CheckBox checkDance = (CheckBox)findViewById(R.id.otherCheckDance);
        final CheckBox checkPoetry = (CheckBox)findViewById(R.id.otherCheckPoetry);
        final CheckBox checkPiano = (CheckBox)findViewById(R.id.otherCheckPiano);
        final CheckBox checkReading = (CheckBox)findViewById(R.id.otherCheckReading);


        //Button listener on Previous button
        previous.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //go to previous activity
                finish();   //go to previous activity that called this register page
            }
        });

        //Button listener on Next button
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //variables are true if checkbox checked
                boolean acting = checkActing.isChecked();
                boolean sculp = checkSculp.isChecked();
                boolean photo = checkPhoto.isChecked();
                boolean paint = checkPaint.isChecked();

                boolean programming = checkProgramming.isChecked();
                boolean museum = checkMuseum.isChecked();
                boolean bookClub = checkBookClub.isChecked();

                boolean soccer = checkSoccer.isChecked();
                boolean basketball = checkBasketball.isChecked();
                boolean volleyball = checkVolleyball.isChecked();
                boolean hockey = checkHockey.isChecked();
                boolean rugby = checkRugby.isChecked();
                boolean badminton = checkBadminton.isChecked();
                boolean cycling = checkCycling.isChecked();
                boolean tennis = checkTennis.isChecked();
                boolean swimming = checkSwimming.isChecked();

                boolean fieldTrip = checkField.isChecked();
                boolean hike = checkHike.isChecked();
                boolean birdWatching = checkBird.isChecked();

                boolean eating = checkEat.isChecked();
                boolean karaoke = checkKaraoke.isChecked();
                boolean dance = checkDance.isChecked();
                boolean poetry = checkPoetry.isChecked();
                boolean piano = checkPiano.isChecked();
                boolean reading = checkReading.isChecked();
                //check to see if any of these are checked. if checked then add to the list
                if(acting)
                    list.add(checkActing.getText().toString());
                if(sculp)
                    list.add(checkSculp.getText().toString());
                if(photo)
                    list.add(checkPhoto.getText().toString());
                if(paint)
                    list.add(checkPaint.getText().toString());
                if(programming)
                    list.add(checkProgramming.getText().toString());
                if(museum)
                    list.add(checkMuseum.getText().toString());
                if(bookClub)
                    list.add(checkBookClub.getText().toString());
                if(soccer)
                    list.add(checkSoccer.getText().toString());
                if(basketball)
                    list.add(checkBasketball.getText().toString());
                if(volleyball)
                    list.add(checkVolleyball.getText().toString());
                if(hockey)
                    list.add(checkHockey.getText().toString());
                if(rugby)
                    list.add(checkRugby.getText().toString());
                if(badminton)
                    list.add(checkBadminton.getText().toString());
                if(cycling)
                    list.add(checkCycling.getText().toString());
                if(tennis)
                    list.add(checkTennis.getText().toString());
                if(swimming)
                    list.add(checkSwimming.getText().toString());
                if(fieldTrip)
                    list.add(checkField.getText().toString());
                if(hike)
                    list.add(checkHike.getText().toString());
                if(birdWatching)
                    list.add(checkBird.getText().toString());
                if(eating)
                    list.add(checkEat.getText().toString());
                if(karaoke)
                    list.add(checkKaraoke.getText().toString());
                if(dance)
                    list.add(checkDance.getText().toString());
                if(poetry)
                    list.add(checkPoetry.getText().toString());
                if(piano)
                    list.add(checkPiano.getText().toString());
                if(reading)
                    list.add(checkReading.getText().toString());
                writeDBInformation();
            }
        });
    }

    /*
    * Method to write to firebase all of the hobbies that the user is interested in based on if
    * the checkboxes are checked
    * */
    private void writeDBInformation(){
        final String c1 = getIntent().getStringExtra("course1");
        final String c2 = getIntent().getStringExtra("course2");
        final String c3 = getIntent().getStringExtra("course3");
        final String c4 = getIntent().getStringExtra("course4");
        final String c5 = getIntent().getStringExtra("course5");
        final String c6 = getIntent().getStringExtra("course6");
        //write all of the hobbies into the user's table.
        for(int i=0;i<list.size();i++){
            table_user_hobbies.child(list.get(i).toString()).setValue("");
        }

        table_user.child(currUser.getUid()).child("first_login").setValue("false");
        Intent activity_home = new Intent(HobbiesRegisterPage.this, NavBar.class);
        startActivity(activity_home);
        finish();
    }


}//HobbiesRegisterPage
