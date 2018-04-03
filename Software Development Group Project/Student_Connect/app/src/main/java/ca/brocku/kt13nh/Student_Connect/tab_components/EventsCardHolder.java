package ca.brocku.kt13nh.Student_Connect.tab_components;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.brocku.kt13nh.Student_Connect.R;

public abstract class EventsCardHolder extends RecyclerView.ViewHolder {


    public TextView title;
    public Button date;

    public EventsCardHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.eventsTitle);
        this.date = (Button) view.findViewById(R.id.eventsDate);

    }


}