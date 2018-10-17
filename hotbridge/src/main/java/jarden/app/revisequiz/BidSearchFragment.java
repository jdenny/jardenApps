package jarden.app.revisequiz;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.jardenconsulting.cardapp.R;
import com.jardenconsulting.cardapp.ReviseQuizActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import jarden.document.DocumentTextView;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

import static jarden.app.revisequiz.FreakWizFragment.Quizable;

/**
 * A simple {@link Fragment} subclass.
 */
public class BidSearchFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {
    private PresetQuiz reviseItQuiz;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView notesTextView;
    private Button backButton;
    private CheckBox notesCheckBox;
    private ListView bidListView;
    private DocumentTextView documentTextView;
    private ArrayAdapter<String> bidListAdapter;
    private Stack<QuestionAnswer> bidSearchStack;
    private List<QuestionAnswer> qaList;
    private List<QuestionAnswer> currentQAList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_bid_search, container, false);
        this.questionTextView = rootView.findViewById(R.id.questionTextView);
        this.answerTextView = rootView.findViewById(R.id.answerTextView);
        this.notesTextView = rootView.findViewById(R.id.notesTextView);
        this.notesTextView.setMovementMethod(LinkMovementMethod.getInstance());
        this.notesTextView.setHighlightColor(Color.TRANSPARENT);
        this.notesTextView.setVisibility(View.INVISIBLE);
        this.backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        Button quizButton = rootView.findViewById(R.id.quizButton);
        quizButton.setOnClickListener(this);
        this.notesCheckBox = rootView.findViewById(R.id.notesCheckBox);
        this.notesCheckBox.setOnClickListener(this);
        this.bidListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        this.bidListView = rootView.findViewById(R.id.listView);
        this.bidListView.setAdapter(bidListAdapter);
        this.bidListView.setOnItemClickListener(this);
        this.currentQAList = new ArrayList<>();
        this.bidSearchStack = new Stack<>();
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Quizable reviseActivity = (Quizable) getActivity();
        this.reviseItQuiz = reviseActivity.getReviseQuiz();
        this.documentTextView = new DocumentTextView(
                getActivity().getApplicationContext(),
                notesTextView, reviseActivity.getNotesResIds(),
                true, null, false);
        qaList = reviseItQuiz.getQuestionAnswerList();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.quizButton) {
            ReviseQuizActivity rqActivity = (ReviseQuizActivity) getActivity();
            rqActivity.backToQuiz();
        } else if (id == R.id.backButton) {
            bidSearchStack.pop();
            if (bidSearchStack.empty()) loadBidList(null);
            else showBidAndResponses(bidSearchStack.peek());
        } else if (id == R.id.notesCheckBox) {
            showNotesOrBids();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(ReviseQuizActivity.TAG, "position=" + position);
        QuestionAnswer qa = this.currentQAList.get(position);
        this.bidSearchStack.add(qa);
        showBidAndResponses(qa);
    }
    private void showBidAndResponses(QuestionAnswer qa) {
        this.questionTextView.setText(qa.question);
        this.answerTextView.setText(qa.answer);
        this.documentTextView.showPageText(qa.notes, "Notes");
        loadBidList(qa.question);
    }
    private void showNotesOrBids() {
        if (this.notesCheckBox.isChecked()) {
            this.bidListView.setVisibility(View.GONE);
            this.notesTextView.setVisibility(View.VISIBLE);
        } else {
            this.bidListView.setVisibility(View.VISIBLE);
            this.notesTextView.setVisibility(View.GONE);
        }
    }
    // TODO: use this instead of bidSearchStack
    private QuestionAnswer getBackBid(String bid) {
        int lastComma = bid.lastIndexOf(',');
        int lastColon = bid.lastIndexOf(';');
        int lastSeparator = (lastComma > lastColon) ? lastComma : lastColon;
        if (lastSeparator == -1) return null;
        String backQuestion = bid.substring(0, lastSeparator);
        for (QuestionAnswer qa: this.qaList) {
            if (backQuestion.equals(qa.question)) return qa;
        }
        throw new IllegalArgumentException("no backBid for bid " + bid);
    }
    /*
    Get all responses to supplied bid. If bid is null, get all opening bids.
     */
    public void loadBidList(String bid) {
        if (this.qaList == null) {
            qaList = reviseItQuiz.getQuestionAnswerList();
            this.currentQAList = new ArrayList<>();
            this.bidSearchStack = new Stack<>();
        } else {
            this.currentQAList.clear();
        }
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        if (bid == null) {
            this.backButton.setEnabled(false);
            this.notesCheckBox.setEnabled(false);
            this.questionTextView.setText("");
            this.answerTextView.setText("");
            this.notesTextView.setText("");
        } else {
            this.backButton.setEnabled(true);
            this.notesCheckBox.setEnabled(true);
        }
        String response;
        for (QuestionAnswer qa: qaList) {
            String question = qa.question;
            if ((response = getResponseToBid(bid, question)) != null) {
                bidListAdapter.add(response + "=" + qa.answer);
                currentQAList.add(qa);
            }
        }
        this.bidListAdapter.notifyDataSetChanged();
        this.notesCheckBox.setChecked(false);
        showNotesOrBids();
    }
    private String getResponseToBid(String bid, String question) {
        if (bid == null) { // getting only opening bids:
            if (question.contains(",")) return null;
            else return question;
        } else {
            if (question.startsWith(bid + ", ") || question.startsWith(bid + "; ")) {
                String response = question.substring(bid.length() + 2);
                if (response.contains(",") || response.contains(";")) {
                    return null;
                } else return response;
            } else return null;
        }
    }
}
