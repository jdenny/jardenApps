package jarden.codswallop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class QuestionViewModel extends ViewModel {
    private final MutableLiveData<String> questionLD =
            new MutableLiveData<>(new String(""));
    public void setAnswerState(String question) {
        questionLD.setValue(question);
    }
    public LiveData<String> getQuestionLD() {
        return questionLD;
    }
}
