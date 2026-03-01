package jarden.codswallop;

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
    private TextView promptTextView;
    private Constants.PlayerState playerState;

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
        promptTextView = rootView.findViewById(R.id.promptView);
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
                            allAnswers.isCorrect);

                    answersAdapter.notifyDataSetChanged();
                });
        gameViewModel.getPlayerStateLiveData()
                .observe(getViewLifecycleOwner(),
                        playerState -> {
                            this.playerState = playerState;
                            int promptId;
                            if (playerState == Constants.PlayerState.SUPPLY_VOTE) {
                                promptId = R.string.voteNow;
                            } else if (playerState == Constants.PlayerState.AWAITING_VOTES) {
                                promptId = R.string.waiting_for_more_votes;
                            } else if (playerState == Constants.PlayerState.AWAITING_NEXT_QUESTION) {
                                promptId = R.string.scores_wait_for_question;
                            } else {
                                promptId = R.string.play_on;
                            }
                            promptTextView.setText(promptId);
                        });
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onItemClick(position=" + position + ')');
        }
        if (playerState == Constants.PlayerState.SUPPLY_VOTE) {
            gameViewModel.setSelectedAnswer(position);
        } else {
            Toast.makeText(getContext(),
                    R.string.already_cast_vote,
                    Toast.LENGTH_LONG).show();
        }
    }
}
