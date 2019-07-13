package jarden.explorer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jardenconsulting.explorer.BuildConfig;
import com.jardenconsulting.explorer.ExplorerActivity;
import com.jardenconsulting.explorer.R;

import java.io.File;
import java.util.Arrays;

public class ExplorerFragment extends Fragment implements OnItemClickListener {
    private static final String TAG = "ExplorerFragment";
    // if we don't have read access to root directory, try "/sdcard"
	private static final File ROOT_DIR = new File("/");

    private File currentDir;
	private ArrayAdapter<String> fileListAdapter;
	
	public ExplorerFragment() {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ExplorerFragment.ExplorerFragment()");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ExplorerFragment.onCreate(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		this.currentDir = ROOT_DIR;
        /*
        This is needed for SDK 23 or higher; still needs to be requested
        in the manifest file
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE }, 1);
            Log.d(TAG, "can NOT read external storage");
        } else {
            Log.d(TAG, "CAN read external storage");
        }
		setRetainInstance(true);
	}
	public void showRootDirectory() {
		showDirectory(ROOT_DIR);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ExplorerFragment.onCreateView(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		View rootView = inflater.inflate(R.layout.fragment_explorer,
				container, false);
		this.fileListAdapter = new ArrayAdapter<>(
				getActivity(), android.R.layout.simple_list_item_1);
        ListView fileListView = rootView.findViewById(R.id.fileListView);
		fileListView.setAdapter(fileListAdapter);
		fileListView.setOnItemClickListener(this);
		showDirectory(this.currentDir);
		return rootView;
	}
	private void showFile(String fileName) {
		File file;
		if (fileName.startsWith("/")) {
			file = new File(fileName);
		} else {
			file = new File(this.currentDir, fileName);
		}
		if (file.isDirectory()) {
			showDirectory(file);
		} else {
			((OnFileSelectedListener) getActivity()).onFileSelected(file);
		}
	}
	private void showDirectory(File directory) {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
				"ExplorerFragment.showDirectory(" + directory + ")");
		}
		String[] fileNames = directory.list();
		if (fileNames == null) {
			Log.w(ExplorerActivity.TAG,
				"ExplorerFragment.showDirectory(" + directory + "); null list");
			return;
		}
		this.currentDir = directory;
		Arrays.sort(fileNames);
		showFileNames(fileNames);
		getActivity().setTitle(directory.getAbsolutePath());
	}
	public void showFileNames(String[] fileNames) {
		this.fileListAdapter.setNotifyOnChange(false);
		this.fileListAdapter.clear();
		for (String fileName: fileNames) {
			this.fileListAdapter.add(fileName);
		}
		this.fileListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		TextView textView = (TextView) view;
		String fileName = textView.getText().toString();
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ExplorerFragment.onItemClick(fileName=" +
					fileName + ")");
		}
		showFile(fileName);
	}
	public boolean onBackPressed() {
		if (currentDir != null) {
			currentDir = currentDir.getParentFile();
		}
		if (currentDir != null) {
			showDirectory(currentDir);
			return true;
		}
		return false;
	}
	public File getCurrentDirectory() {
		return this.currentDir;
	}
}

