package jarden.codswallop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class QuestionViewModel extends ViewModel {
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>(new String(""));
    private final MutableLiveData<String> answerLiveData =
            new MutableLiveData<>(new String(""));

    public void setQuestionLiveData(String question) {
        questionLiveData.setValue(question);
    }
    public LiveData<String> getQuestionLiveData() {
        return questionLiveData;
    }
    public void setAnswerLiveData(String answer) {
        answerLiveData.setValue(answer);
    }
    public MutableLiveData<String> getAnswerLiveData() {
        return answerLiveData;
    }
}
