package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ca.brocku.kt13nh.Student_Connect.MainActivity;
import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.base_interface_java_v3.Home;

public abstract class DemoViewHolder extends RecyclerView.ViewHolder {


    public TextView title;
    private View view;
    private OnRecyclerClickListener onRecyclerClickListener;

    public DemoViewHolder(View view) {
        super(view);
        this.view = view;

        this.title = (TextView) view.findViewById(R.id.cardTitle);

    }
    public View getView(){
        return this.view;
    }

    public void onRecyclerClickListener(View.OnLongClickListener onLongClickListener) {

    }
}