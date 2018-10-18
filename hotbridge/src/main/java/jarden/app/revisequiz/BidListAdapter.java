package jarden.app.revisequiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jardenconsulting.cardapp.R;

import jarden.quiz.QuestionAnswer;

/**
 * Created by john.denny@gmail.com on 18/10/2018.
 */
public class BidListAdapter extends ArrayAdapter<QuestionAnswer> {
    public BidListAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuestionAnswer qa = getItem(position);
        if (qa == null) {
            qa = new QuestionAnswer("null found", "position=" + position);
        }
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.bid_list_item, parent, false);
        }
        // Lookup view for data population
        TextView question = convertView.findViewById(R.id.question);
        TextView answer = convertView.findViewById(R.id.answer);
        // Populate the data into the template view using the data object
        question.setText(qa.question);
        answer.setText(qa.answer);
        // Return the completed view to render on screen
        return convertView;
    }

}
