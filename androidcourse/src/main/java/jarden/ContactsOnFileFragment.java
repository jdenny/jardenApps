package jarden;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import com.jardenconsulting.androidcourse.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ContactsOnFileFragment extends Fragment implements OnClickListener {
	private final static String CONTACTS_FILE_NAME = "contacts.txt";
	private TableLayout contactTable;
	private Button newContactButton;
	private Activity activity;
	private EditText nameEdit;
	private EditText numberEdit;
	private HashMap<String, String> contactMap;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.contacts_fragment, container, false);
		activity = this.getActivity();
		nameEdit = (EditText) view.findViewById(R.id.contactName);
		numberEdit = (EditText) view.findViewById(R.id.contactNumber);
		contactTable = (TableLayout) view.findViewById(R.id.contactTable);
		newContactButton = (Button) view.findViewById(R.id.newContactButton);
		newContactButton.setOnClickListener(this);
		showContacts();
		return view;
	}
	private void showContacts() {
		if (contactMap == null) {
			contactMap = new HashMap<String, String>();
			try {
				FileInputStream fis = activity.openFileInput(CONTACTS_FILE_NAME);
				InputStreamReader isr = new InputStreamReader(fis);
				BufferedReader reader = new BufferedReader(isr);
				String line;
				while ((line=reader.readLine()) != null) {
					String[] fields = line.split(":");
					contactMap.put(fields[0], fields[1]);
				}
			} catch (IOException e) {
				throw new RuntimeException("error reading contacts file: ", e);
			}
		}
		TextView textView;
		TableRow tableRow;
		contactTable.removeAllViews();
		Set<String> keySet = contactMap.keySet();
		for (String contactName : keySet) {
			String contactNumber = contactMap.get(contactName);
			tableRow = new TableRow(activity);
			contactTable.addView(tableRow);
			textView = new TextView(activity);
			textView.setText(contactName);
			tableRow.addView(textView);
			textView = new TextView(activity);
			textView.setText(contactNumber);
			tableRow.addView(textView);
		}
	}
	@Override
	public void onClick(View view) {
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		String contactName = nameEdit.getText().toString();
		String contactNumber = numberEdit.getText().toString();
		editor.putString(contactName, contactNumber);
		editor.commit();
		showContacts();
	}
}
