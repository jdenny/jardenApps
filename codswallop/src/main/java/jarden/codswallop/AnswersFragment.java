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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment {
    private static final String TAG = "AnswersFragment";
    private TextView questionView;
    private ListView answersListView;
    private ArrayAdapter<String> answersAdapter;
    private AdapterView.OnItemClickListener savedListener = null;
    private boolean savedShowPlayerNames;
    private AnswersViewModel viewModel;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        answersListView = rootView.findViewById(R.id.answersListView);
        this.answersAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        answersListView.setAdapter(answersAdapter);
        viewModel = new ViewModelProvider(requireActivity()).get(AnswersViewModel.class);
        return rootView;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            answersListView.setOnItemClickListener(listener);
        } else {
            savedListener = listener;
        }
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
        if (savedListener != null) {
            setOnItemClickListener(savedListener);
            savedListener = null;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.getAnswersState().observe(
                getViewLifecycleOwner(),
                answersState -> {
                    try {
                        AdapterView.OnItemClickListener onItemClickListener = (AdapterView.OnItemClickListener) getContext();
                        setOnItemClickListener(onItemClickListener);
                    } catch (ClassCastException cce) {
                        Log.e(TAG, cce.toString());
                    }
                    questionView.setText(answersState.question);
                    List<String> answers = answersState.answers;
                    answersAdapter.setNotifyOnChange(false);
                    answersAdapter.clear();
                    for (String answer : answers) {
                        answersAdapter.add(answer);
                    }
                    answersAdapter.notifyDataSetChanged();
                });
    }
}
