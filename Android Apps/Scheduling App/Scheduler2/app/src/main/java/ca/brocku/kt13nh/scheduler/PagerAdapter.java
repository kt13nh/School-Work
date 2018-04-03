package ca.brocku.kt13nh.scheduler;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AllFragment tab1 = new AllFragment();
                return tab1;
            case 1:
                TodayFragment tab2 = new TodayFragment();
                return tab2;
            default:
                TomorrowFragment tab3 = new TomorrowFragment();
                return tab3;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}