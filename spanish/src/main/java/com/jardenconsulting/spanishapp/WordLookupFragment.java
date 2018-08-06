package com.jardenconsulting.spanishapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.List;

import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.VerbUtils;
import jarden.engspa.VerbUtils.Person;
import jarden.engspa.VerbUtils.Tense;
import jarden.provider.engspa.EngSpaContract.WordType;

public class WordLookupFragment extends Fragment implements OnEditorActionListener,
        View.OnClickListener {
	public static final String TAG = "WordLookupFragment";

	private EditText spanishLookupEditText;
	private EditText englishLookupEditText;
	private ListView conjugationListView;
    private EngSpaDAO engSpaDAO;
	private ArrayAdapter<String> conjugateListAdapter;
	private EngSpaActivity engSpaActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
				(savedInstanceState==null?"":"not ") + "null)");
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) Log.d(TAG, "onCreateView()");
		this.engSpaActivity = (EngSpaActivity) getActivity();
		engSpaActivity.setTip(R.string.Word_Lookup);
		View rootView = inflater.inflate(R.layout.fragment_word_lookup, container, false);
		this.spanishLookupEditText = rootView.findViewById(R.id.spanishLookupEditText);
		this.englishLookupEditText = rootView.findViewById(R.id.englishLookupEditText);
		Button button = rootView.findViewById(R.id.clearSpanishButton);
        button.setOnClickListener(this);
        button = rootView.findViewById(R.id.clearEnglishButton);
        button.setOnClickListener(this);
        this.spanishLookupEditText.setOnEditorActionListener(this);
		this.englishLookupEditText.setOnEditorActionListener(this);
		this.conjugationListView = rootView.findViewById(R.id.conjugationListView);
		this.conjugateListAdapter = new ArrayAdapter<>(
				getActivity(), android.R.layout.simple_list_item_1);
		this.conjugationListView.setAdapter(conjugateListAdapter);

		return rootView;
	}

    /**
     * Lookup word, from either Spanish or English.
     * If Spanish or English word provided, translate that;
     * if both provided, use the word in focus;
     * if neither provided, prompt user to supply a word
     */
    private void goPressed() {
        String engStr = this.englishLookupEditText.getText().toString().trim();
        String spaStr = this.spanishLookupEditText.getText().toString().trim();
        boolean spaToEng = true;
        if (spaStr.length() > 0) {
            if (engStr.length() > 0) {
                View focusedView = getActivity().getCurrentFocus();
                if (focusedView == this.englishLookupEditText) spaToEng = false;
            }
        } else { // no spanish phrase supplied
            if (engStr.length() > 0) {
                spaToEng = false;
            } else {
                engSpaActivity.setStatus(R.string.supplyWord);
                return;
            }
        }
        // now we know which way to translate, as specified by spaToEng
        if (this.engSpaDAO == null) {
            this.engSpaDAO = engSpaActivity.getEngSpaDAO();
        }
        List<EngSpa> matches;
        if (spaToEng) {
            matches = engSpaDAO.getSpanishWord(spaStr);
        } else {
            matches = engSpaDAO.getEnglishWord(engStr);
        }
        this.conjugateListAdapter.setNotifyOnChange(false);
        this.conjugateListAdapter.clear();
        if (matches.size() < 1) {
            engSpaActivity.setStatus(
                    (spaToEng ? spaStr : engStr) + " not found in our dictionary");
        } else {
            engSpaActivity.setStatus(EngSpaActivity.CLEAR_STATUS);
            // TODO: sort out may have more than 1 match
            EngSpa engSpa = matches.get(0);
            String spanish = engSpa.getSpanish();
            engSpaActivity.setSpanish(spanish);
            this.spanishLookupEditText.setText(spanish);
            this.englishLookupEditText.setText(engSpa.getEnglish());
            if (engSpa.getWordType() == WordType.verb) {
                String english = engSpa.getEnglish();
                String line;
                for (Tense tense: Tense.values()) {
                    if (tense.isDiffPersons()) {
                        for (Person person: Person.values()) {
                            line = person.getSpaPronoun() + " " +
                                    VerbUtils.conjugateSpanishVerb(spanish, tense, person) + "; " +
                                    person.getEngPronoun() + " " +
                                    VerbUtils.conjugateEnglishVerb(english, tense, person);
                            conjugateListAdapter.add(line);
                        }
                    } else {
                        line = tense + ": " +
                                VerbUtils.conjugateSpanishVerb(spanish, tense, null) + "; " +
                                VerbUtils.conjugateEnglishVerb(english, tense, null);
                        conjugateListAdapter.add(line);
                    }
                }
            } else {
                for (EngSpa es: matches) {
                    conjugateListAdapter.add(es.getDictionaryString());
                }
            }

        }
		this.conjugateListAdapter.notifyDataSetChanged();
	}
	@Override
	public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
		boolean handled = false;
		if (actionId == EditorInfo.IME_ACTION_GO) {
			goPressed();
			handled = true;
		}
		return handled;
	}
    @Override // onClickListener
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.clearEnglishButton) {
            this.englishLookupEditText.getText().clear();
        } else if (id == R.id.clearSpanishButton) {
            this.spanishLookupEditText.getText().clear();
        }
    }
}
