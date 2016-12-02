package acr.browser.lightning.async;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by kmglh on 2016-12-01.
 */
public class GetAllImagesTask extends AsyncTask<String, Void, Boolean> {

    private GetAllImagesCallback callback;

    ArrayList<String> images;

    public GetAllImagesTask(GetAllImagesCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = params[0];

        try {
            URL u = new URL(url);
            URI uri = u.toURI();
            Document doc = Jsoup.connect(url).get();
            Elements img = doc.getElementsByTag("img");
            if (img != null && img.size() > 0) {
                images = new ArrayList<>();
                for (Element el : img) {
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images != null && images.size() > 0;
    }

    @Override
    protected void onPostExecute(Boolean success) {

        if (success) {
            callback.callback(images);
        }
    }
}