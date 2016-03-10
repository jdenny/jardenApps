package com.jardenconsulting.spanishapp;

import java.util.List;

import jarden.engspa.EngSpa;
import jarden.engspa.EngSpaDAO;
import jarden.engspa.EngSpaQuiz;
import jarden.engspa.VerbUtils;
import jarden.engspa.VerbUtils.Person;
import jarden.engspa.VerbUtils.Tense;
import jarden.provider.engspa.EngSpaContract.WordType;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class WordLookupFragment extends Fragment implements OnEditorActionListener {
	public static final String TAG = "WordLookupFragment";

	private EditText spanishVerbEditText;
	private EditText englishVerbEditText;
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
		engSpaActivity.setTip(R.string.WordLookupTip);
        engSpaActivity.setAppBarTitle(R.string.wordLookupLit);

		View rootView = inflater.inflate(R.layout.fragment_word_lookup, container, false);
		this.spanishVerbEditText = (EditText) rootView.findViewById(R.id.spanishVerbEditText);
		this.englishVerbEditText = (EditText) rootView.findViewById(R.id.englishVerbEditText);
		this.spanishVerbEditText.setOnEditorActionListener(this);
		this.englishVerbEditText.setOnEditorActionListener(this);
		this.conjugationListView = (ListView) rootView.findViewById(R.id.conjugationListView);
		this.conjugateListAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1);
		this.conjugationListView.setAdapter(conjugateListAdapter);

		return rootView;
	}

	private void goPressed() {
		List<EngSpa> matches;
		if (this.engSpaDAO == null) {
			this.engSpaDAO = engSpaActivity.getEngSpaDAO();
		}
		String wordStr = this.spanishVerbEditText.getText().toString().trim();
		if (wordStr.length() > 0) {
			matches = engSpaDAO.getSpanishWord(wordStr);
		} else {
			wordStr = this.englishVerbEditText.getText().toString().trim();
			if (wordStr.length() > 0) {
				matches = engSpaDAO.getEnglishWord(wordStr);
			} else {
				engSpaActivity.setStatus(R.string.supplyWord);
				return;
			}
		}
		if (matches.size() < 1) {
			engSpaActivity.setStatus(wordStr + " not found on our dictionary");
			return;
		}
		// TODO: sort out may have more than 1 match
		EngSpa engSpa = matches.get(0);
		String spanish = engSpa.getSpanish();
		engSpaActivity.setSpanish(spanish);
		this.conjugateListAdapter.setNotifyOnChange(false);
		this.conjugateListAdapter.clear();
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
}
