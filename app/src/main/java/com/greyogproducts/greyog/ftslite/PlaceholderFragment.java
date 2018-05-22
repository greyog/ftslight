package com.greyogproducts.greyog.ftslite;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.greyogproducts.greyog.ftslite.LoaderTask.NAME;
import static com.greyogproducts.greyog.ftslite.LoaderTask.TIME;
import static com.greyogproducts.greyog.ftslite.MainTabsActivity.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends SwipeRefreshListFragment implements SectionsPagerAdapter.ChangeListener, LoaderManager.LoaderCallbacks<ArrayList<HashMap<String,String>>>{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    static final String ARG_SECTION_NUMBER = "section_number";
//        private Context mContext;

    private int LOADER_ID = 1;
    private static LoaderManager mLoaderManager;

    public PlaceholderFragment() {
    }
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View srlFragment = super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.list_fragment_layout, container, false);
//        SwipeRefreshLayout srl = parent.findViewById(R.id.srl);
        ListView lv = parent.findViewById(android.R.id.list);
        int ind = parent.indexOfChild(lv);
        parent.addView(srlFragment, ind, lv.getLayoutParams());

        return parent;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = new Bundle(getArguments());
        LOADER_ID = getArguments().getInt(ARG_SECTION_NUMBER);
        mLoaderManager = getLoaderManager();
//        Log.d(TAG, "fragment.onViewCreated: loader id = "+LOADER_ID);
        mLoaderManager.initLoader(LOADER_ID, args, this);


        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

    }

    @Override
    public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle args) {
//            Log.d(TAG, "onCreateLoader: id, args = "+ id + args);
        LoaderTask mLoader = new LoaderTask(getContext(), args);
        mLoader.forceLoad();
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> data) {
//        Log.d(TAG, "onLoadFinished: list count = "+ data.size());
        if (data.size() == 0) {
            setEmptyText(getText(R.string.nothing_to_show));
            setListAdapter(null);
//            setListShown(false);
            setRefreshing(false);
            Toast.makeText(getContext(), getText(R.string.unable_load), Toast.LENGTH_SHORT).show();
//            ((MainTabsActivity) getActivity()).setUpdTime(null);
            return;
        }
        MyFragmentListAdapter adapter = new MyFragmentListAdapter(getContext(), data);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> stringSet = new HashSet<>();
        for (HashMap<String, String> map : data) {
            stringSet.add(map.get(NAME));
        }
        Log.d(TAG, "onLoadFinished: stringSet = " + stringSet);
        prefs.edit().putStringSet("name_set", stringSet).apply();
        setListAdapter(adapter);
//        Toast.makeText(getContext(),
//                getText(R.string.load_success) + " " + String.valueOf(data.size()) + " " + getText(R.string.items),
//                Toast.LENGTH_SHORT)
//                .show();
        ((MainTabsActivity) getActivity()).setUpdTime(data.get(0).get(TIME));
        setRefreshing(false);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.d(TAG, "onLoaderReset: ");
//        setListAdapter(null);

    }

    @Override
    public void reloadItems() {
        if (!isRefreshing()) setRefreshing(true);
        refresh();
    }

    @Override
    public void redrawItems() {
        ((MyFragmentListAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private void refresh() {
        if (mLoaderManager != null) {
            Loader<Object> loader = mLoaderManager.getLoader(LOADER_ID);
            if (loader != null) loader.onContentChanged();
        }
    }
}