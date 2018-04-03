package ca.brocku.kt13nh.Student_Connect.base_interface_java_v3;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ca.brocku.kt13nh.Student_Connect.MainActivity;
import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.drawer_components.CourseFragmentPage;
import ca.brocku.kt13nh.Student_Connect.drawer_components.HobbiesFragmentPage;
import ca.brocku.kt13nh.Student_Connect.drawer_components.Profile;

/**
 * Created by kevin on 2018-01-18.
 */

/*
*
* */
public class NavBar extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth authenticator = FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        checkReportCount();

        //set title and support actionbars
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Student Connect");
        setSupportActionBar(toolbar);

        //this is for the drawer, setting the toggle options
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Listen for selections of the navigation.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //default selection home screen
        displaySelectedScreen(R.id.nav_home);

    }


    //Closes drawer if the back button is pressed
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
        }
    }

    //
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        displaySelectedScreen(item.getItemId());

        return true;
    }

    //For the navbar options, screen should display what is selected.
    private void displaySelectedScreen(int itemId){

        Fragment fragment = null;

        switch(itemId){
            case R.id.nav_home:
                fragment = new Home();
                break;
            case R.id.nav_profile:
                Intent profileIntent = new Intent(NavBar.this, Profile.class);
                startActivity(profileIntent);
                break;
            case R.id.nav_courses:
                fragment = new CourseFragmentPage();
                break;
            case R.id.nav_hobbies:
                fragment = new HobbiesFragmentPage();
                break;
            case R.id.nav_signout:
                FirebaseAuth.getInstance().signOut();
                finish();
                break;

        }
        //refresh the fragment
        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame,fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }//displaySelectedScreen

    private void checkReportCount(){
        if(currUser != null) {
            DatabaseReference currUserRef = database.getReference().child("User")
                    .child(currUser.getUid());

            currUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int reportCount = Integer.parseInt(dataSnapshot.child("report_count").getValue().toString());
                    if(reportCount >= 25){
                        currUser.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NavBar.this,
                                                    "You have been banned for malicious behaviour!",
                                                    Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(NavBar.this,
                                                    "You have been banned for malicious activity!",
                                                    Toast.LENGTH_LONG).show();
                                            authenticator.signOut();
                                            finish();
                                        }
                                    }
                                });


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

}
