package jarden;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jardenconsulting.androidcourse.BuildConfig;
import com.jardenconsulting.androidcourse.LayoutsActivity;
import com.jardenconsulting.androidcourse.R;

public class LayoutsFragment extends Fragment implements OnClickListener {
	private final static String TAG = "LayoutsFragment";
	private int currentLayoutId = R.layout.layouts_table;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "currentLayoutId=" + this.currentLayoutId);
		}
		View rootView = inflater.inflate(this.currentLayoutId,
				container, false);
		String type, file;
		if (currentLayoutId == R.layout.layouts_table) {
			type = "TableLayout";
			file = "layouts_table.xml";
		} else if (currentLayoutId == R.layout.layouts_linear) {
			type = "LinearLayout";
			file = "layouts_linear.xml";
		} else if (currentLayoutId == R.layout.layouts_relative) {
			type = "RelativeLayout";
			file = "layouts_relative.xml";
		} else {
			Log.w(TAG, "unrecognised value of currentLayoutId: " + this.currentLayoutId);
			type = "unknown";
			file = "unknown";
		}
		EditText editText = (EditText) rootView.findViewById(R.id.layoutType);
		editText.setText(type);
		editText = (EditText) rootView.findViewById(R.id.layoutFile);
		editText.setText(file);
		editText = (EditText) rootView.findViewById(R.id.layoutId);
		editText.setText(String.valueOf(currentLayoutId));
		rootView.findViewById(R.id.linear).setOnClickListener(this);
		rootView.findViewById(R.id.relative).setOnClickListener(this);
		rootView.findViewById(R.id.table).setOnClickListener(this);
		return rootView;
	}
	public void setLayoutId(int layoutId) {
		this.currentLayoutId = layoutId;
	}

	@Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.linear) {
			setLayoutId(R.layout.layouts_linear);
		} else if (id == R.id.table) {
			setLayoutId(R.layout.layouts_table);
		} else if (id == R.id.relative) {
			setLayoutId(R.layout.layouts_relative);
		} else {
			Log.w(TAG, "unrecognised button:" + view);
			return;
		}
		((LayoutsActivity) getActivity()).forceRecreateView();
	}
}

