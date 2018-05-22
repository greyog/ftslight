package com.greyogproducts.greyog.ftslite;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public interface ChangeListener {
        void reloadItems();
        void redrawItems();
    }

    private ArrayList<Fragment> fragments;

    SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment retFr;
        switch (position) {
            case 1:
                retFr = MyWebViewFragment.newInstance(position);
                break;
            default:
                retFr = PlaceholderFragment.newInstance(position);
        }
        fragments.add(retFr);
        return retFr;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "FOREX";
            case 1:
                return "NEWS";
            case 2:
                return "SECTION 3";
        }
        return null;
    }

    void reloadFragments() {
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) instanceof ChangeListener) {
//                Log.d(TAG, "reloadFragments: item num = "+i);
                ((ChangeListener) fragments.get(i)).reloadItems();
            }
        }
    }
    void redrawFragments() {
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i) instanceof ChangeListener) {
//                Log.d(TAG, "reloadFragments: item num = "+i);
                ((ChangeListener) fragments.get(i)).redrawItems();
            }
        }
    }
}