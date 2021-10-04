package jarden.explorer;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.jardenconsulting.explorer.BuildConfig;
import com.jardenconsulting.explorer.ExplorerActivity;

import java.io.File;

public class SearchService extends IntentService {
	private static final int SEARCH_NOTIFICATION_ID = 1;

	public SearchService() {
		super("SearchService");
        if (BuildConfig.DEBUG) {
        	Log.d(ExplorerActivity.TAG, "SearchService.SearchService()");
        }
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String directory = intent.getStringExtra(ExplorerActivity.DIRECTORY_TAG);
		String searchExpr = intent.getStringExtra(ExplorerActivity.SEARCH_EXPR_TAG);

		// the next line is what the service does; everything else is admin!
		String[] fileNames = ExplorerTools.findFileNames(new File(directory), searchExpr);
		
		Intent notifyIntent = new Intent(this, ExplorerActivity.class);
		notifyIntent.putExtra(ExplorerActivity.FILE_NAMES_TAG, fileNames);
		notifyIntent.putExtra(ExplorerActivity.DIRECTORY_TAG, directory);
		notifyIntent.putExtra(ExplorerActivity.SEARCH_EXPR_TAG, searchExpr);
		PendingIntent pendingIntent = PendingIntent.getActivity(
				this, 0, notifyIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
				.setContentIntent(pendingIntent)
//				.setSmallIcon(R.drawable.abc_ic_search_api_mtrl_alpha)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND)
				.setContentTitle("Explorer Search")
				.setContentText("Results are in for: " + searchExpr);
		NotificationManager mNotifyMgr =
				(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotifyMgr.notify(SEARCH_NOTIFICATION_ID, mBuilder.build());
	}

}
