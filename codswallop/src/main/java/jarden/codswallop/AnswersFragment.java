package jarden.codswallop;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
    private AnswersAdapter answersAdapter;
    private GameViewModel gameViewModel;
    private Constants.PlayerState playerState;
    private GameServiceProvider serviceProvider;

    @Override // Fragment
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        serviceProvider = (GameServiceProvider) context;
    }
    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        ListView answersListView = rootView.findViewById(R.id.answersListView);
        this.answersAdapter = new AnswersAdapter(getActivity());
        answersListView.setAdapter(answersAdapter);
        answersListView.setOnItemClickListener(this);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
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
        gameViewModel.getAnswersLiveData().observe(
                getViewLifecycleOwner(),
                allAnswers -> {
                    questionView.setText(allAnswers.question);
                    answersAdapter.setNotifyOnChange(false);
                    answersAdapter.clear();
                    answersAdapter.addAll(allAnswers.answers);
                    answersAdapter.setResultsState(
                            allAnswers.named,
                            allAnswers.isCorrect,
                            allAnswers.linesVotedForMe);

                    answersAdapter.notifyDataSetChanged();
                });
        gameViewModel.getPlayerStateLiveData()
                .observe(getViewLifecycleOwner(),
                        playerState -> {
                            this.playerState = playerState;
                        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onItemClick(position=" + position + ')');
        }
        if (playerState == Constants.PlayerState.SUPPLY_VOTE) {
            serviceProvider.submitVote(position);
        } else {
            Toast.makeText(getContext(),
                    R.string.already_cast_vote,
                    Toast.LENGTH_LONG).show();
        }
    }
}
