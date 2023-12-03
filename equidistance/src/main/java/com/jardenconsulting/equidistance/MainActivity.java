package com.jardenconsulting.equidistance;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import jarden.equidistance.Group;
import jarden.equidistance.Person;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "Equidistance";

    private CanvasView canvasView;
    private Person[] people;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        people = new Person[] {
                new Person("1"),
                new Person("2"),
                new Person("3"),
                new Person("4")
        };
        Group group = new Group(people, 10, 10);
        for (Person bod: people) {
            bod.setGroup(group);
        }
        setContentView(R.layout.activity_main);
        this.canvasView = findViewById(R.id.canvasView);
        canvasView.setPeople(people);
        Button button = findViewById(R.id.moveButton);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        for (Person bod : people) {
            bod.moveIfNecessary();
            if (BuildConfig.DEBUG) {
                Log.i(TAG, bod.toString());
            }
        }
        if (BuildConfig.DEBUG) {
            Log.i(TAG, "");
        }
        canvasView.invalidate();
    }
}