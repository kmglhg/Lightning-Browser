/*
 * Copyright 2014 A.C.R. Development
 */
package acr.browser.lightning.constant;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.inject.Inject;

import acr.browser.lightning.R;
import acr.browser.lightning.app.BrowserApp;
import acr.browser.lightning.database.BookmarkManager;
import acr.browser.lightning.database.HistoryItem;
import acr.browser.lightning.preference.PreferenceManager;
import acr.browser.lightning.utils.Utils;
import acr.browser.lightning.view.LightningView;

public class StartPage extends AsyncTask<Void, Void, Void> {

    public static final String FILENAME = "homepage.html";

    private static final String HEAD_1 = "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\">"
            + "<head>"
            + "<meta content=\"en-us\" http-equiv=\"Content-Language\" />"
            + "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" />"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">"
            + "<title>";

    private static final String HEAD_2 = "</title>"
            + "</head>"
            + "<style> body { background: #454545; text-align: center; margin: 0px; }  #search_input { height: 35px; width: 100%; outline: none; border: none; font-size: 16px; background-color: transparent; }  span { display: block; overflow: hidden; padding-left: 5px; vertical-align: middle; }  .search_bar { display: block; vertical-align: middle; width: 90%; height: 35px; max-width: 500px; margin: 0 auto; background-color: #fff; box-shadow: 0px 2px 3px rgba( 0, 0, 0, 0.25); font-family: Arial; color: #444; -moz-border-radius: 2px; -webkit-border-radius: 2px; border-radius: 2px; }  #search_submit { outline: none; height: 37px; float: right; color: #404040; font-size: 16px; font-weight: bold; border: none; background-color: transparent; }  .outer { display: block; height: 100%; width: 100%; margin: 10% 0px 5% 0px; }  .middle { display: block; vertical-align: middle; }  .inner { margin-left: auto; margin-right: auto; width: 100%; }  img.smaller { width: 50%; max-width: 300px; }  .box { vertical-align: middle; position: relative; display: block; margin: 10px; padding-left: 10px; padding-right: 10px; padding-top: 5px; padding-bottom: 5px; background-color: #fff; box-shadow: 0px 3px rgba( 0, 0, 0, 0.1); font-family: Arial; color: #444; font-size: 12px; -moz-border-radius: 2px; -webkit-border-radius: 2px; border-radius: 2px; } .bookmarks { text-align: left; display: block; -webkit-touch-callout: none; -webkit-user-select: none; -khtml-user-select: none; -moz-user-select: none; -ms-user-select: none; user-select: none;} .bookmark { height: 30px; padding: 5px; margin: 10px 5%; background-color: #555; } .thumbnail { display: inline-block; margin: 5px 10px; height: 20px; width: 20px; background-size: contain;} .title { margin-left: 10px; height: 30px; line-height: 30px; display: inline-block; color: #f5f5f5; vertical-align: top;}</style>"
            + "<body> <div class=\"outer\"><div class=\"middle\"><div class=\"inner\"><img class=\"smaller\" src=\"";

    private static final String MIDDLE_1 = "\" ></br></br><form onsubmit=\"return search()\" class=\"search_bar\" autocomplete=\"off\">"
            + "<input type=\"submit\" id=\"search_submit\" value=\"Search\" ><span><input class=\"search\" type=\"text\" value=\"\" id=\"search_input\" >"
            + "</span></form></br></br></div></div>";
    private static final String MIDDLE_2 = "</div><script type=\"text/javascript\">function search(){if(document.getElementById(\"search_input\").value != \"\"){window.location.href = \"";

    private static final String END = "\" + document.getElementById(\"search_input\").value;document.getElementById(\"search_input\").value = \"\";}return false;}</script></body></html>";

    @NonNull private final String mTitle;
    @NonNull private final Application mApp;
    @NonNull private final WeakReference<LightningView> mTabReference;

    @Inject BookmarkManager mBookmarkManager;
    @Inject PreferenceManager mPreferenceManager;

    private String mStartpageUrl;

    public StartPage(LightningView tab, @NonNull Application app) {
        BrowserApp.getAppComponent().inject(this);
        mTitle = app.getString(R.string.home);
        mApp = app;
        mTabReference = new WeakReference<>(tab);
    }

    @Nullable
    @Override
    protected Void doInBackground(Void... params) {
        mStartpageUrl = getHomepage();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        LightningView tab = mTabReference.get();
        if (tab != null) {
            tab.loadUrl(mStartpageUrl);
        }
    }

    /**
     * This method builds the homepage and returns the local URL to be loaded
     * when it finishes building.
     *
     * @return the URL to load
     */
    @NonNull
    private String getHomepage() {
        StringBuilder homepageBuilder = new StringBuilder(HEAD_1 + mTitle + HEAD_2);
        String icon;
        String searchUrl;
        switch (mPreferenceManager.getSearchChoice()) {
            case 0:
                // CUSTOM SEARCH
                icon = "file:///android_asset/lightning.png";
                searchUrl = mPreferenceManager.getSearchUrl();
                break;
            case 1:
                // GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png";
                // "https://www.google.com/images/srpr/logo11w.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;
            case 2:
                // NAVER_SEARCH;
                icon = "file:///android_asset/naver.png";
                searchUrl = Constants.NAVER_SEARCH;
                break;
            case 3:
                // WIKIPEDIA_SEARCH;
                icon = "file:///android_asset/wikipedia.png";
                searchUrl = Constants.WIKIPEDIA_SEARCH;
                break;
            default:
                // DEFAULT GOOGLE_SEARCH;
                icon = "file:///android_asset/google.png";
                searchUrl = Constants.GOOGLE_SEARCH;
                break;
        }

        List<HistoryItem> bookmarks = getBookmarks();

        homepageBuilder.append(icon);
        homepageBuilder.append(MIDDLE_1);

        String bookmarkHtml = "<div class=\"bookmarks\">";
        for (HistoryItem bookmark : bookmarks) {
            Bitmap bitmap = bookmark.getBitmap();

            String enc = "";
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                enc = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            }

            bookmarkHtml += "<div class=\"bookmark\" onclick=\"javascript:window.location.href='" + bookmark.getUrl() + "'\"><div class=\"thumbnail\" style=\"background-image: url('data:image/png;base64," + enc + "')\"></div><div class=\"title\">" + bookmark.getTitle() + "</div></div>";
        }
        bookmarkHtml += "</div>";

        homepageBuilder.append(bookmarkHtml);
        homepageBuilder.append(MIDDLE_2);
        homepageBuilder.append(searchUrl);
        homepageBuilder.append(END);

        File homepage = new File(mApp.getFilesDir(), FILENAME);
        FileWriter hWriter = null;
        try {
            //noinspection IOResourceOpenedButNotSafelyClosed
            hWriter = new FileWriter(homepage, false);
            hWriter.write(homepageBuilder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Utils.close(hWriter);
        }

        return Constants.FILE + homepage;
    }

    private List<HistoryItem> getBookmarks() {
        return mBookmarkManager.getAllBookmarks(true);
    }

    public void load() {
        executeOnExecutor(BrowserApp.getIOThread());
    }

}
