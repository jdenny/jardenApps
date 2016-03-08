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

import java.util.HashMap;
import java.util.Set;

/**
 * Utility class to produce and process SpannableStrings to allow
 * hypertext in TextViews. Markup:<ul>
 *     <li>[a]links[/a]</li>
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
    private static final String TAG = "DocumentTextView";
    private final TextView textView;
    private final HashMap<String, CharSequence> spannableMap;

    /**
     *
     * @param appContext
     * @param textView to hold hypertext
     * @param resIds string resource ids; first one assumed to be home page
     */
    public DocumentTextView(Context appContext, TextView textView, int[] resIds) {
        this(appContext, textView, resIds, false);
    }
    /**
     *
     * @param appContext
     * @param textView to hold hypertext
     * @param resIds string resource ids; first one assumed to be home page
     * @param withPageNames show page name as title at head of page
     */
    public DocumentTextView(Context appContext, TextView textView, int[] resIds,
                boolean withPageNames) {
        this.textView = textView;
        this.spannableMap = new HashMap<>();
        Resources resources = appContext.getResources();
        for (int resId: resIds) {
            String pageName = resources.getResourceEntryName(resId);
            String pageText = resources.getString(resId);
            String pageHeader = withPageNames ? pageName : null;
            CharSequence ss = getSpannable(pageText, pageHeader);
            spannableMap.put(pageName, ss);

        }
        String homePageName = resources.getResourceEntryName(resIds[0]);
        CharSequence homePage = spannableMap.get(homePageName);
        textView.setText(homePage);
    }
    public void showPage(String name) {
        CharSequence ss = spannableMap.get(name);
        textView.setText(ss);
        textView.scrollTo(0, 0);
    }
    private SpannableStringBuilder getSpannable(String src, String pageName) {
        int index = 0;
        int startIndex;
        int endIndex;
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
                throw new IllegalStateException("found '[' with no matching ']'");
                // index = startIndex + 1;
                // continue; // found '[' with no matching ']'
            }
            tag = src.charAt(startIndex + 1);
            endIndex = src.indexOf("[/" + tag + "]", startIndex + 3);
            if (endIndex < 0) {
                throw new IllegalStateException("no matching end-tag for " + tag);
            }
            builder.append(src.substring(index, startIndex));
            tagText = src.substring(startIndex + 3, endIndex);
            startSpanIndex = builder.length();
            builder.append(tagText);
            if (tag == 'a') {
                what = new MyClickableSpan(tagText);
                // if next char is new line, also add space, to get round feature
                // whereby hyper-link fills rest of line
                if ((endIndex + 4) < src.length() && src.charAt(endIndex + 4) == '\n') {
                    builder.append(' ');
                }
            } else if (tag == 'b') {
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
