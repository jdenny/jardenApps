package jarden.app.revisequiz;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
        implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {

    private CheckBox notesCheckBox;
    private ListView bidListView;
    private BidListAdapter bidListAdapter;

    private BridgeQuiz bridgeQuiz;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG,
                "ReviseQuizFragment.onCreateView()");
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
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

    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG, "ReviseQuizFragment.onResume()");
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(HotBridgeActivity.TAG, "ReviseQuizFragment.onPause()");
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
        if (qa != null && qa != OPENING_BIDS) {
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
    private void loadBidList(QuestionAnswer targetQA) {
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
