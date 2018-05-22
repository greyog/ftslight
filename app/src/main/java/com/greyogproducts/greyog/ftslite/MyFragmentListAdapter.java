package com.greyogproducts.greyog.ftslite;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.greyogproducts.greyog.ftslite.LoaderTask.DAY;
import static com.greyogproducts.greyog.ftslite.LoaderTask.HOUR;
import static com.greyogproducts.greyog.ftslite.LoaderTask.MIN15;
import static com.greyogproducts.greyog.ftslite.LoaderTask.MIN5;
import static com.greyogproducts.greyog.ftslite.LoaderTask.NAME;
import static com.greyogproducts.greyog.ftslite.MainTabsActivity.TAG;

/**
 * Created by mac on 12/11/2017.
 */

public class MyFragmentListAdapter extends ArrayAdapter {
//    private final String[] from = new String[] {"name", "price", "5min", "15min", "hour", "day"};
//    private final int[] to = new int[] {com.greyogproducts.greyog.ftslite.R.id.tvName, com.greyogproducts.greyog.ftslite.R.id.tvPrice, com.greyogproducts.greyog.ftslite.R.id.iv5min, com.greyogproducts.greyog.ftslite.R.id.iv15min, com.greyogproducts.greyog.ftslite.R.id.ivHour, com.greyogproducts.greyog.ftslite.R.id.ivDay};
    private final Context mContext;
    private final ArrayList<HashMap<String, String>> mItemList;
    private final ArrayList<Integer> mHiddenItems;
    private final String[] sortDir = new String[] {"Strong Buy", "Buy", "Neutral", "Sell", "Strong Sell"};

    MyFragmentListAdapter(@NonNull Context context, ArrayList<HashMap<String, String>> list) {
        super(context, com.greyogproducts.greyog.ftslite.R.layout.fragment_list_item, list);
        mContext = context;
        mItemList = list;
        mHiddenItems = new ArrayList<>();
        updateSortOptions();
        updateHiddenItems();
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        for (Integer hItem : mHiddenItems) {
            if (hItem <= position) position++;
        }
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null) {
                convertView = inflater.inflate(com.greyogproducts.greyog.ftslite.R.layout.fragment_list_item,parent,false);
            }
        }
        if (convertView != null) {

            TextView tvName = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.tvName);
            TextView tvPrice = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.tvPrice);
            ImageView iv5min = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.iv5min);
            ImageView iv15min = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.iv15min);
            ImageView ivHour = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.ivHour);
            ImageView ivDay = convertView.findViewById(com.greyogproducts.greyog.ftslite.R.id.ivDay);

            tvName.setText(mItemList.get(position).get("name"));
            tvPrice.setText(mItemList.get(position).get("price"));
            setImage(iv5min, mItemList.get(position).get("5min"));
            setImage(iv15min, mItemList.get(position).get("15min"));
            setImage(ivDay, mItemList.get(position).get("day"));
            setImage(ivHour, mItemList.get(position).get("hour"));
            return convertView;
        } else return super.getView(position, convertView, parent);

    }

    private void setImage(ImageView imageView, String s) {
        switch (s) {
            // TODO pomenyat' razmer strelok
            case "Strong Buy" :
                imageView.setImageResource(R.drawable.ic_strong_buy);
                break;
            case "Buy" :
                imageView.setImageResource(R.drawable.ic_buy);
                break;
            case "Sell" :
                imageView.setImageResource(R.drawable.ic_sell);
                break;
            case "Strong Sell" :
                imageView.setImageResource(R.drawable.ic_strong_sell);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_neutral);
        }
    }

    @Override
    public int getCount() {
        return mItemList.size() - mHiddenItems.size();
    }

    @Override
    public void notifyDataSetChanged() {
        updateSortOptions();
        updateHiddenItems();
        super.notifyDataSetChanged();
    }

    private int getSortResult(HashMap<String, String> t1, HashMap<String, String> t2, String field) {
        String n1 = t1.get(field);
        String n2 = t2.get(field);
        int pos1 = Arrays.binarySearch(sortDir, n1);
        int pos2 = Arrays.binarySearch(sortDir, n2);
        return ((int) Math.signum(pos1 - pos2));
    }

    private void updateSortOptions() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        int sort = prefs.getInt("sort", 1);
        Comparator<HashMap<String, String>> nameAscComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                String n1 = t1.get(NAME);
                String n2 = t2.get(NAME);
                return n1.compareToIgnoreCase(n2);
            }
        };
        Comparator<HashMap<String, String>> nameDescComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                String n1 = t1.get(NAME);
                String n2 = t2.get(NAME);
                return n2.compareToIgnoreCase(n1);
            }
        };

        Comparator<HashMap<String, String>> min5AscComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return getSortResult(t1,t2,MIN5);
            }
        };
        Comparator<HashMap<String, String>> min5DescComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return -getSortResult(t1,t2,MIN5);
            }
        };
        Comparator<HashMap<String, String>> min15AscComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return getSortResult(t1,t2,MIN15);
            }
        };
        Comparator<HashMap<String, String>> min15DescComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return -getSortResult(t1,t2,MIN15);
            }
        };
        Comparator<HashMap<String, String>> hourAscComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return getSortResult(t1,t2,HOUR);
            }
        };
        Comparator<HashMap<String, String>> hourDescComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return -getSortResult(t1,t2,HOUR);
            }
        };
        Comparator<HashMap<String, String>> dayAscComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
               return getSortResult(t1, t2, DAY);
            }
        };
        Comparator<HashMap<String, String>> dayDescComparator = new Comparator<HashMap<String, String>>() {
            @Override
            public int compare(HashMap<String, String> t1, HashMap<String, String> t2) {
                return -getSortResult(t1, t2, DAY);
            }
        };
        switch (sort) {
            case 1:
                Collections.sort(mItemList, nameAscComparator);
                break;
            case -1:
                Collections.sort(mItemList, nameDescComparator);
                break;
            case 2:
                Collections.sort(mItemList, min5AscComparator);
                break;
            case -2:
                Collections.sort(mItemList, min5DescComparator);
                break;
            case 3:
                Collections.sort(mItemList, min15AscComparator);
                break;
            case -3:
                Collections.sort(mItemList, min15DescComparator);
                break;
            case 4:
                Collections.sort(mItemList, hourAscComparator);
                break;
            case -4:
                Collections.sort(mItemList, hourDescComparator);
                break;
            case 5:
                Collections.sort(mItemList, dayAscComparator);
                break;
            case -5:
                Collections.sort(mItemList, dayDescComparator);
                break;
            default:
                Collections.sort(mItemList, nameAscComparator);
        }
        super.notifyDataSetChanged();
    }


    private void updateHiddenItems() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> hidden = prefs.getStringSet("hidden", new HashSet<String>());
        mHiddenItems.clear();
        for (int i = 0; i < mItemList.size(); i++) {
            if (hidden.contains(mItemList.get(i).get("name"))) mHiddenItems.add(i);
        }
        Log.d(TAG, "updateHiddenItems: mHiddenItems = "+mHiddenItems);
    }
}
