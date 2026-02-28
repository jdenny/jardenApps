package jarden.codswallop;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

/**
 * Created by john.denny@gmail.com on 28/02/2026.
 */
public class AnswersAdapter extends ArrayAdapter<String> {
    private int indexIVotedFor = -1;
    private boolean showResults = false;

    public AnswersAdapter(Context context) {
        super(context, 0);
    }
    public void setResultsState(boolean showResults, int indexIVotedFor) {
        this.showResults = showResults;
        this.indexIVotedFor = indexIVotedFor;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext())
                    .inflate(R.layout.answer_row, parent, false);
        }
        TextView text = view.findViewById(R.id.answerText);
        text.setText(getItem(position));
        // 🚨 ALWAYS RESET (ListView recycles views!)
        view.setBackgroundColor(Color.TRANSPARENT);
        if (showResults) {
            // Correct answer is always first
            if (position == 0) {
                view.setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.correct_green));
            }
            // If I voted wrongly
            else if (position == indexIVotedFor) {
                view.setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.wrong_red));
            }
        }
        return view;
    }
}
