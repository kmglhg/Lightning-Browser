/*
 * Copyright 2014 A.C.R. Development
 */
package acr.browser.lightning.constant;

import android.app.Application;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

import acr.browser.lightning.R;
import acr.browser.lightning.app.BrowserApp;
import acr.browser.lightning.database.HistoryDatabase;
import acr.browser.lightning.utils.Utils;
import acr.browser.lightning.view.LightningView;

public class AllImagesPage extends AsyncTask<Void, Void, Void> {

    private static final String TAG = AllImagesPage.class.getSimpleName();

    public static final String FILENAME = "allImagePage.html";

    private static final String START = "<!DOCTYPE html><html xmlns=\"http://www.w3.org/1999/xhtml\">";

    private static final String HEAD = "<head><meta content=\"en-us\" http-equiv=\"Content-Language\" /> <meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\" /><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"><title>%s</title></head>";

    private static final String STYLE = "<style> body { background: #f5f5f5; margin: 0px; } .grid-wrapper { font-size: 0px; } .image-wrapper { display: inline-block; width: 25%; padding-top: 25%; position: relative; } .image { height: 80%; width: 80%; background-color: white; top: 10%; left: 10%; position: absolute; opacity: 0.3; } .select { opacity: 1; }</style>";

    private static final String SCRIPT = "<script> function toggleImage(el) { if (el.className.indexOf('select') > -1) { el.className = 'image'; } else { el.className = 'image select'; } } function allImagesDownload() { var list = []; var selectList = document.getElementsByClassName(\"select\"); for (var i = 0; i < selectList.length; i++) { list.push(selectList[i].getAttribute(\"url\")); } downloader.allImagesDownload(list); return false; } </script>";

    private static final String BODY = "<body><div class=\"download\" onClick=\"allImagesDownload()\">download</div><div class=\"grid-wrapper\">%s</div></body>";

    private static final String IMAGE = "<div class=\"image-wrapper\"><div class=\"image\" style=\"background: url('%s') no-repeat center center; background-size: contain;\" onClick=\"toggleImage(this)\"></div></div>";

    private static final String END = "</html>";

    @NonNull private final WeakReference<LightningView> mTabReference;
    @NonNull private final Application mApp;
    @NonNull private final String mUrl;
    @NonNull private final String mTitle;

    @Nullable private String mAllImageUrl = null;

    public AllImagesPage(LightningView tab, @NonNull Application app, @NonNull String url, @NonNull String title) {
        mTabReference = new WeakReference<>(tab);
        mApp = app;
        mUrl = url;
        mTitle = title;
    }

    @Override
    protected Void doInBackground(Void... params) {
        mAllImageUrl = getAllImagesPage();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        LightningView tab = mTabReference.get();
        if (tab != null && mAllImageUrl != null) {
            tab.loadUrl(mAllImageUrl);
        }
    }

    @NonNull
    private String getAllImagesPage() {
        ArrayList<String> images = new ArrayList<>();
        try {
            URL u = new URL(mUrl);
            URI uri = u.toURI();
            Document doc = Jsoup.connect(mUrl).get();
            Elements img = doc.select("[src]");
            if (img != null && img.size() > 0) {
                images = new ArrayList<>();
                for (Element el : img) {
                    if (el.tagName().equals("img")) {
                        String src = el.absUrl("src").trim();
                        if (src == null || src.length() <= 0) {
                            src = el.attr("src").trim();
                        }
                        String uriSrc = uri.resolve(src).toString();
                        if (uriSrc != null && uriSrc.length() > 0) {
                            if (!images.contains(uriSrc)) {
                                images.add(uriSrc);
                            }
                        }
                    }
                }
            }
            Elements imgEls = doc.select("img");
            if (imgEls != null && imgEls.size() > 0) {
                for (Element el : imgEls) {
                    String src = el.absUrl("src").trim();
                    if (src == null || src.length() <= 0) {
                        src = el.attr("src").trim();
                    }
                    String uriSrc = uri.resolve(src).toString();
                    if (uriSrc != null && uriSrc.length() > 0) {
                        if (!images.contains(uriSrc)) {
                            images.add(uriSrc);
                        }
                    }
                }
            }
            Elements iptEls = doc.select("input");
            if (imgEls != null && imgEls.size() > 0) {
                for (Element el : imgEls) {
                    if (el.attr("type").equalsIgnoreCase("image")) {
                        String src = el.absUrl("src").trim();
                        if (src == null || src.length() <= 0) {
                            src = el.attr("src").trim();
                        }
                        String uriSrc = uri.resolve(src).toString();
                        if (uriSrc != null && uriSrc.length() > 0) {
                            if (!images.contains(uriSrc)) {
                                images.add(uriSrc);
                            }
                        }
                    }
                }
            }
            Elements linkEls = doc.select("a");
            if (linkEls != null && linkEls.size() > 0) {
                for (Element el : linkEls) {
                    String href = el.attr("href");
                    if (href.endsWith(".jpg") || href.endsWith(".jpeg") || href.endsWith(".bmp") || href.endsWith(".ico") || href.endsWith(".gif") || href.endsWith(".png")) {
                        String uriSrc = uri.resolve(href).toString();
                        if (uriSrc != null && uriSrc.length() > 0) {
                            if (!images.contains(uriSrc)) {
                                images.add(uriSrc);
                            }
                        }
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (images != null && images.size() > 0) {
            StringBuilder pageBuilder = new StringBuilder(START + String.format(HEAD, "title") + STYLE + SCRIPT);
            String imgHtml = "";
            for (String imgUrl : images) {
                imgHtml += String.format(IMAGE, imgUrl);
            }
            pageBuilder.append(String.format(BODY, imgHtml));
            pageBuilder.append(END);
            File webPage = new File(mApp.getFilesDir(), FILENAME);
            FileWriter pageWriter = null;
            try {
                pageWriter = new FileWriter(webPage, false);
                pageWriter.write(pageBuilder.toString());
            } catch (IOException e) {
                Log.e(TAG, "Unable to write image page to disk", e);
            } finally {
                Utils.close(pageWriter);
            }
            return Constants.FILE + webPage;
        }
        return null;
    }

    public void load() {
        executeOnExecutor(BrowserApp.getIOThread());
    }
}