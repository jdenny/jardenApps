package jarden.explorer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jardenconsulting.explorer.BuildConfig;
import com.jardenconsulting.explorer.ExplorerActivity;
import com.jardenconsulting.explorer.R;

import java.io.File;

public class ImageFragment extends Fragment {

	private ImageView imageView;
	private String filePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ImageFragment.onCreate(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"ImageFragment.onCreateView(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		View rootView = inflater.inflate(R.layout.fragment_image,
				container, false);
		this.imageView = rootView.findViewById(R.id.imageView);
		if (this.filePath != null) { // i.e. resumed
			setImage();
		}
		return rootView;
	}

	public void setFile(File file) {
		this.filePath = file.getAbsolutePath();
		setImage();
	}
	private void setImage() {
		Drawable drawable = Drawable.createFromPath(this.filePath);
		this.imageView.setImageDrawable(drawable);
	}
}

