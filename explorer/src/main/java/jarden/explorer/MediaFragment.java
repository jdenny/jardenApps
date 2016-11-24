package jarden.explorer;

import java.io.File;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.jardenconsulting.explorer.BuildConfig;
import com.jardenconsulting.explorer.ExplorerActivity;
import com.jardenconsulting.explorer.R;

public class MediaFragment extends Fragment {

	private VideoView videoView;
	private MediaController mediaController;
    private int position;
    private File file;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"MediaFragment.onCreate(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"MediaFragment.onCreateView(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		View rootView = inflater.inflate(R.layout.fragment_media,
				container, false);
		this.videoView = (VideoView) rootView.findViewById(R.id.videoView);
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG, "MediaFragment.onStart()");
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG, "MediaFragment.onResume()");
		}
        if (this.file != null) playFile();
    }
	
	@Override
	public void onPause() {
		super.onPause();
		if (this.videoView.isPlaying()) {
			this.videoView.pause();
		}
        this.position = videoView.getCurrentPosition();
        if (BuildConfig.DEBUG) {
            Log.d(ExplorerActivity.TAG,
                    "MediaFragment.onPause();" +
                            " position=" + this.position);
        }
	}

	@Override
	public void onStop() {
		super.onStop();
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG, "MediaFragment.onStop()");
		}
	}
	
	@Override
	public void onDestroy() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG, "MediaFragment.onDestroy()");
		}
		if (this.mediaController != null) {
			this.videoView.stopPlayback();
			mediaController = null;
		}
	}
	public void setFile(File file) {
        this.file = file;
        this.position = 0;
        playFile();
    }
    private void playFile() {
        this.mediaController = new MediaController(getActivity());
        this.videoView.setMediaController(mediaController);
        videoView.setVideoPath(this.file.getAbsolutePath());
        videoView.requestFocus();
        videoView.seekTo(this.position);
        videoView.start();
    }
}

