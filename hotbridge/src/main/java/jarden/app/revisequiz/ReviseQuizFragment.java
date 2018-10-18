package jarden.app.revisequiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.jardenconsulting.cardapp.R;

import java.util.List;

import jarden.quiz.QuestionAnswer;

/**
 * Created by john.denny@gmail.com on 15/10/2018.
 */
public class ReviseQuizFragment extends FreakWizFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener /*!!, AdapterView.OnItemSelectedListener*/ {
    private final static char[] findChars = {',', ';'};
    private final static String OPENING_BIDS = "Opening Bids";

    private CheckBox notesCheckBox;
    private ListView bidListView;
    private ArrayAdapter<String> bidListAdapter;
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
        //!! this.notesCheckBox = rootView.findViewById(R.id.notesCheckBox);

        notesCheckBox.setOnClickListener(this);
        this.bidListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
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
        String selection = bidListAdapter.getItem(position);
        loadBidList(selection);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String selection = bidListAdapter.getItem(position);
        Toast.makeText(getContext(), selection, Toast.LENGTH_LONG).show();
        int index = selection.indexOf('=');
        if (index != -1) {
            String bid = selection.substring(0, index);
            for (QuestionAnswer qa: qaList) {
                if (qa.question.equals(bid)) {
                    this.questionTextView.setText(qa.question);
                    this.answerTextView.setText(qa.answer);
                    this.documentTextView.showPageText(qa.notes, "Notes");
                    break;
                }
            }
        }
        loadBidList(selection);
        return true;
    }
    @Override
    public void resetListView() {
        this.bidListAdapter.clear();
    }
    @Override
    public void setListView() {
        String bidSequence = this.questionTextView.getText().toString();
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        int start = 0, findCharsIndex = 0, end;
        String nextBid, nextBidAnswer;
        char findChar;
        do {
            findChar = findChars[findCharsIndex]; // i.e. ',' or ';'
            end = bidSequence.indexOf(findChar, start);
            if (end == -1) {
                nextBid = bidSequence;
            } else {
                nextBid = bidSequence.substring(0, end);
                start = end + 2;
                findCharsIndex = ++findCharsIndex % 2;
            }
            nextBidAnswer = findNextBidAnswer(nextBid);
            bidListAdapter.add(nextBid + "=" + nextBidAnswer);
        } while (end != -1);
        this.bidListAdapter.notifyDataSetChanged();
    }
    private String findNextBidAnswer(String nextBid) {
        for (QuestionAnswer qa: qaList) {
            if (qa.question.equals(nextBid)) return qa.answer;
        }
        return null;
    }
    /*
    Get all responses to supplied bid.
        e.g. Q: 1C, 1H; 2D

        BID_LIST
            1C = ...
            1C, 1H = ...
            1C, 1H; 2D = ...
        click on 1C, 1H
            1C = ... (i.e. back/up)
            1C, 1H; 1S = ...
            1C, 1H; 1NT = ...
        click on 1C
            opening bids
            1C, 1D = ...
            1C, 1H = ...
     */
    public void loadBidList(String selection) {
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        if (selection.equals(OPENING_BIDS)) {
            for (QuestionAnswer qa: qaList) {
                if (!qa.question.contains(",")) {
                    bidListAdapter.add(qa.question + "=" + qa.answer);
                }
            }
        } else {
            int index = selection.indexOf('=');
            String bid = selection.substring(0, index);
            int i = bid.indexOf(',');
            if (i == -1) {
                bidListAdapter.add(OPENING_BIDS);
            } else {
                QuestionAnswer qa = getBackBid(bid);
                bidListAdapter.add(qa.question + "=" + qa.answer);
            }
            for (QuestionAnswer qa : qaList) {
                if (qa.question.startsWith(bid + ", ") || qa.question.startsWith(bid + "; ")) {
                    String response = qa.question.substring(bid.length() + 2);
                    if (!(response.contains(",") || response.contains(";"))) {
                        bidListAdapter.add(qa.question + "=" + qa.answer);
                    }
                }
            }
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
    private QuestionAnswer getBackBid(String bid) {
        int lastComma = bid.lastIndexOf(',');
        int lastColon = bid.lastIndexOf(';');
        int lastSeparator = (lastComma > lastColon) ? lastComma : lastColon;
        if (lastSeparator == -1) return null;
        String backQuestion = bid.substring(0, lastSeparator);
        for (QuestionAnswer qa: this.qaList) {
            if (backQuestion.equals(qa.question)) return qa;
        }
        return new QuestionAnswer(bid, "no backBid found!");
    }
}
