package com.greyogproducts.greyog.ftslite;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MyWebViewFragment extends WebViewFragment implements SectionsPagerAdapter.ChangeListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    static final String ARG_SECTION_NUMBER = "section_number";
//        private Context mContext;

    private int LOADER_ID = 1;

    public MyWebViewFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment newInstance(int sectionNumber) {
        MyWebViewFragment fragment = new MyWebViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        getWebView().getSettings().setJavaScriptEnabled(true);
        // настройка масштабирования
        getWebView().getSettings().setSupportZoom(true);
        getWebView().getSettings().setBuiltInZoomControls(true);
        getWebView().loadUrl("https://sslecal2.forexprostools.com/?columns=exc_flags,exc_currency,exc_importance,exc_actual,exc_forecast,exc_previous&features=datepicker,timezone&countries=25,32,6,37,72,22,17,39,14,10,35,43,56,36,110,11,26,12,4,5&calType=week&timeZone=55&lang=1");

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = new Bundle(getArguments());
        LOADER_ID = getArguments().getInt(ARG_SECTION_NUMBER);
//        mLoaderManager = getLoaderManager();
//        Log.d(TAG, "fragment.onViewCreated: loader id = "+LOADER_ID);
//        mLoaderManager.initLoader(LOADER_ID, args, this);

    }

    @Override
    public void reloadItems() {
        refresh();
    }

    @Override
    public void redrawItems() {

    }

    private void refresh() {
//        if (mLoaderManager != null) {
//            Loader<Object> loader = mLoaderManager.getLoader(LOADER_ID);
//            if (loader != null) loader.onContentChanged();
//        }
    }
}