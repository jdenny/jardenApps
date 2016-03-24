package com.jardenconsulting.spanishapp;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import jarden.document.DocumentTextView;

public class HelpFragment extends Fragment implements View.OnClickListener,
        DocumentTextView.OnShowPageListener {
    private static final String TAG = "HelpFragment";
    private EngSpaActivity engSpaActivity;
    private DocumentTextView documentTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        TextView helpTextView = (TextView) rootView.findViewById(R.id.helpTextView);
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
        Button homeButton = (Button) rootView.findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);
        Button reviseButton = (Button) rootView.findViewById(R.id.reviseButton);
        reviseButton.setOnClickListener(this);

        this.engSpaActivity = (EngSpaActivity) getActivity();
        this.engSpaActivity.setTip(R.string.helpTip);

        int[] helpResIds = {
                R.string.HomeHelp,
                R.string.QuickStartHelp,
                R.string.MoreQuickHelp,
                R.string.QuestionsByLevelHelp,
                R.string.QuestionStyleHelp,
                R.string.SelectTopicHelp,
                R.string.FeedbackHelp,
                R.string.SelfMarkHelp,
                R.string.NumbersGameHelp,
                R.string.WordLookupHelp,
                R.string.HintsNTipsHelp
        };
        this.documentTextView = new DocumentTextView(
                getActivity().getApplicationContext(),
                helpTextView, helpResIds, this);
        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        helpTextView.setHighlightColor(Color.TRANSPARENT);
        return rootView;
    }

    @Override // OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.homeButton) {
            this.documentTextView.showHomePage();
        } else if (id == R.id.reviseButton) {
            this.engSpaActivity.showEngSpaFragment();
        } else {
            Log.e(TAG, "unrecognised onClick Id: " + id);
        }

    }
    @Override
    public void onShowPage(String pageName) {
        this.engSpaActivity.setAppBarTitle(pageName);
    }
}
