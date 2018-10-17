package jarden.app.revisequiz;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jardenconsulting.cardapp.R;

import java.util.List;

import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

/**
 * Created by john.denny@gmail.com on 15/10/2018.
 */
public class ReviseQuizFragment extends FreakWizFragment
        implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private final static char[] findChars = {',', ';'};

    private TextView questionTextView;
    //!! private CheckBox notesCheckBox;
    private Spinner spinner;
    private TextView notesTextView;
    private ListView bidListView;
    private ArrayAdapter<String> bidListAdapter;
    private PresetQuiz reviseItQuiz;
    private List<QuestionAnswer> qaList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        this.questionTextView = rootView.findViewById(R.id.questionTextView);
        Context context = getContext();
        /*??
        ViewGroup selfMarkLayout = rootView.findViewById(R.id.selfMarkLayout);

        spinner = new Spinner(context);
        selfMarkLayout.addView(spinner, -1);
        */
        this.spinner = rootView.findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.spinnerValues,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        /*!!
        this.notesCheckBox = new CheckBox(getContext());
        notesCheckBox.setText(R.string.notes);
        selfMarkLayout.addView(notesCheckBox, -1);
        notesCheckBox.setId(R.id.notesCheckBox);
        notesCheckBox.setOnClickListener(this);
         */
        this.bidListAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        this.bidListView = rootView.findViewById(R.id.listView);
        this.bidListView.setAdapter(bidListAdapter);
        //?? this.bidListView.setOnItemClickListener(this);
        this.notesTextView = rootView.findViewById(R.id.notesTextView);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Quizable reviseActivity = (Quizable) getActivity();
        this.reviseItQuiz = reviseActivity.getReviseQuiz();
        qaList = reviseItQuiz.getQuestionAnswerList();
    }
    /*!!
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.notesCheckBox) {
            showNotesOrBids();
        } else super.onClick(view);
    }
    */
    @Override
    public void resetListView() {
        this.bidListView.setVisibility(View.GONE);
    }
    /*!!
    private void showNotesOrBids() {
        if (this.notesCheckBox.isChecked()) {
            this.bidListView.setVisibility(View.GONE);
            this.notesTextView.setVisibility(View.VISIBLE);
        } else {
            this.bidListView.setVisibility(View.VISIBLE);
            this.notesTextView.setVisibility(View.GONE);
        }
    }
    */
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    private String findNextBidAnswer(String nextBid) {
        for (QuestionAnswer qa: qaList) {
            if (qa.question.equals(nextBid)) return qa.answer;
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selection = spinner.getSelectedItem().toString();
        // Toast.makeText(getContext(), selection, Toast.LENGTH_LONG).show();
        if (position == 0) {
            this.bidListView.setVisibility(View.GONE);
            this.notesTextView.setVisibility(View.VISIBLE);
        } else {
            this.bidListView.setVisibility(View.VISIBLE);
            this.notesTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
