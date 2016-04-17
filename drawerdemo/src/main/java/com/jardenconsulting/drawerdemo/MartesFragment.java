package com.jardenconsulting.drawerdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MartesFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "MartesFragment";
    private EditText editText;

    @Override // Fragment
    public void onAttach(Context context) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }
    @Override // Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate(" +
                (savedInstanceState == null ? "" : "not ") + "null)");
        setRetainInstance(true);
    }

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_martes, container, false);
        Button button = (Button) root.findViewById(R.id.clearTextButton);
        button.setOnClickListener(this);
        this.editText = (EditText) root.findViewById(R.id.editText);
        return root;
    }
    @Override // Fragment
    public void onResume() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
    }
    @Override // Fragment
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, "onPause()");
    }
    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        // assume it's the clearTextButton!
        this.editText.setText("");

    }
}
