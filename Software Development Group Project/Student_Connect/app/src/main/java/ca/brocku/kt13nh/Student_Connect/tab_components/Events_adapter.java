package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.event_components.Event;
import ca.brocku.kt13nh.Student_Connect.qa_components.QaDisplay;

public class Events_adapter extends
        RecyclerView.Adapter<EventsCardHolder> {
    private ArrayList<Map<String, String>>arrayList;
    private Context context;


    public Events_adapter(Context context) {
        this.context = context;
        this.arrayList = new ArrayList<>();
    }

    public void addAll(ArrayList<Map> list){

        for(int i=0;i<list.size();i++){
            String joined = list.get(i).get("joined").toString();

        }

    }

    public void clear(){
        arrayList.clear();
    }

    //add the eventdata into the arraylist to hold all of the events
    public void addEventData(Map<String, String> eventData){
        arrayList.add(eventData);
    }



    //return items in the arraylist
    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);

    }
    //set the title and date of the card holders in the Events fragment
    @Override
    public void onBindViewHolder(EventsCardHolder holder,
                                 int position) {


        final EventsCardHolder mainHolder = (EventsCardHolder) holder;
        //Setting text over textview
        mainHolder.title.setText(arrayList.get(position).get("title"));
        mainHolder.date.setText(arrayList.get(position).get("date"));
    }

    /*
     *set the on click listeners and start the event activity upon the click while also
     *transferring event information into the next inten
     */
    @Override
    public EventsCardHolder onCreateViewHolder(
            ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.events_row, viewGroup, false);
        final EventsCardHolder mainHolder = new EventsCardHolder(mainGroup) {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        final View.OnClickListener mOnClickListener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int position =mainHolder.getLayoutPosition();
                //create map and transfer event data to the next activity
                Map<String, String> event = arrayList.get(position);
                Intent eventIntent = new Intent(context, Event.class);
                eventIntent.putExtra("eventID", event.get("eventID"));
                eventIntent.putExtra("email",event.get("email"));
                eventIntent.putExtra("title", event.get("title"));
                eventIntent.putExtra("date", event.get("date"));
                eventIntent.putExtra("description", event.get("description"));
                eventIntent.putExtra("location", event.get("location"));
                eventIntent.putExtra("time", event.get("time"));
                eventIntent.putExtra("joined",event.get("joined"));
                context.startActivity(eventIntent);

            }


        };
        mainGroup.setOnClickListener(mOnClickListener);


        return mainHolder;

    }


}