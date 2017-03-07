package acr.browser.lightning.activity;

import acr.browser.lightning.R;
import acr.browser.lightning.app.BrowserApp;
import acr.browser.lightning.model.GridModel;
import acr.browser.lightning.preference.PreferenceManager;
import acr.browser.lightning.utils.GridViewAdapter;
import acr.browser.lightning.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

/**
 * Created by kmglh on 2016-11-29.
 */
public class GalleryActivity extends Activity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private List<GridModel> imageItems;

    @Inject PreferenceManager mPreferenceManager;

    private String userAgent;
    private String path;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        BrowserApp.getAppComponent().inject(this);

        Intent intent = getIntent();

        List<String> iList = intent.getStringArrayListExtra("images");
        if (iList != null) {
            imageItems = new ArrayList<>();
            for (String iUrl : iList) {
                GridModel gm = new GridModel();
                gm.setSelected(false);
                gm.setUrl(iUrl);
                imageItems.add(gm);
            }
        }

        userAgent = intent.getStringExtra("userAgent");
        path = intent.getStringExtra("path");
        url = intent.getStringExtra("url");

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, url, R.layout.grid_item, imageItems);

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String imageUri = parent.getAdapter().getItem(position).toString();
                Intent intent = new Intent(GalleryActivity.this, GalleryDetailActivity.class);
                intent.putExtra("imageUri", imageUri);
                intent.putExtra("url", url);
                startActivity(intent);
                return false;
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                GridModel gm = imageItems.get(position);
                gm.setSelected(!gm.isSelected());
                imageItems.set(position, gm);

                gridAdapter.notifyDataSetChanged();
            }
        });

        gridView.setAdapter(gridAdapter);

        Button downloadBtn = (Button) findViewById(R.id.downloadBtn);

        downloadBtn.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Timer().schedule(new TimerTask() {
                    public void run() {
                        if (imageItems != null && imageItems.size() > 0) {
                            for (GridModel gm : imageItems) {
                                if (gm.isSelected()) {
                                    Utils.downloadFile(GalleryActivity.this, mPreferenceManager, gm.getUrl(), userAgent, "attachment", path, url);
                                }
                            }
                        }
                    }
                }, 928);
            }
        });
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}