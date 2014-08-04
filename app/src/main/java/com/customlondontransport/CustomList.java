package com.customlondontransport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomList<UserRouteItem> extends ArrayAdapter<UserRouteItem>{
    private final Activity context;
    private final List<UserRouteItem> itemList;
    private final int imageId;
    public CustomList(Activity context, List<UserRouteItem> itemList, int imageId) {
        super(context, R.layout.list_of_user_routes, itemList);
        this.context = context;
        this.itemList = itemList;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_of_user_routes, null, true);
        TextView text1 = (TextView) rowView.findViewById(R.id.txt1);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        text1.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getLine1());
        imageView.setImageResource(imageId);
        return rowView;
    }
}
