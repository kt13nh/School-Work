package ca.brocku.kt13nh.scheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Created by Tram on 26/11/2017.
 */

public class NewMeeting extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_meeting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initRoomButtons();
    }

    public void initRoomButtons() {
        Button button1 =(Button) findViewById(R.id.room1Button);
        Button button2 =(Button) findViewById(R.id.room2Button);
        Button button3 =(Button) findViewById(R.id.room3Button);
        Button button4 =(Button) findViewById(R.id.room4Button);
        Button button5 =(Button) findViewById(R.id.room5Button);
        Button button6 =(Button) findViewById(R.id.room6Button);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","1");
                startActivity(bookDetails);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","2");
                startActivity(bookDetails);
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","3");
                startActivity(bookDetails);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","4");
                startActivity(bookDetails);
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","5");
                startActivity(bookDetails);
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                Intent bookDetails = new Intent(NewMeeting.this,BookDetails.class);
                bookDetails.putExtra("buttonPressed","6");
                startActivity(bookDetails);
            }
        });
    }

}
