package com.jardenconsulting.androidcourse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class DynamicFormActivity extends AppCompatActivity
		implements OnClickListener {
	private final static String TAG = "DynamicFormActivity";
	private String[] fieldNames;
	private EditText[] fieldEditTexts;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_form);
		TableLayout rootTableLayout = (TableLayout) findViewById(R.id.rootLayout);
		this.fieldNames = getIntent().getStringArrayExtra("fieldNames");
		if (fieldNames == null || fieldNames.length == 0) {
			Log.e(TAG, "invalid fieldNames in Intent");
			return;
		}
		this.fieldEditTexts = new EditText[fieldNames.length];
		for (int i = 0; i < this.fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			TableRow tableRow = new TableRow(this);
			rootTableLayout.addView(tableRow);
			TextView nameTextView = new TextView(this);
			nameTextView.setText(fieldName);
			if (i == 0) {
				nameTextView.setPadding(0, 0, 40, 0);
			}
			tableRow.addView(nameTextView);
			EditText fieldEditText = new EditText(this);
			fieldEditText.setHint("supply value for " + fieldName);
			tableRow.addView(fieldEditText);
			this.fieldEditTexts[i] = fieldEditText;
		}
		// now add button!
		TableRow tableRow = new TableRow(this);
		rootTableLayout.addView(tableRow);
		Button button = new Button(this);
		button.setText(R.string.go);
		button.setOnClickListener(this);
		tableRow.addView(button);
		TableRow.LayoutParams params = (TableRow.LayoutParams)button.getLayoutParams();
		params.span = 2;
		button.setLayoutParams(params);
	}

	@Override
	public void onClick(View view) {
		for (int i = 0; i < this.fieldNames.length; i++) {
			Log.i(TAG, fieldNames[i] + " = " + fieldEditTexts[i].getText().toString());
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dynamic_form, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
