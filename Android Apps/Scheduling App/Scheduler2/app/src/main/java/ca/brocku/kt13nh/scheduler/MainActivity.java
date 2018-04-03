package ca.brocku.kt13nh.scheduler;


import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String date;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        //ActionBar actionBar = getActionBar();
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("All"));
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        tabLayout.addTab(tabLayout.newTab().setText("Tomorrow"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        date="";
        SimpleDateFormat frmt = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(frmt.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = frmt.format(c.getTime());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        initFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.allOption) {
            deleteDatabase(DataHelper.DB_NAME);
            finish();
            Intent main = new Intent(MainActivity.this, MainActivity.class);
            startActivity(main);
        }
        else if(id==R.id.todayOption){
            String[] fields=new String[]{"id","date","room","time"};
            DataHelper dh=new DataHelper(MainActivity.this);
            SQLiteDatabase datareader=dh.getReadableDatabase();

            Cursor cursor=datareader.query(DataHelper.DB_TABLE,fields,
                    null,null,null,null,null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                //System.out.println(Integer.toString(cursor.getInt(0))+", "+
                // cursor.getString(1)+", "+cursor.getString(2));
                if(cursor.getString(1).equals(date)){
                    System.out.println("true");
                    delete(date);
                }
                cursor.moveToNext();
            }
            finish();
            Intent main = new Intent(MainActivity.this, MainActivity.class);
            startActivity(main);
        }

        return super.onOptionsItemSelected(item);
    }
    public void delete(String value) {
        DataHelper dh=new DataHelper(MainActivity.this);
        SQLiteDatabase datareader=dh.getReadableDatabase();
        datareader.delete(DataHelper.DB_TABLE, "date" + "=?", new String[]{String.valueOf(value)});
    }
    public void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newMeeting = new Intent(MainActivity.this,NewMeeting.class);
                startActivity(newMeeting);
            }
        });
    }

}
