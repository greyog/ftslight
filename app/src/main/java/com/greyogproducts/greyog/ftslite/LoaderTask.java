package com.greyogproducts.greyog.ftslite;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.greyogproducts.greyog.ftslite.PlaceholderFragment.ARG_SECTION_NUMBER;
import static com.greyogproducts.greyog.ftslite.MainTabsActivity.TAG;

class LoaderTask extends AsyncTaskLoader<ArrayList<HashMap<String,String>>> {

    static final String NAME = "name";
    private static final String PRICE = "price";
    static final String MIN5 = "5min";
    static final String MIN15 = "15min";
    static final String HOUR = "hour";
    static final String DAY = "day";
    static final String TIME = "time";
    private int mTabNum;
    private ArrayList<HashMap<String,String>> list;
    private String resultJson = "";
    private Date updTime;

    LoaderTask(Context context, Bundle args) {
        super(context);
//        Log.d(TAG, "LoaderTask: constructor, args = "+ args);
        if (args != null) {
            mTabNum = args.getInt(ARG_SECTION_NUMBER);
//            Log.d(TAG, "LoaderTask.init: mTabNum, id = "+mTabNum+" , "+ getId());
//            mTabNum = getId();

        }
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged() || list == null) {
//            Log.d(TAG, "onStartLoading: forceLoad");
            forceLoad();
        }
    }

    @Override
    public ArrayList<HashMap<String, String>> loadInBackground() {
//                Log.d(TAG, "loadInBackground: 123");
//        updTime = null;
        try {
            Log.d(TAG, "doInBackground: tab = " + mTabNum);
            URL url;
            switch (mTabNum) {
                case 1 : url = new URL("http://94.130.151.136/fts/script02.php");
                    break;
                default : url = new URL("http://94.130.151.136/fts/script02.php");

            }
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder stringBuffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }
            resultJson = stringBuffer.toString();
            updTime = Calendar.getInstance().getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }
        list = new ArrayList<>();

        JSONArray arrayPairs;
        try {
            arrayPairs = new JSONArray(resultJson);
            for (int i = 0; i < arrayPairs.length(); i++) {
                JSONObject jsonObject = arrayPairs.getJSONObject(i);
                String name = jsonObject.getString(NAME);
                String price = jsonObject.getString(PRICE);
                String min5 = jsonObject.getString(MIN5);
                String min15 = jsonObject.getString(MIN15);
                String hour = jsonObject.getString(HOUR);
                String day = jsonObject.getString(DAY);

                HashMap<String,String> map = new HashMap<>();
                map.put(NAME, name);
                map.put(PRICE, price);
                map.put(MIN5, min5);
                map.put(MIN15, min15);
                map.put(HOUR, hour);
                map.put(DAY, day);
                map.put(TIME, updTime.toString());
                list.add(map);
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.d(TAG, "onPostExecute: list "+ list.toString());
//                if (!list.isEmpty()) {
//                    SimpleAdapter adapter = new SimpleAdapter(mContext,list,
//                            android.R.layout.simple_list_item_2,
//                            new String[] {"name", "price"},
//                            new int[]{android.R.id.text1, android.R.id.text2});

//                }
        return list;
    }

}
