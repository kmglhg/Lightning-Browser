package acr.browser.lightning.activity;

import acr.browser.lightning.R;
import acr.browser.lightning.utils.GridViewAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by kmglh on 2016-11-29.
 */
public class GalleryActivity extends AppCompatActivity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private List<String> imageItems;
    private String imagePath="";
    private File savedFileDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);

        imagePath =  Environment.getExternalStorageDirectory().toString() + "/instinctcoder/gridview/";
        imageItems = new ArrayList<>();


        Bundle b = getIntent().getExtras();
        if (b != null) {
            imageItems = b.getStringArrayList("images");
        }

//        getImages();

        gridView = (GridView) findViewById(R.id.gridView);
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item, imageItems);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String imagePath =   parent.getAdapter().getItem(position).toString();
                Intent intent = new Intent(GalleryActivity.this, GalleryDetailActivity.class);
                intent.putExtra("imagePath", imagePath);

                startActivity(intent);
            }
        });
        gridView.setAdapter(gridAdapter);
    }

    private List<String> getImages(){
        new File(imagePath ).mkdirs();

        File fileTarget = new File(imagePath);
        File[] files = fileTarget.listFiles();

        imageItems.clear();

        if (files!=null){
            for (File file: files){
                imageItems.add(file.getAbsolutePath());
            }
        }

        return imageItems;
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
//        getImages();
        gridAdapter.notifyDataSetChanged(); //Notify adapter to refresh gridView
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString("destination", savedFileDestination.getName());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String sPath = savedInstanceState.getString("destination");
        savedFileDestination = new File(sPath);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}