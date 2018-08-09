package jarden.app.revisequiz;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jardenconsulting.jardenlib.BuildConfig;
import com.jardenconsulting.jardenlib.R;

import jarden.document.DocumentTextView;
import jarden.quiz.EndOfQuestionsException;
import jarden.quiz.PresetQuiz;
import jarden.quiz.QuestionAnswer;

import static jarden.quiz.PresetQuiz.QuizMode.PRACTICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FreakWizFragment extends Fragment implements View.OnClickListener {
    public interface Quizable {
        PresetQuiz getReviseQuiz();
        int[] getNotesResIds();
    }
    private static final String TAG = "FreakWizFragment";

    private PresetQuiz reviseItQuiz;
    private TextView questionTextView;
    private TextView answerTextView;
    private TextView statsTextView;
    private TextView notesTextView;
    private Button goButton;
    private ViewGroup selfMarkLayout;
    private DocumentTextView documentTextView;

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
        askQuestion();
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
    public void askQuestion() {
        String question;
        try {
            question = reviseItQuiz.getNextQuestion(1);
        } catch (EndOfQuestionsException e) {
            showMessage("end of questions! starting practice mode");
            setPracticeMode();
            try {
                question = reviseItQuiz.getNextQuestion(1);
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
    }
    private void selfMarkButton(boolean isCorrect) {
        this.reviseItQuiz.setCorrect(isCorrect);
        showButtonLayout();
        askQuestion();
    }

    private void goPressed() {
        QuestionAnswer currentQA = reviseItQuiz.getCurrentQuestionAnswer();
        showAnswer(currentQA);
        showSelfMarkLayout();
    }
    private void showAnswer(QuestionAnswer qa) {
        this.answerTextView.setText(qa.answer);
        this.documentTextView.showPageText(qa.notes, "Notes");
    }
    private void setPracticeMode() {
        reviseItQuiz.setQuizMode(PRACTICE);
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
        String stats = "Current=" +
                reviseItQuiz.getCurrentQAIndex() + "/" +
                reviseItQuiz.getCurrentCount() +
                " Fails=" +
                reviseItQuiz.getFailedCount();
        this.statsTextView.setText(stats);
    }
}
