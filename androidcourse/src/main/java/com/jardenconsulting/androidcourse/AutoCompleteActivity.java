package com.jardenconsulting.androidcourse;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

public class AutoCompleteActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_complete);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.countryAuto);
        String[] countries = getResources().getStringArray(R.array.countries);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, countries);
        textView.setAdapter(adapter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.auto_complete, menu);
		return true;
	}

}
