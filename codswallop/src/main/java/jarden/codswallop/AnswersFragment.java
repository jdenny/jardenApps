package jarden.codswallop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment implements AdapterView.OnItemClickListener {
    private static final String TAG = "AnswersFragment";
    private TextView questionView;
    private ArrayAdapter<String> answersAdapter;
    private AnswersViewModel answersViewModel;
    private boolean voteCast;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        ListView answersListView = rootView.findViewById(R.id.answersListView);
        this.answersAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        answersListView.setAdapter(answersAdapter);
        answersListView.setOnItemClickListener(this);
        answersViewModel = new ViewModelProvider(requireActivity()).get(AnswersViewModel.class);
        return rootView;
    }

    public AnswersFragment() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "AnswersFragment()");
        }
    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume()");
        }
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        answersViewModel.getAnswersLiveData().observe(
                getViewLifecycleOwner(),
                answersState -> {
                    questionView.setText(answersState.question);
                    List<String> answers = answersState.answers;
                    answersAdapter.setNotifyOnChange(false);
                    answersAdapter.clear();
                    for (String answer : answers) {
                        answersAdapter.add(answer);
                    }
                    answersAdapter.notifyDataSetChanged();
                });
        voteCast = false;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onItemClick(position=" + position + ')');
        }
        if (!voteCast) {
            answersViewModel.setSelectedAnswerLiveData(position);
            voteCast = true;
        } else {
            Toast.makeText(getContext(),
                    "You have already cast your vote; you can't change your mind!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
