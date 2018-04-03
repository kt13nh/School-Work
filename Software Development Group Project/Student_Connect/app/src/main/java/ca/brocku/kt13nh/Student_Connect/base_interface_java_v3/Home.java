package ca.brocku.kt13nh.Student_Connect.base_interface_java_v3;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.design.widget.TabLayout.TabLayoutOnPageChangeListener;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import ca.brocku.kt13nh.Student_Connect.R;
import ca.brocku.kt13nh.Student_Connect.floating_action_button_components.ChatroomCreatorDialog;
import ca.brocku.kt13nh.Student_Connect.floating_action_button_components.NewEvent;
import ca.brocku.kt13nh.Student_Connect.floating_action_button_components.NewQuestion;

public class Home extends Fragment {
    private int tabPosition = 0;
    private View view;

    /*
    * Initialize floating action button and set positions of the tab
    * */
    public void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Home.this.tabPosition == 0) {
                    Dialog chatCreatorDialog = new ChatroomCreatorDialog(view.getContext());
                    chatCreatorDialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    chatCreatorDialog.setTitle("Create Private Chatroom");
                    chatCreatorDialog.show();
                }
                if (Home.this.tabPosition == 1) {
                    //Home.this.startActivity(new Intent(Home.this.getActivity(), NewQuestion.class));
                    Intent newQuestion = new Intent(getActivity(),NewQuestion.class);
                    startActivity(newQuestion);
                } else if (Home.this.tabPosition == 2) {
                    Intent newEvent = new Intent(getActivity(),NewEvent.class);
                    startActivity(newEvent);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.view = inflater.inflate(R.layout.activity_main, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar((Toolbar) this.view.findViewById(R.id.toolbar));
        inflateTabs(this.view, (TabLayout) this.view.findViewById(R.id.tab_layout));
        initFloatingActionButton();

        return this.view;
    }



    public void inflateTabs(View view, TabLayout tabLayout) {
        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabLayout.addTab(tabLayout.newTab().setText((CharSequence) "Inbox"));
        tabLayout.addTab(tabLayout.newTab().setText((CharSequence) "Q/A"));
        tabLayout.addTab(tabLayout.newTab().setText((CharSequence) "Events"));
        tabLayout.setTabGravity(0);
        viewPager.setAdapter(new PagerAdapter(getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new OnTabSelectedListener() {
            public void onTabSelected(Tab tab) {
                Home.this.tabPosition = tab.getPosition();
                //System.out.println(Home.this.tabPosition);
                viewPager.setCurrentItem(tab.getPosition());

            }

            public void onTabUnselected(Tab tab) {
            }

            public void onTabReselected(Tab tab) {
            }
        });
    }
}
