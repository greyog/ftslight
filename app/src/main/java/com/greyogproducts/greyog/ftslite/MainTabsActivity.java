package com.greyogproducts.greyog.ftslite;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.Switch;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.ads.AdRequest;

import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainTabsActivity extends AppCompatActivity  {
    static final String TAG = "ftslitelog";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    private Toolbar toolbar;
    private Timer mUpdTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.greyogproducts.greyog.ftslite.R.layout.activity_main_tab);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(com.greyogproducts.greyog.ftslite.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(com.greyogproducts.greyog.ftslite.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setVisibility(View.GONE);

        setUpdTimer();

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.greyogproducts.greyog.ftslite.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.animate().rotation(view.getRotation() + 360f).setDuration(1000).setInterpolator(new AccelerateDecelerateInterpolator());
                showFilterDialog();
//                Snackbar.make(view, "Replace me", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });

        String appKey = "550b7f6e67495bdd809780ce69fbb7edcb62475c3368b540";
        Appodeal.initialize(this, appKey, Appodeal.INTERSTITIAL | Appodeal.BANNER | Appodeal.NATIVE);
        Appodeal.show(this, Appodeal.BANNER_BOTTOM);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Appodeal.onResume(this, Appodeal.BANNER);
    }

    private void showFilterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        Log.d(TAG, "showFilterDialog: prefs = " + prefs.getAll());
        final Set<String> names = prefs.getStringSet("name_set", new HashSet<String>());
//        Log.d(TAG, "showFilterDialog: names = "+ names);
        final Set<String> hidden = prefs.getStringSet("hidden", new HashSet<String>());
//        Log.d(TAG, "showFilterDialog: hidden = "+ hidden);
        boolean[] visible = new boolean[names.size()];
        for (int i =0; i < names.toArray().length; i++) {
            visible[i] = !hidden.contains(names.toArray()[i].toString());
        }
//        Log.d(TAG, "showFilterDialog: visible = " +visible.toString());
        DialogInterface.OnMultiChoiceClickListener listener = new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if (b && hidden.contains(names.toArray()[i].toString())) hidden.remove(names.toArray()[i].toString());
                if (!b) hidden.add((String) names.toArray()[i]);
                prefs.edit().putStringSet("hidden", hidden).apply();
                redrawFragments();
            }
        };
        builder.setMultiChoiceItems(names.toArray(new String[names.size()]), visible, listener);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    private void reloadFragments() {
        mSectionsPagerAdapter.reloadFragments();
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL) && Math.random()>0.8) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
    }



    private void redrawFragments() {mSectionsPagerAdapter.redrawFragments();}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.greyogproducts.greyog.ftslite.R.menu.menu_main_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_refresh :
                reloadFragments();
                break;
            case R.id.action_sort :
                showSortDialog();
                break;
            case R.id.action_set_auto_update :
                showAutoUpdateDialog();
                break;
            case R.id.action_about :
                showAboutDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final View v = getLayoutInflater().inflate(R.layout.about_layout, null);
        builder.setTitle(R.string.action_about);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }

    private void showAutoUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final View v = getLayoutInflater().inflate(R.layout.auto_update_layout, null);
        final Switch sw = v.findViewById(R.id.swAutoUpd);
        sw.setChecked(prefs.getBoolean("auto", false));
        final EditText ed = v.findViewById(R.id.etAutoUpd);
        ed.setText((String.valueOf(prefs.getInt("auto_time", 5))));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: sw, edt = "+sw.isChecked() + ed.getText().toString());
                int time = Integer.parseInt(ed.getText().toString());
                Log.d(TAG, "onClick: time = " + time);
                if (time <= 0) time = 5;
                prefs.edit().putInt("auto_time", time).putBoolean("auto",sw.isChecked()).apply();
                setUpdTimer();
            }
        };
//        builder.setTitle(R.string.action_auto_update);
        builder.setView(v);
        builder.setPositiveButton(R.string.ok, listener);
        builder.create().show();
    }

    private void setUpdTimer() {
        if (mUpdTimer != null) mUpdTimer.cancel();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("auto", false)) return;
        final int minutes = prefs.getInt("auto_time", 5);
        mUpdTimer = new Timer("autoUpdTimer");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reloadFragments();
                        Log.d(TAG, "run: autoUpdTimer, time = "+ minutes);
                    }
                });
            }
        };
        mUpdTimer.schedule(task, minutes*60*1000, minutes*60000);
        Log.d(TAG, "setUpdTimer: set timer, time = " + minutes*60000);

    }

    private void showSortDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int sort = prefs.getInt("sort", 1);
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int prefSort = prefs.getInt("sort", 1);
                Log.d(TAG, "onClick: before i , sort = "+ i + " , " + prefSort);
                i++;
//                TextView tv = null;
//                switch (i) {
//                    case 2:
//                        tv = (TextView) findViewById(R.id.textView1);
//                        break;
//                    case 3:
//                        tv = (TextView) findViewById(R.id.textView2);
//                        break;
//                    case 4:
//                        tv = (TextView) findViewById(R.id.textView3);
//                        break;
//                    case 5:
//                        tv = (TextView) findViewById(R.id.textView4);
//                        break;
//                }
                if (i == Math.abs(prefSort)) {
                    i = -prefSort;
//                    if (tv != null) {
//                        String txt = (String) tv.getText();
//                        char lastChar = txt.toCharArray()[txt.length()-1];
//                        Log.d(TAG, "onClick: char = " + lastChar);
////                        /u22c5
//                        if (lastChar != '\u22c5') {
//                            txt = txt + '\u22c5';
//                        }
//                    }
                }
                prefs.edit().putInt("sort", i).apply();
                Log.d(TAG, "onClick: after i = " + i);
                redrawFragments();
                dialogInterface.dismiss();

            }
        };
        builder.setTitle(R.string.sort_by);
        builder.setSingleChoiceItems(R.array.sort_array, Math.abs(sort) - 1, listener);
        builder.setPositiveButton(R.string.ok, null);
        builder.create().show();
    }


    public void onListHeaderClick(View view) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final int sort = prefs.getInt("sort", 1);
        int i = 1;
        switch (view.getId()) {
            case R.id.textView1: i = 2;
                break;
            case R.id.textView2: i = 3;
                break;
            case R.id.textView3: i = 4;
                break;
            case R.id.textView4: i = 5;
                break;
        }
        if (i == Math.abs(sort)) i = -sort;
        prefs.edit().putInt("sort", i).apply();
        redrawFragments();
    }

    public void setUpdTime(String s) {
        if (s == null) toolbar.setTitle(R.string.empty);
        toolbar.setTitle(getString(R.string.last_upd));
        toolbar.setSubtitle(s);
    }
}
