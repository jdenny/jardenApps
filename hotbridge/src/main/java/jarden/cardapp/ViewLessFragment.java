package jarden.cardapp;


import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jardenconsulting.cardapp.BuildConfig;
import com.jardenconsulting.cardapp.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jarden.quiz.PresetQuiz;

/**
 * Created by john.denny@gmail.com on 7th June 2019.
 */
public class ViewLessFragment extends Fragment {
    private static final String TAG = "ViewlessFragment";
    private static final String quizFileName = "reviseit.txt";
    // "reviseitmini.txt"; // ***also change name of resource file***
    private PresetQuiz reviseItQuiz;


    @Override // Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    public PresetQuiz getReviseItQuiz() {
        if (reviseItQuiz == null) {
            try {
                File publicDirectory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
                File file = new File(publicDirectory, quizFileName);
                if (BuildConfig.DEBUG) Log.d(TAG, file.getAbsolutePath());
                InputStream inputStream;
                if (file.canRead()) {
                    inputStream = new FileInputStream(file);
                } else {
                    inputStream = getResources().openRawResource(R.raw.reviseit);
                }
                this.reviseItQuiz = new PresetQuiz(new InputStreamReader(inputStream));
            } catch (IOException ioe) {
                if (BuildConfig.DEBUG) Log.d(TAG, "exception loading data file: " + ioe);
                reviseItQuiz = null;
            }
        }
        return reviseItQuiz;
    }
}
