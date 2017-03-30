package acr.browser.lightning.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import acr.browser.lightning.R;
import acr.browser.lightning.model.GridModel;

/**
 * Created by kmglh on 2016-11-29.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private List<GridModel> data;
    private int resourceId;
    private LayoutInflater inflater ;
    private String url;

    public GridViewAdapter(Context context, String url, int resourceId, List<GridModel> data) {
        super(context, resourceId, data);
        this.resourceId = resourceId;
        this.context = context;
        this.data = data;
        this.url = url;
        inflater = LayoutInflater.from(context);

    }

    static class ViewHolder {
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;
        if (row == null) {
            row = inflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        String cookies = CookieManager.getInstance().getCookie(url);
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        if (cookies != null) {
            builder.addHeader("Cookie", cookies);
        }
        builder.addHeader("referer", url);

        GlideUrl glideUrl = new GlideUrl(data.get(position).getUrl(), builder.build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .into(holder.image);

        if (data.get(position).isSelected()) {
            holder.image.setAlpha(1.0f);
        } else {
            holder.image.setAlpha(0.3f);
        }
        return row;
    }
}