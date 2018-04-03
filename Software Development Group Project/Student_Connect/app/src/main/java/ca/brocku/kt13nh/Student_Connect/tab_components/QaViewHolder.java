package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ca.brocku.kt13nh.Student_Connect.R;


public abstract class QaViewHolder extends RecyclerView.ViewHolder{


    public TextView title;
    public Button course;
    private OnRecyclerClickListener onRecyclerClickListener;

    public QaViewHolder(View view) {
        super(view);
        this.title = (TextView) view.findViewById(R.id.qaTitle);
        this.course = (Button) view.findViewById(R.id.qaView);
    }


    public void onRecyclerClickListener(View.OnLongClickListener onLongClickListener) {
    }
}
