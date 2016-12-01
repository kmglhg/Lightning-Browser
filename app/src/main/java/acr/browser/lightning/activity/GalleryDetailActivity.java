package acr.browser.lightning.activity;

import android.app.Activity;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

        PhotoView photoView = (PhotoView) findViewById(R.id.image);
        Glide.with(GalleryDetailActivity.this)
                .load(imageUri)
//                .skipMemoryCache(true)
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .fitCenter()
                .centerCrop()
                .into(photoView);
    }


}