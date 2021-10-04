package jarden;

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

public class ContactsFragment extends Fragment implements OnClickListener {
	private TableLayout contactTable;
	private Button newContactButton;
	private Activity activity;
	private EditText nameEdit;
	private EditText numberEdit;

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
		SharedPreferences prefs = activity.getPreferences(Activity.MODE_PRIVATE);
		Set<String> contactNameSet = prefs.getAll().keySet();

		TextView textView;
		TableRow tableRow;
		contactTable.removeAllViews();
		for (String contactName : contactNameSet) {
			String contactNumber = prefs.getString(contactName, "unknown");
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
