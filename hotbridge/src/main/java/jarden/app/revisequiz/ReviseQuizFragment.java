package jarden.app.revisequiz;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.jardenconsulting.cardapp.BuildConfig;
import com.jardenconsulting.cardapp.HotBridgeActivity;
import com.jardenconsulting.cardapp.R;

import java.util.List;
import java.util.Set;

import jarden.app.dialog.IntegerDialog;
import jarden.quiz.BridgeQuiz;
import jarden.quiz.QuestionAnswer;

import static jarden.quiz.BridgeQuiz.OPENING_BIDS;

/*
 * Created by john.denny@gmail.com on 15/10/2018.

TODO:
* new bid search not working with competitive bids!
* perhaps hold bidList as QuestionAnswer array, then use adapter to
    display qa.question = qa.answer;
    see https://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView
* long press on response, then Wrong marks the original bid as wrong, not the current
* show bid sequence in columns
* Add to document: meaning of 3H/3S; comprehensive responses to fit
* Add levels, to PresetQuiz and here
    Level 2 (all following QA at level 2 until told otherwise)
* add spy glass icon for bid search
* mark all the raw bids
 */
public class ReviseQuizFragment extends FreakWizFragment
        implements AdapterView.OnItemClickListener, IntegerDialog.IntValueListener,
        AdapterView.OnItemLongClickListener {

    private static final String QUESTION_INDEX_KEY = "questionIndexKey";
    private static final String TARGET_CORRECTS_KEY = "targetCorrectsKey";
    private static final String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";

    private CheckBox notesCheckBox;
    private ListView bidListView;
    private BidListAdapter bidListAdapter;
    private boolean changingQuestionIndex;
    private IntegerDialog integerDialog;

    private BridgeQuiz bridgeQuiz;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG,
                "ReviseQuizFragment.onCreateView()");
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        this.notesCheckBox = new CheckBox(getContext());
        notesCheckBox.setText(R.string.notes);
        selfMarkLayout.addView(notesCheckBox, -1);
        notesCheckBox.setId(R.id.notes2CheckBox);
        this.notesTextView.setVisibility(View.GONE);

        notesCheckBox.setOnClickListener(this);
        this.bidListAdapter = new BidListAdapter(getActivity());
        this.bidListView = rootView.findViewById(R.id.listView);
        this.bidListView.setAdapter(bidListAdapter);
        this.bidListView.setOnItemClickListener(this);
        this.bidListView.setOnItemLongClickListener(this);
        this.notesTextView = rootView.findViewById(R.id.notesTextView);
        bridgeQuiz = (BridgeQuiz) reviseQuiz;

        this.sharedPreferences = getActivity().getSharedPreferences(HotBridgeActivity.TAG,
                Context.MODE_PRIVATE);
        boolean learnMode = sharedPreferences.getBoolean(LEARN_MODE_KEY, true);
        int savedQuestionIndex = sharedPreferences.getInt(QUESTION_INDEX_KEY, -1);
        if (savedQuestionIndex > 0) {
            bridgeQuiz.setQuestionIndex(savedQuestionIndex);
        }
        int savedTargetsCorrect = sharedPreferences.getInt(TARGET_CORRECTS_KEY, -1);
        if (savedTargetsCorrect > 0) {
            bridgeQuiz.setTargetCorrectCt(savedTargetsCorrect);
        }
        String failIndexStr = sharedPreferences.getString(FAIL_INDICES_KEY, "");
        if (failIndexStr.length() > 0) {
            String[] failIndices = failIndexStr.split(",");
            bridgeQuiz.setFailIndices(failIndices);
        }
        setLearnMode(learnMode);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG,
                "ReviseQuizFragment.onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG, "ReviseQuizFragment.onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.revise, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.learnModeButton) {
            setLearnMode(true);
            askQuestion();
        } else if (id == R.id.practiceModeButton) {
            setLearnMode(false);
            askQuestion();
        } else if (id == R.id.setCurrentIndexButton) {
            // if we add more ints for the user to update, then
            // change this boolean to an enum
            changingQuestionIndex = true;
            showIntegerDialog("Change Current Index",
                    reviseQuiz.getQuestionIndex() + 1,
                    "UserLevelDialog");
        } else if (id == R.id.setTargetCorrectsButton) {
            changingQuestionIndex = false;
            showIntegerDialog("Change Target Correct",
                    reviseQuiz.getTargetCorrectCt(),
                    "TargetCorrectsDialog");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void setLearnMode(boolean learnMode) {
        bridgeQuiz.setLearnMode(learnMode);
        getActivity().setTitle(getQuizTitleId());
    }
    public int getQuizTitleId() {
        return bridgeQuiz.isLearnMode() ? R.string.learnMode : R.string.practiceMode;
    }
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG, "ReviseQuizFragment.onResume()");
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG, "ReviseQuizFragment.onPause()");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int questionIndex = bridgeQuiz.getQuestionIndex();
        editor.putInt(QUESTION_INDEX_KEY, questionIndex);
        int targetCorrectCt = bridgeQuiz.getTargetCorrectCt();
        editor.putInt(TARGET_CORRECTS_KEY, targetCorrectCt);
        editor.putBoolean(LEARN_MODE_KEY, bridgeQuiz.isLearnMode());
        Set<Integer> failedIndexSet = bridgeQuiz.getFailedIndexSet();
        String failStr;
        if (failedIndexSet.size() == 0) {
            failStr = "";
        } else {
            StringBuilder sBuilder = new StringBuilder();
            for (int failIndex : failedIndexSet) {
                sBuilder.append(failIndex).append(",");
            }
            failStr = sBuilder.substring(0, sBuilder.length() - 1);
        }
        editor.putString(FAIL_INDICES_KEY, failStr);
        editor.apply();
    }
    private void showIntegerDialog(String title, int value, String tag) {
        if (integerDialog == null) {
            integerDialog = new IntegerDialog();
            integerDialog.setIntValueListener(this);
        }
        this.integerDialog.setTitle(title);
        this.integerDialog.setIntValue(value);
        this.integerDialog.show(getActivity().getSupportFragmentManager(), tag);
    }
    @Override // IntValueListener
    public void onUpdateIntValue(int intValue) {
        if (changingQuestionIndex) {
            // to people, ordinals start from 1; to computers, they start from 0
            this.reviseQuiz.setQuestionIndex(intValue - 1);
            askQuestion();
        } else {
            this.reviseQuiz.setTargetCorrectCt(intValue);
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notes2CheckBox) {
            if (this.notesCheckBox.isChecked()) {
                this.bidListView.setVisibility(View.GONE);
                this.notesTextView.setVisibility(View.VISIBLE);
            } else {
                this.bidListView.setVisibility(View.VISIBLE);
                this.notesTextView.setVisibility(View.GONE);
            }
        } else super.onClick(view);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionAnswer qa = bidListAdapter.getItem(position);
        if (qa == null) {
            Toast.makeText(getContext(),
                    "item selected at position " + position + " is null!",
                    Toast.LENGTH_LONG).show();
        } else {
            loadBidList(qa);
        }
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionAnswer qa = bidListAdapter.getItem(position);
        if (qa != OPENING_BIDS) {
            this.questionTextView.setText(qa.question);
            this.answerTextView.setText(qa.answer);
            this.documentTextView.showPageText(qa.notes, "Notes");
        }
        loadBidList(qa);
        return true;
    }
    @Override
    public void resetListView() {
        this.bidListAdapter.clear();
    }
    /**
     * Break down question into component bids.
     * Examples:
     *     BID_LIST for Q: 1C, 1H; 2D
     *         1C = ...
     *         1C, 1H = ...
     *         1C, 1H; 2D = ...
     *     BID_LIST for Q: 1C, (1S) double; (pass) 2NT
     *         1C = ...
     *         1C, (1S) double = ...
     *         1C, (1S) double; (pass) 2NT = ...
     */
    @Override
    public void setListView() {
        String bidSequence = this.questionTextView.getText().toString();
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        bidListAdapter.add(OPENING_BIDS);
        List<String> bidList = BridgeQuiz.getBidItems(bidSequence);
        QuestionAnswer nextQA;
        for (String bid: bidList) {
            nextQA = bridgeQuiz.findNextBidAnswer(bid);
            bidListAdapter.add(nextQA);
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
    /*
    Get all responses to selected bid sequence
        BID_LIST for Q: 1C, 1H; 2D
            1C = ...
            1C, 1H = ...
            1C, 1H; 2D = ...
        click on 1C
            opening bids
            1C, 1D = ...
            1C, 1H = ...
        click on 1C, 1H
            opening bids
            1C = ...
            1C, 1H; 1S = ...
            1C, 1H; 1NT = ...

        BID_LIST for Q: 1C, (1S) double; (pass) 2NT
            1C = ...
            1C, (1S) double = ...
            1C, (1S) double; (pass) 2NT = ...
        click on 1C
            opening bids
            1C, 1D = ...
            1C, 1H = ...
        click on 1C, (1S) double
            opening bids
            1C = ...
            1C, (1S) double; (pass) 2NT

        BID_LIST for Q: (1S) double, (pass) 2NT
            opening bids
            (1S) double = ...
            (1S) double, (pass) 2NT
        click on (1S) double
            opening bids
            (1S) double, (pass) 2NT
     */
    public void loadBidList(QuestionAnswer targetQA) {
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        if (targetQA != OPENING_BIDS) {
            bidListAdapter.add(OPENING_BIDS);
            String question = targetQA.question;
            int i = question.indexOf(',');
            if (i >= 0) {
                QuestionAnswer qa = bridgeQuiz.getBackBid(question);
                bidListAdapter.add(qa);
            }
        }
        List<QuestionAnswer> possibleResponses = bridgeQuiz.getPossibleResponses(targetQA);
        for (QuestionAnswer qa : possibleResponses) {
            bidListAdapter.add(qa);
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
}
