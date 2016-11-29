package acr.browser.lightning.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import acr.browser.lightning.R;

/**
 * Created by kmglh on 2016-11-29.
 */
public class GalleryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_detail_layout);

        String imagePath = getIntent().getStringExtra("imagePath");

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Glide.with(GalleryDetailActivity.this)
//                .load("file://" + imagePath)
                .load(imagePath)
                .fitCenter()
                .centerCrop()
                .into(imageView);
    }
}