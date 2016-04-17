package com.jardenconsulting.drawerdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import jarden.document.DocumentTextView;

public class HelpFragment extends Fragment implements View.OnClickListener,
        DocumentTextView.OnShowPageListener {
    private static final String TAG = "HelpFragment";

    private TextView helpTextView;
    private TextView statusTextView;
    private DocumentTextView documentTextView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        this.helpTextView = (TextView) rootView.findViewById(R.id.helpTextView);
        this.statusTextView = (TextView) rootView.findViewById(R.id.statusTextView);
        Button homeButton = (Button) rootView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);

        Resources resources = getResources();
        int[] helpResIds = {
                R.string.Home,
                R.string.EngSpa,
                R.string.WordLookup
        };
        this.documentTextView = new DocumentTextView(getActivity().getApplicationContext(),
                helpTextView, helpResIds, this);

        //?? helpTextView.setMovementMethod(new ScrollingMovementMethod());
        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        helpTextView.setHighlightColor(Color.TRANSPARENT);
        return rootView;
    }
    @Override // Fragment
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
    }
    @Override // Fragment
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "onPause()");
    }
    @Override // Fragment
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override // DocumentTextView.OnShowPageListener
    public void onShowPage(String pageName) {
        getActivity().setTitle(pageName);
    }

    @Override // OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.homeButton) {
            this.documentTextView.showHomePage();
        } else {
            Log.e(TAG, "unrecognised onClick Id: " + id);
        }
    }
}
