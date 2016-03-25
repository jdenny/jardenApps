package jarden.document;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jardenconsulting.jardenlib.BuildConfig;

import java.util.HashMap;
import java.util.Set;

/**
 * Utility class to produce and process SpannableStrings to allow
 * hypertext in TextViews. Markup:<ul>
 *     <li>[a]linkName[/a]</li>
 *     <li>[a]linkName|link text[/a]</li>
 *     <li>[b]bold[/b]</li>
 *     <li>[i]italics[/i]</li>
 *     <li>[l]large[/l]</li>
 *     <li>[r]red[/r]</li>
 *     <li>[t]bullet[/t]</li>
 * </ul>
 *
 * @author john.denny@gmail.com
 */
public class DocumentTextView {
    public interface OnShowPageListener {
        void onShowPage(String pageName);
    }
	private static final String TAG = "DocumentTextView";
    private OnShowPageListener onShowPageListener;
	private final TextView textView;
	private final HashMap<String, CharSequence> spannableMap;
    private final String homePageName;

	/**
	 *
	 * @param appContext
	 * @param textView to hold hypertext
	 * @param resIds string resource ids; first one assumed to be home page
	 */
	public DocumentTextView(Context appContext, TextView textView, int[] resIds,
                            OnShowPageListener onShowPageListener) {
		this(appContext, textView, resIds, false, onShowPageListener);
	}
	/**
	 *
	 * @param appContext
	 * @param textView to hold hypertext
	 * @param resIds string resource ids; first one assumed to be home page
	 * @param withPageNames show page name as title at head of page
	 */
	public DocumentTextView(Context appContext, TextView textView, int[] resIds,
				boolean withPageNames, OnShowPageListener onShowPageListener) {
		this.textView = textView;
        this.onShowPageListener = onShowPageListener;
        this.spannableMap = new HashMap<>();
		Resources resources = appContext.getResources();
		for (int resId: resIds) {
			String pageName = resources.getResourceEntryName(resId);
			String pageText = resources.getString(resId);
			String pageHeader = withPageNames ? pageName : null;
			CharSequence ss = getSpannable(pageText, pageHeader);
			spannableMap.put(pageName, ss);
		}
        this.homePageName = resources.getResourceEntryName(resIds[0]);
        showHomePage();
	}
    public void showHomePage() {
        showPage(this.homePageName);
    }

    /**
     * Show hypertext page with given name. Return false if
     * name not found.
     * @param name
     * @return
     */
    public boolean showPage(String name) {
		CharSequence ss = spannableMap.get(name);
        if (ss == null) {
            if (BuildConfig.DEBUG) Log.w(TAG,
                    "showPage(" + name + "); name not found");
            return false;
        }
		textView.setText(ss);
        textView.scrollTo(0, 0);
        if (this.onShowPageListener != null) {
            this.onShowPageListener.onShowPage(name);
        }
        return true;
	}
    /*!!
    public void setOnShowPageListener(OnShowPageListener onShowPageListener) {
        this.onShowPageListener = onShowPageListener;
    }
    */
    private SpannableStringBuilder getSpannable(String src, String pageName) {
		int index = 0;
		int startIndex; // index of '[' in [tag]
		int endIndex; // index of '[' in [/tag]
		int pipeIndex; // index of '|' in [a]linkName|link text[/a]
		String linkName;
		int startSpanIndex;
		String tagText;
		char tag;
		Object what;

		SpannableStringBuilder builder = new SpannableStringBuilder();
		if (pageName != null && pageName.length() > 0) {
			builder.append(pageName + "\n\n");
			builder.setSpan(new RelativeSizeSpan(1.6f), 0, pageName.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		while (true) {
			startIndex = src.indexOf('[', index);
			if (startIndex < 0 || (startIndex + 2) >= src.length()) break;
			if (src.charAt(startIndex + 2) != ']') {
				throw new IllegalStateException("found '[' with no matching ']'; src=" + src);
				// index = startIndex + 1;
				// continue; // found '[' with no matching ']'
			}
			tag = src.charAt(startIndex + 1);
			endIndex = src.indexOf("[/" + tag + "]", startIndex + 3);
			if (endIndex < 0) {
				throw new IllegalStateException("no matching end-tag for " + tag);
			}
			// append any plain text before tag:
			builder.append(src.substring(index, startIndex));
			tagText = src.substring(startIndex + 3, endIndex);
			startSpanIndex = builder.length();
			if (tag == 'a') {
				pipeIndex = tagText.indexOf('|');
				if (pipeIndex > 0) {
					linkName = tagText.substring(0, pipeIndex);
					tagText = tagText.substring(pipeIndex + 1);
				} else {
					linkName = tagText;
				}
				what = new MyClickableSpan(linkName);
                builder.append(tagText);
				// if next char is new line, also add space, to get round feature
				// whereby hyper-link fills rest of line
				if ((endIndex + 4) < src.length() && src.charAt(endIndex + 4) == '\n') {
					builder.append(' ');
				}
			} else {
                builder.append(tagText);
				if (tag == 'b') {
					what = new StyleSpan(android.graphics.Typeface.BOLD);
				} else if (tag == 'i') {
					what = new StyleSpan(android.graphics.Typeface.ITALIC);
				} else if (tag == 'l') {
					what = new RelativeSizeSpan(1.3f);
				} else if (tag == 'r') {
					what = new ForegroundColorSpan(Color.RED);
				} else if (tag == 't') { // bullet
					what = new BulletSpan(15, Color.BLACK);
				} else {
					Log.e(TAG, "unrecognised tag: " + tag);
					index = endIndex + 4;
					continue;
				}
			}
			builder.setSpan(what, startSpanIndex,
					startSpanIndex + tagText.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			index = endIndex + 4;
		}
		builder.append(src.substring(index));
		return builder;
	}
	private class MyClickableSpan extends ClickableSpan {
		private String linkName;

		public MyClickableSpan(String linkName) {
			this.linkName = linkName;
		}

		@Override
		public void onClick(View widget) {
			showPage(linkName);
		}
	}
}
