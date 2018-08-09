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
        this.backButton = rootView.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);
        Button quizButton = rootView.findViewById(R.id.quizButton);
        quizButton.setOnClickListener(this);
        this.notesCheckBox = rootView.findViewById(R.id.notesCheckBox);
        this.notesCheckBox.setOnClickListener(this);
        this.bidListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        this.bidListView = rootView.findViewById(R.id.bidListView);
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
        showAnswer(qa);
        loadBidList(qa.question);
    }
    private void showNotesOrBids() {
        if (this.notesCheckBox.isChecked()) {
            this.bidListView.setVisibility(View.INVISIBLE);
            this.notesTextView.setVisibility(View.VISIBLE);
        } else {
            this.bidListView.setVisibility(View.VISIBLE);
            this.notesTextView.setVisibility(View.INVISIBLE);
        }
    }
    private void showAnswer(QuestionAnswer qa) {
        this.answerTextView.setText(qa.answer);
        this.documentTextView.showPageText(qa.notes, "Notes");
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
        this.backButton.setEnabled(bid != null);
        this.notesCheckBox.setEnabled(bid != null);
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        for (QuestionAnswer qa: qaList) {
            String question = qa.question;
            if (isResponseToBid(bid, question)) {
                bidListAdapter.add(question);
                currentQAList.add(qa);
            }
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
    private boolean isResponseToBid(String bid, String question) {
        if (bid == null) { // getting only opening bids:
            return !question.contains(",");
        } else {
            if (question.startsWith(bid + ", ") || question.startsWith(bid + "; ")) {
                int pos = bid.length() + 2;
                return !(question.substring(pos).contains(",") ||
                        question.substring(pos).contains(";"));
            } else return false;
        }
    }
}
