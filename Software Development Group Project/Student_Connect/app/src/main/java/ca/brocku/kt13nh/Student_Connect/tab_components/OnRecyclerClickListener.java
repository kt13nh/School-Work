package ca.brocku.kt13nh.Student_Connect.tab_components;

import android.view.View;


public interface OnRecyclerClickListener extends View.OnClickListener {

    void onLongClick(View view);

    @Override
    void onClick(View view);

    void recyclerViewListClicked(View v, int position);
}