package jarden.codswallop;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class AnswersViewModel extends ViewModel {
    private final MutableLiveData<AnswersState> answersState =
            new MutableLiveData<>(new AnswersState(null, null));
    public void setAnswersState(AnswersState newAnswersState) {
        answersState.setValue(newAnswersState);
    }
    public LiveData<AnswersState> getAnswersState() {
        return answersState;
    }
}

