package jarden.app.revisequiz;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.jardenlib.BuildConfig;
import com.jardenconsulting.jardenlib.R;

import java.util.Set;

import jarden.app.dialog.IntegerDialog;
import jarden.document.DocumentTextView;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

public class FreakWizFragment extends Fragment implements View.OnClickListener,
        IntegerDialog.IntValueListener {
    public interface Quizable {
        PresetQuiz getReviseQuiz();
        int[] getNotesResIds();
    }
    private static final String QUESTION_INDEX_KEY = "questionIndexKey";
    private static final String TARGET_CORRECTS_KEY = "targetCorrectsKey";
    private static final String FAIL_INDICES_KEY = "failIndicesKey";
    private static final String LEARN_MODE_KEY = "learnModeKey";

    protected DocumentTextView documentTextView;
    protected TextView questionTextView;
    protected TextView answerTextView;
    protected TextView notesTextView;
    protected PresetQuiz reviseQuiz;
    protected ViewGroup selfMarkLayout;

    private static final String TAG = "FreakWizFragment";

    private TextView statsTextView;
    private Button goButton;
    private boolean changingQuestionIndex;
    private IntegerDialog integerDialog;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_freak_wiz, container, false);
        this.questionTextView = rootView.findViewById(R.id.questionTextView);
        this.answerTextView = rootView.findViewById(R.id.answerTextView);
        this.statsTextView = rootView.findViewById(R.id.statsTextView);
        this.notesTextView = rootView.findViewById(R.id.notesTextView);
        this.notesTextView.setMovementMethod(LinkMovementMethod.getInstance());
        this.notesTextView.setHighlightColor(Color.TRANSPARENT);
        this.goButton = rootView.findViewById(R.id.goButton);
        this.goButton.setOnClickListener(this);
        Button correctButton = rootView.findViewById(R.id.correctButton);
        correctButton.setOnClickListener(this);
        Button incorrectButton = rootView.findViewById(R.id.incorrectButton);
        incorrectButton.setOnClickListener(this);
        this.selfMarkLayout =  rootView.findViewById(R.id.selfMarkLayout);
        this.selfMarkLayout.setVisibility(View.GONE);
        setHasOptionsMenu(true);

        Quizable reviseActivity = (Quizable) getActivity();
        this.reviseQuiz = reviseActivity.getReviseQuiz();
        this.documentTextView = new DocumentTextView(
                getActivity().getApplicationContext(),
                notesTextView, reviseActivity.getNotesResIds(),
                true, null, false);

        this.sharedPreferences = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        boolean learnMode = sharedPreferences.getBoolean(LEARN_MODE_KEY, true);
        int savedQuestionIndex = sharedPreferences.getInt(QUESTION_INDEX_KEY, -1);
        if (savedQuestionIndex > 0) {
            reviseQuiz.setQuestionIndex(savedQuestionIndex);
        }
        int savedTargetsCorrect = sharedPreferences.getInt(TARGET_CORRECTS_KEY, -1);
        if (savedTargetsCorrect > 0) {
            reviseQuiz.setTargetCorrectCt(savedTargetsCorrect);
        }
        String failIndexStr = sharedPreferences.getString(FAIL_INDICES_KEY, "");
        if (failIndexStr.length() > 0) {
            String[] failIndices = failIndexStr.split(",");
            reviseQuiz.setFailIndices(failIndices);
        }
        setLearnMode(learnMode);

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        askQuestion();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.revise, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.learnModeButton) {
            setLearnMode(true);
            askQuestion();
        } else if (id == R.id.practiceModeButton) {
            setLearnMode(false);
            askQuestion();
        } else if (id == R.id.setCurrentIndexButton) {
            // if we add more ints for the user to update, then
            // change this boolean to an enum
            changingQuestionIndex = true;
            showIntegerDialog("Change Current Index",
                    reviseQuiz.getQuestionIndex() + 1,
                    "UserLevelDialog");
        } else if (id == R.id.setTargetCorrectsButton) {
            changingQuestionIndex = false;
            showIntegerDialog("Change Target Correct",
                    reviseQuiz.getTargetCorrectCt(),
                    "TargetCorrectsDialog");
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
    private void setLearnMode(boolean learnMode) {
        reviseQuiz.setLearnMode(learnMode);
        getActivity().setTitle(getQuizTitleId());
    }
    public int getQuizTitleId() {
        return reviseQuiz.isLearnMode() ? R.string.learnMode : R.string.practiceMode;
    }
    private void showIntegerDialog(String title, int value, String tag) {
        if (integerDialog == null) {
            integerDialog = new IntegerDialog();
            integerDialog.setIntValueListener(this);
        }
        this.integerDialog.setTitle(title);
        this.integerDialog.setIntValue(value);
        this.integerDialog.show(getActivity().getSupportFragmentManager(), tag);
    }
    @Override // IntValueListener
    public void onUpdateIntValue(int intValue) {
        if (changingQuestionIndex) {
            // to people, ordinals start from 1; to computers, they start from 0
            this.reviseQuiz.setQuestionIndex(intValue - 1);
            askQuestion();
        } else {
            this.reviseQuiz.setTargetCorrectCt(intValue);
        }
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.goButton) {
            goPressed();
        } else if (id == R.id.correctButton) {
            selfMarkButton(true);
        } else if (id == R.id.incorrectButton) {
            selfMarkButton(false);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "FreakWizFragment.onPause()");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int questionIndex = reviseQuiz.getQuestionIndex();
        editor.putInt(QUESTION_INDEX_KEY, questionIndex);
        int targetCorrectCt = reviseQuiz.getTargetCorrectCt();
        editor.putInt(TARGET_CORRECTS_KEY, targetCorrectCt);
        editor.putBoolean(LEARN_MODE_KEY, reviseQuiz.isLearnMode());
        Set<Integer> failedIndexSet = reviseQuiz.getFailedIndexSet();
        String failStr;
        if (failedIndexSet.size() == 0) {
            failStr = "";
        } else {
            StringBuilder sBuilder = new StringBuilder();
            for (int failIndex : failedIndexSet) {
                sBuilder.append(failIndex).append(",");
            }
            failStr = sBuilder.substring(0, sBuilder.length() - 1);
        }
        editor.putString(FAIL_INDICES_KEY, failStr);
        editor.apply();
    }
    public void askQuestion() {
        String question;
        try {
            question = reviseQuiz.getNextQuestion(1);
        } catch (EndOfQuestionsException e) {
            showMessage("end of questions! starting practice mode");
            setPracticeMode();
            try {
                question = reviseQuiz.getNextQuestion(1);
            } catch (EndOfQuestionsException e1) {
                showMessage("exception: " + e);
                return;
            }
        }
        this.questionTextView.setText(question);
        this.answerTextView.setText("");
        this.notesTextView.setText("");
        showButtonLayout();
        showStats();
        resetListView();
    }

    /**
     * Override these two methods subclasses if necessary
     */
    public void setListView() {
    }
    public void resetListView() {
    }
    private void selfMarkButton(boolean isCorrect) {
        this.reviseQuiz.setCorrect(isCorrect);
        showButtonLayout();
        askQuestion();
    }
    private void goPressed() {
        QuestionAnswer currentQA = reviseQuiz.getCurrentQuestionAnswer();
        showAnswer(currentQA);
        showSelfMarkLayout();
        setListView();
    }
    private void showAnswer(QuestionAnswer qa) {
        this.answerTextView.setText(qa.answer);
        this.documentTextView.showPageText(qa.notes, "Notes");
    }
    private void setPracticeMode() {
        //!! reviseQuiz.setQuizMode(PRACTICE);
        reviseQuiz.setLearnMode(false);
        getActivity().setTitle(R.string.practiceMode);
    }
    private void showSelfMarkLayout() {
        this.goButton.setVisibility(View.GONE);
        this.selfMarkLayout.setVisibility(View.VISIBLE);
    }
    private void showButtonLayout() {
        this.selfMarkLayout.setVisibility(View.GONE);
        this.goButton.setVisibility(View.VISIBLE);
    }
    private void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
    private void showStats() {
        // humans count from 1, machines from 0
        int humanIndex = reviseQuiz.getCurrentQAIndex() + 1;
        String stats = "Current=" + humanIndex;
        if (reviseQuiz.isLearnMode()) {
            stats += ", ToDo=" + reviseQuiz.getToDoCount();
        }
        stats += ", Fails=" + reviseQuiz.getFailedCount();
        this.statsTextView.setText(stats);
    }
}
