package ca.brocku.kt13nh.Student_Connect.base_interface_java_v3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import ca.brocku.kt13nh.Student_Connect.tab_components.EventsFragment;
import ca.brocku.kt13nh.Student_Connect.tab_components.InboxFragment;
import ca.brocku.kt13nh.Student_Connect.tab_components.QaFragment;

/*
* Simple pager adapter for the purpose of obtaining tab positions and initialize a default
* */
public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    //set number of tabs
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    //return fragments based on the tab positions
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InboxFragment("Inbox");
            case 1:
                return new QaFragment("QA");
            case 2:
                return new EventsFragment("Events");
            default:
                return new InboxFragment("Inbox");
        }
    }
    //return number of tabs
    public int getCount() {
        return this.mNumOfTabs;
    }
}
