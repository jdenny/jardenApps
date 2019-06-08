package jarden.app.revisequiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.jardenconsulting.cardapp.R;

import java.util.List;

import jarden.quiz.QuestionAnswer;

import static jarden.quiz.PresetQuiz.OPENING_BIDS;

/**
 * Created by john.denny@gmail.com on 15/10/2018.
 */
public class ReviseQuizFragment extends FreakWizFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private CheckBox notesCheckBox;
    private ListView bidListView;
    private BidListAdapter bidListAdapter;
    private List<QuestionAnswer> qaList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        qaList = reviseItQuiz.getQuestionAnswerList();
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
        List<String> bidList = reviseItQuiz.getBidItems(bidSequence);
        QuestionAnswer nextQA;
        for (String bid: bidList) {
            nextQA = reviseItQuiz.findNextBidAnswer(bid);
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
                QuestionAnswer qa = reviseItQuiz.getBackBid(question);
                bidListAdapter.add(qa);
            }
        }
        List<QuestionAnswer> possibleResponses = reviseItQuiz.getPossibleResponses(targetQA);
        for (QuestionAnswer qa : possibleResponses) {
            bidListAdapter.add(qa);
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
}
