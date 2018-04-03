package ca.brocku.kt13nh.scheduler;


import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Tram on 26/11/2017.
 */

public class BookDetails extends AppCompatActivity {
    RadioButton radioButton1, radioButton2, radioButton3;
    RadioButton radioButton4, radioButton5, radioButton6;
    RadioButton radioButton7, radioButton8, radioButton9;
    RadioButton radioButton10, radioButton11, radioButton12;
    Button sButton; String room; String date="";
    TextView editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_details);
        room = getIntent().getStringExtra("buttonPressed");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editText = (TextView)findViewById(R.id.dateTextField);
        setTitle(room);

        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(frmt.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = frmt.format(c.getTime());
        editText.setText(date);
        initRadioButtons();
        initDateButtons();
        submitListener();
    }

    public void submitListener(){
        sButton = (Button)findViewById(R.id.submitButton);
        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                DataHelper dh=new DataHelper(BookDetails.this);
                SQLiteDatabase datachanger=dh.getWritableDatabase();
                String selected = getSelected();
                ContentValues vals = new ContentValues();
                vals.put("date", editText.getText().toString());
                vals.put("room", room.toString());
                vals.put("time", getSelected().toString());
                System.out.println(date+" , "+room+" , "+getSelected());
                datachanger.insert(DataHelper.DB_TABLE,null,vals);
                datachanger.close();
                Toast.makeText(BookDetails.this,"Meeting created!",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    public String getSelected(){
        if(radioButton1.isChecked()){
            return "9:00 - 10:00";
        }
        else if(radioButton2.isChecked()){
            return "10:00 - 11:00";
        }
        else if(radioButton3.isChecked()){
            return "11:00 - 12:00";
        }
        if(radioButton4.isChecked()){
            return "12:00 - 13:00";
        }
        else if(radioButton5.isChecked()){
            return "13:00 - 14:00";
        }
        else if(radioButton6.isChecked()){
            return "14:00 - 15:00";
        }
        if(radioButton7.isChecked()){
            return "15:00 - 16:00";
        }
        else if(radioButton8.isChecked()){
            return "16:00 - 17:00";
        }
        else if(radioButton9.isChecked()){
            return "17:00 - 18:00";
        }
        if(radioButton10.isChecked()){
            return "18:00 - 19:00";
        }
        else if(radioButton11.isChecked()){
            return "19:00 - 20:00";
        }
        else{
            return "20:00 - 21:00";
        }
    }
    public void initRadioButtons(){
        radioButton1 = (RadioButton)findViewById(R.id.nineToTen);
        radioButton2 = (RadioButton)findViewById(R.id.tenToEleven);
        radioButton3 = (RadioButton)findViewById(R.id.elevenToTwelve);
        radioButton4 = (RadioButton)findViewById(R.id.twelveToOne);
        radioButton5 = (RadioButton)findViewById(R.id.oneToTwo);
        radioButton6 = (RadioButton)findViewById(R.id.twoToThree);
        radioButton7 = (RadioButton)findViewById(R.id.threeToFour);
        radioButton8 = (RadioButton)findViewById(R.id.fourToFive);
        radioButton9 = (RadioButton)findViewById(R.id.fiveToSix);
        radioButton10 = (RadioButton)findViewById(R.id.sixToSeven);
        radioButton11 = (RadioButton)findViewById(R.id.sevenToEight);
        radioButton12 = (RadioButton)findViewById(R.id.eightToNine);
    }

    public void setTitle(String room){
        TextView textView = (TextView)findViewById(R.id.roomTitle);
        switch(room) {
            case "1":
                textView.setText("Library room 1");
                break;
            case "2":
                textView.setText("Library room 2");
                break;
            case "3":
                textView.setText("Library room 3");
                break;
            case "4":
                textView.setText("Library room 4");
                break;
            case "5":
                textView.setText("Library room 5");
                break;
            case "6":
                textView.setText("Library room 6");
                break;
            default:
                break;
        }

    }
    public void initDateButtons() {
        Button prevDate =(Button) findViewById(R.id.prevDateButton);
        Button nextDate =(Button) findViewById(R.id.nextDateButton);
        final TextView editText = (TextView)findViewById(R.id.dateTextField);
        final SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar c = Calendar.getInstance();
        String d="";
        try {
            c.setTime(frmt.parse(d));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        prevDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here
                String currDate="";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar temp = Calendar.getInstance();
                try {
                    temp.setTime(format.parse(currDate));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                currDate = format.format(temp.getTime());
                if(frmt.format(c.getTime()).equals(currDate)){
                }
                else{
                    c.add(Calendar.DATE, -1);  // number of days to add
                    final String date = frmt.format(c.getTime());
                    editText.setText(date);
                }

            }
        });
        nextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do stuff here

                c.add(Calendar.DATE, 1);  // number of days to add
                final String date = frmt.format(c.getTime());
                editText.setText(date);
            }
        });
    }


}