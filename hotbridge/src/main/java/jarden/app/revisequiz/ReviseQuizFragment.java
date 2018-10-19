package jarden.app.revisequiz;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;

import com.jardenconsulting.cardapp.R;

import java.util.List;

import jarden.quiz.QuestionAnswer;

/**
 * Created by john.denny@gmail.com on 15/10/2018.
 */
public class ReviseQuizFragment extends FreakWizFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener /*!!, AdapterView.OnItemSelectedListener*/ {
    private final static char[] findChars = {',', ';'};
    //!! private final static String OPENING_BIDS = "Opening Bids";
    private final static QuestionAnswer OPENING_BIDS = new QuestionAnswer("Opening", "bids");

    private CheckBox notesCheckBox;
    private ListView bidListView;
    //!! private ArrayAdapter<String> bidListAdapter;
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
        /*!!
        this.bidListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
         */
        Object o = android.R.layout.simple_list_item_1;
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
        /*!!
        String selection = bidListAdapter.getItem(position);
        loadBidList(selection);
        */
        QuestionAnswer qa = bidListAdapter.getItem(position);
        loadBidList(qa);
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        //!! String selection = bidListAdapter.getItem(position);
        QuestionAnswer qa = bidListAdapter.getItem(position);
        if (qa != OPENING_BIDS) {
            this.questionTextView.setText(qa.question);
            this.answerTextView.setText(qa.answer);
            this.documentTextView.showPageText(qa.notes, "Notes");
        }
        /*!!
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
        */
        loadBidList(qa);
        return true;
    }
    @Override
    public void resetListView() {
        this.bidListAdapter.clear();
    }
    @Override
    public void setListView() {
        /* Notes for competitive bidding:
        if bid sequence contains '(':
            if first '(' not at beginning:
                add string before first '('
            while there remains a ',' or ';':
                get next bid in form [,;] (bid)[,;] bid
                add this bid
        else: as below!
         */
        String bidSequence = this.questionTextView.getText().toString();
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        int start = 0, findCharsIndex = 0, end;
        //!! String nextBid, nextBidAnswer;
        String nextBid;
        QuestionAnswer nextQA;
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
            /*!!
            nextBidAnswer = findNextBidAnswer(nextBid);
            bidListAdapter.add(nextBid + "=" + nextBidAnswer);
            */
            nextQA = findNextBidAnswer(nextBid);
            bidListAdapter.add(nextQA);
        } while (end != -1);
        this.bidListAdapter.notifyDataSetChanged();
    }
    private QuestionAnswer findNextBidAnswer(String nextBid) {
        for (QuestionAnswer qa: qaList) {
            if (qa.question.equals(nextBid)) return qa;
        }
        return null;
    }
    /*!!
    private String findNextBidAnswer(String nextBid) {
        for (QuestionAnswer qa: qaList) {
            if (qa.question.equals(nextBid)) return qa.answer;
        }
        return null;
    }
    */
    /*
    Get all responses to selected bid sequence
        BID_LIST for Q: 1C, 1H; 2D
            1C = ...
            1C, 1H = ...
            1C, 1H; 2D = ...
        click on 1C
            opening bids (replaces old "Back" button)
            1C, 1D = ...
            1C, 1H = ...
        click on 1C, 1H
            1C = ... ("Back")
            1C, 1H; 1S = ...
            1C, 1H; 1NT = ...

        BID_LIST for Q: 1C, (1S), double, (pass); 2NT
            1C = ...
            1C, (1S), double = ...
            1C, (1S), double, (pass); 2NT = ...
        click on 1C
            opening bids
            1C, 1D = ...
            1C, 1H = ...
        click on 1C, (1S), double
            1C = ...
            1C, (1S), double, (pass); 2NT

     */
    public void loadBidList(QuestionAnswer targetQA) {
        this.bidListAdapter.setNotifyOnChange(false);
        this.bidListAdapter.clear();
        if (targetQA == OPENING_BIDS) {
            for (QuestionAnswer qa: qaList) {
                if (!qa.question.contains(",")) {
                    bidListAdapter.add(qa);
                }
            }
        } else {
            String question = targetQA.question;
            int i = question.indexOf(',');
            if (i == -1) {
                bidListAdapter.add(OPENING_BIDS);
            } else {
                QuestionAnswer qa = getBackBid(question);
                bidListAdapter.add(qa);
            }
            for (QuestionAnswer qa : qaList) {
                if (qa.question.startsWith(question + ", ") ||
                        qa.question.startsWith(question + "; ")) {
                    String response = qa.question.substring(question.length() + 2);
                    if (!(response.contains(",") || response.contains(";"))) {
                        bidListAdapter.add(qa);
                    }
                }
            }
        }
        this.bidListAdapter.notifyDataSetChanged();
    }
    /*!!
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
    */
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
