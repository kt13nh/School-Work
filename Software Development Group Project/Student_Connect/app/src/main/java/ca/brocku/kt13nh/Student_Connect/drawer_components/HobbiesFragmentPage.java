package ca.brocku.kt13nh.Student_Connect.drawer_components;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.util.ArrayList;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.first_login_components.CourseRegisterPage;

/**
 * This class used for hobbies page in nav drawer, user can manage their hobbies on this page.
 *
 * If the user wants to add a hobby then they type in the box to search it and click add.
 *
 * If the user wants to delete a hobby, then they select one or more hobbies to remove then click
 * delete button at the bottom, and if they do this then a confirmation dialog will pop up to make
 * sure they want to delete the hobby, if yes then proceed with deletion.
 *
 * */

public class HobbiesFragmentPage extends ListFragment {

    private FragmentActivity activity;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> hobbies;
    private ArrayList<String> user_hobbies;
    private ArrayList<String> list = new ArrayList<String>();
    private AutoCompleteTextView edit;
    private SparseBooleanArray checkedItemPositions;
    private int itemCount;
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference table_hobbies = mFirebaseDatabase.getReference().child("Hobbies");
    private DatabaseReference table_user = mFirebaseDatabase.getReference().child("User");
    private FirebaseAuth authenticator= FirebaseAuth.getInstance();
    private FirebaseUser currUser = authenticator.getCurrentUser();
    private ArrayAdapter<String> hobbyAdapter;
    private ListView hobbyListView;

    /*
    * Create the view and initialize database information. Set button listeners with view param
    * and set the list adapter.
    * */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.hobbies_fragment_page, container, false);
        getDatabaseInfo();
        setButtonListeners(v);
        initializeComponents();
        setListView();
        setListAdapter(adapter);

        return v;
    }//onCreateView

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Hobbies");



    }//on ViewCreated

    private void initializeComponents(){
        activity = getActivity();
        adapter = new ArrayAdapter<String>(activity.getApplicationContext(),
                android.R.layout.simple_list_item_multiple_choice,list);   //used to set courses to ListView

    }

    //initialize button listeners
    private void setButtonListeners(View v){
        ImageButton btnAdd = (ImageButton) v.findViewById(R.id.btnAddHobby);  //add hobby button
        Button btnDel = (Button) v.findViewById(R.id.btnDel); //delete hobby button
        //On click listener for Add hobby button

        View.OnClickListener listenerAdd = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hobby = edit.getText().toString();
                //check to see if hobby is valid choice and if user is already participating in hobby
                if(hobbies.contains(hobby.toLowerCase())&&!user_hobbies.contains(hobby.toLowerCase())) {
                    String body =hobby.substring(1,hobby.length());
                    String firstLetter = ""+hobby.charAt(0);
                    String combined = firstLetter.toUpperCase()+body;
                    table_user.child(currUser.getUid().toString()).child("hobbies").child(combined).setValue("");
                    edit.setText("");
                    adapter.notifyDataSetChanged();
                    Toast.makeText(v.getContext(),"Hobby added!",Toast.LENGTH_SHORT).show();

                }
                //check to see if user already participating in hobby
                else if(hobbies.contains(hobby)&&user_hobbies.contains(hobby)){
                    Toast.makeText(v.getContext(),"You already have this hobby!",Toast.LENGTH_SHORT).show();
                }
                //otherwise not in the database
                else{
                    Toast.makeText(v.getContext(),"Hobby is not currently in our database," +
                            " feel free to create a chat with your friends!",Toast.LENGTH_LONG).show();
                }
            }
        };

        //OnClick listener for Delete hobby/hobbies button
        View.OnClickListener listenerDel = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedItemPositions = getListView().getCheckedItemPositions();
                itemCount = getListView().getCount();
                //alert dialog pops up to confirm if user wants to delete selected hobby
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Delete Hobby");
                alert.setMessage("Are you sure you want to delete this hobby?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //remove hobby from their list of hobbies in database
                        //continue with delete from list view
                        for(int i=itemCount-1; i >= 0; i--){
                            if(checkedItemPositions.get(i)){
                                String hobbyName = adapter.getItem(i).toString();
                                removeHobby(hobbyName);
                            }
                        }
                        checkedItemPositions.clear();
                        adapter.notifyDataSetChanged();
                    }
                });
                //if user hits no, return to hobbies fragment page
                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //close dialog
                        dialog.cancel();
                    }
                });
                alert.show();

            }
        };

        btnAdd.setOnClickListener(listenerAdd); //set event listener for add hobby button
        btnDel.setOnClickListener(listenerDel); //set even listener for delete hobby button

    }

    //set the list view and populate the listview data
    private void setListView() {
        String UID = currUser.getUid().toString();
        table_user.child(UID).child("hobbies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //set the adapter with hobby data and populate listview with the adapter
                String[] values = new String[(int)dataSnapshot.getChildrenCount()];
                hobbyListView = (ListView)activity.findViewById(android.R.id.list);

                int pos=0;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String name = snapshot.getKey().toString();
                    System.out.println(name);
                    values[pos]=name;
                    pos++;
                }
                adapter.clear();
                adapter.addAll(values);
                hobbyListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //remove hobby from the user's table
    private void removeHobby(String hobby) {
        String UID = currUser.getUid();
        table_user.child(UID).child("hobbies").child(hobby).removeValue();
    }

    //initialize the database info and store the
    // hobbies in the hobbies arraylist for
    // comparing purposes and autocomplete
    // */
    private void getDatabaseInfo(){
        table_hobbies.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                edit = (AutoCompleteTextView) getActivity().findViewById(R.id.hobbyEditItem);

                hobbies = new ArrayList<String>();
                setHobbies();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    hobbies.add(snapshot.getKey().toString().toLowerCase());
                }

                hobbyAdapter = new ArrayAdapter<String>
                        (HobbiesFragmentPage.this.getActivity(),android.R.layout.simple_list_item_1,hobbies);
                hobbyAdapter.addAll(hobbies);
                edit.setAdapter(hobbyAdapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //set the user's hobbies to the arraylist user_hobbies for verification of the input
    private void setHobbies(){
        table_user.child(currUser.getUid().toString()).child("hobbies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_hobbies = new ArrayList<String>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    user_hobbies.add(snapshot.getKey().toString().toLowerCase());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}//HobbiesFragmentPage
