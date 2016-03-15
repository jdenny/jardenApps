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
import android.widget.CheckBox;
import android.widget.TextView;

import jarden.document.DocumentTextView;

public class HelpFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "HelpFragment";
    private CheckBox showHelpCheckBox;
    private CheckBox showTipsCheckBox;
    private EngSpaActivity engSpaActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_help, container, false);
        TextView helpTextView = (TextView) rootView.findViewById(R.id.helpTextView);
        helpTextView.setMovementMethod(new ScrollingMovementMethod());
        this.showHelpCheckBox = (CheckBox) rootView.findViewById(R.id.showHelpCheckBox);
        this.showHelpCheckBox.setOnClickListener(this);
        this.showTipsCheckBox = (CheckBox) rootView.findViewById(R.id.showTipsCheckBox);
        this.showTipsCheckBox.setOnClickListener(this);

        this.engSpaActivity = (EngSpaActivity) getActivity();
        SharedPreferences sharedPreferences = engSpaActivity.getSharedPreferences();
        boolean isShowHelp = sharedPreferences.getBoolean(
                EngSpaActivity.SHOW_HELP_KEY, true);
        this.showHelpCheckBox.setChecked(isShowHelp);
        boolean isShowTips = sharedPreferences.getBoolean(
                EngSpaActivity.SHOW_TIPS_KEY, true);
        this.showTipsCheckBox.setChecked(isShowTips);
        this.engSpaActivity.setTip(R.string.helpTip);

        int[] helpResIds = {
                R.string.HomeHelp,
                R.string.QuickStartHelp,
                R.string.QuestionsByLevelHelp,
                R.string.QuestionStyleHelp,
                R.string.WordLookupHelp,
                R.string.NumbersGameHelp,
                R.string.SelectTopicHelp,
                R.string.FeedbackHelp,
                R.string.incorrectButtonTip,
                R.string.HintsNTipsHelp
        };
        new DocumentTextView(getActivity().getApplicationContext(),
                helpTextView, helpResIds);
        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        helpTextView.setHighlightColor(Color.TRANSPARENT);
        return rootView;
    }

    @Override // OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.showHelpCheckBox) {
            boolean isShowHelp = showHelpCheckBox.isChecked();
            SharedPreferences.Editor editor =
                    this.engSpaActivity.getSharedPreferences().edit();
            editor.putBoolean(EngSpaActivity.SHOW_HELP_KEY, isShowHelp);
            editor.apply();
        } else if (id == R.id.showTipsCheckBox) {
            boolean isShowTips = showTipsCheckBox.isChecked();
            this.engSpaActivity.setShowTips(isShowTips);
        } else {
            Log.e(TAG, "unrecognised onClick Id: " + id);
        }

    }
}
