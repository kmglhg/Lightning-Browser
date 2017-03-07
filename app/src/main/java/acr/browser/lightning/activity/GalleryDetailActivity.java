package acr.browser.lightning.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import acr.browser.lightning.R;
import uk.co.senab.photoview.PhotoView;


/**
 * Created by kmglh on 2016-11-29.
 */
public class GalleryDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_detail_layout);

        String imageUri = getIntent().getStringExtra("imageUri");
        String url = getIntent().getStringExtra("url");

        final PhotoView photoView = (PhotoView) findViewById(R.id.image);

        String cookies = CookieManager.getInstance().getCookie(url);
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        if (cookies != null) {
            builder.addHeader("Cookie", cookies);
        }
        builder.addHeader("referer", url);

        GlideUrl glideUrl = new GlideUrl(imageUri, builder.build());

        Glide.with(GalleryDetailActivity.this)
                .load(glideUrl)
                .fitCenter()
                .into(photoView);
    }
}