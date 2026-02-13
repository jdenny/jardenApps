package jarden.codswallop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class AnswersViewModel extends ViewModel {
    private final MutableLiveData<AnswersState> answersLiveData =
            new MutableLiveData<>(new AnswersState(null, null));
    private final MutableLiveData<Integer> selectedAnswerLiveData =
            new MutableLiveData<>(null);
    public void setAnswersLiveData(AnswersState newAnswersState) {
        answersLiveData.setValue(newAnswersState);
    }
    public LiveData<AnswersState> getAnswersLiveData() {
        return answersLiveData;
    }
    public void setSelectedAnswerLiveData(Integer position) {
        selectedAnswerLiveData.setValue(position);
    }
    public LiveData<Integer> getSelectedAnswerLiveData() {
        return selectedAnswerLiveData;
    }
}

