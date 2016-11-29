package acr.browser.lightning.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import acr.browser.lightning.R;

/**
 * Created by kmglh on 2016-11-29.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private List<String> data = new ArrayList<String>();
    private int resourceId;
    private LayoutInflater inflater ;

    public GridViewAdapter(Context context, int resourceId, List<String> data) {
        super(context, resourceId, data);
        this.resourceId = resourceId;
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }

    static class ViewHolder {
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            row = inflater.inflate(resourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag(); //Easy to recycle view
        }

        Glide.with(context)
//                .load("file://" + data.get(position))
                .load(data.get(position))
                .fitCenter()
                .centerCrop()
                .into(holder.image);
        return row;
    }
}