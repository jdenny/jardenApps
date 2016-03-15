package com.jardenconsulting.drawerdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by john on 10/03/2016.
 */
public class ViewlessFragment extends Fragment {
    private static final String TAG = "ViewlessFragment";
    private Person person;

    @Override // Fragment
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }
    @Override // Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
                (savedInstanceState == null ? "" : "not ") + "null)");
        setRetainInstance(true);
        person = new Person("John", "john@work.com", "1234");
    }

    /*!!
    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.w(TAG, "onCreateView(); we weren't expecting this!");
        return null;
    }
    */
    @Override // Fragment
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
        this.person.incrementCount();
        Log.d(TAG, "person=" + person);
    }
    @Override // Fragment
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "onPause()");
    }
    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }
}
