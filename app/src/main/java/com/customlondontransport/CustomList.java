package com.customlondontransport;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomList<UserItem> extends ArrayAdapter<UserItem>{
    private Activity context;
    private final List<UserItem> itemList;
    private static Typeface font = null;
    public CustomList(Activity context, List<UserItem> itemList) {
        super(context, R.layout.list_of_user_routes, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        if (font == null) {
            Typeface.createFromAsset(context.getAssets(), "London-Tube.ttf");
        }
        ViewHolder holder;

        if (itemList.get(position) instanceof UserRouteItem) {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_of_user_routes, parent, false);

                holder = new ViewHolder();
                holder.itemText = (TextView) rowView.findViewById(R.id.txt1);

                holder.overlayText = (TextView) rowView.findViewById(R.id.routeNumberOverlay);
                holder.transportIcon = (ImageView) rowView.findViewById(R.id.img);

                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.itemText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getItemText());
            holder.overlayText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID());

            String imageName = ((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID().toLowerCase() + "_line_icon";

            if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Tube")) {
                holder.transportIcon.setImageResource(getContext().getResources().getIdentifier(imageName, "drawable", getContext().getPackageName()));
            } else if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Bus")){
                holder.overlayText.setTypeface(font);
                holder.overlayText.setBackgroundResource(R.drawable.layout_border);
            }

        } else if (itemList.get(position) instanceof UserStationItem) {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_of_user_routes, parent, false);

                holder = new ViewHolder();
                holder.itemText = (TextView) rowView.findViewById(R.id.txt1);
                holder.overlayText = (TextView) rowView.findViewById(R.id.routeNumberOverlay);
                holder.transportIcon = (ImageView) rowView.findViewById(R.id.img);

                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.itemText.setText(((com.customlondontransport.UserStationItem) itemList.get(position)).getItemText());
            String overLayText ="";
            if (((UserStationItem) itemList.get(position)).getTransportForm().equals("Bus")) {
                holder.transportIcon.setImageResource(R.drawable.bus_stop);
            } else if (((UserStationItem) itemList.get(position)).getTransportForm().equals("Tube")) {

                holder.transportIcon.setImageResource(R.drawable.underground_logo);
            }
        }

        return rowView;
    }

    private static class ViewHolder {
        public TextView itemText;
        public TextView overlayText;
        public ImageView transportIcon;
    }
}
