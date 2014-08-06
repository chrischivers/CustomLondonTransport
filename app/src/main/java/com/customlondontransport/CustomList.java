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
    public CustomList(Activity context, List<UserRouteItem> itemList) {
        super(context, R.layout.list_of_user_routes, itemList);
        this.context = context;
        this.itemList = itemList;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_of_user_routes, null, true);

        TextView itemText = (TextView) rowView.findViewById(R.id.txt1);
        TextView overlayText = (TextView) rowView.findViewById(R.id.routeNumberOverlay);
        ImageView transportIcon = (ImageView) rowView.findViewById(R.id.img);

        itemText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getLine1());
        overlayText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID());

        String imageName = ((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID().toLowerCase() + "_line_icon";

        if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Bus")) {
            transportIcon.setImageResource(R.drawable.bus_icon);

        } else if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Tube")) {
            transportIcon.setImageResource(getContext().getResources().getIdentifier(imageName, "drawable", getContext().getPackageName()));
        }

        return rowView;
    }
}
