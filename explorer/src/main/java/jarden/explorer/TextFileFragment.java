package jarden.explorer;

import java.io.File;

import com.jardenconsulting.explorer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TextFileFragment extends Fragment {

	private TextView textView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_text,
				container, false);
		this.textView = (TextView) rootView.findViewById(R.id.textView);
		return rootView;
	}

	public void setFile(File file) {
		String filePath = file.getAbsolutePath();
		this.textView.setText(filePath);
		// TODO read contents of file
	}
}

