package com.jardenconsulting.spanishapp;

import java.util.List;

import jarden.engspa.EngSpa;
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
	private TextView statusTextView;
	private EngSpaQuiz engSpaQuiz;
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
		engSpaActivity.setHelp(R.string.helpWordLookup);

		View rootView = inflater.inflate(R.layout.fragment_verb_table, container, false);
		this.spanishVerbEditText = (EditText) rootView.findViewById(R.id.spanishVerbEditText);
		this.englishVerbEditText = (EditText) rootView.findViewById(R.id.englishVerbEditText);
		this.spanishVerbEditText.setOnEditorActionListener(this);
		this.englishVerbEditText.setOnEditorActionListener(this);
		this.conjugationListView = (ListView) rootView.findViewById(R.id.conjugationListView);
		this.statusTextView = (TextView) rootView.findViewById(R.id.statusTextView);
		this.conjugateListAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1);
		this.conjugationListView.setAdapter(conjugateListAdapter);

		return rootView;
	}

	private void goPressed() {
		List<EngSpa> matches;
		if (this.engSpaQuiz == null) {
			this.engSpaQuiz = engSpaActivity.getEngSpaQuiz();
		}
		String verb = this.spanishVerbEditText.getText().toString().trim();
		if (verb.length() > 0) {
			matches = engSpaQuiz.spa2Eng(verb);
		} else {
			verb = this.englishVerbEditText.getText().toString().trim();
			if (verb.length() > 0) {
				matches = engSpaQuiz.eng2Spa(verb);
			} else {
				this.statusTextView.setText("please supply Spanish or English verb");
				return;
			}
		}
		if (matches.size() < 1) {
			this.statusTextView.setText(verb + " not found on our dictionary");
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
