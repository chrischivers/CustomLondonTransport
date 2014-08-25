package com.customlondontransport;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomList<UserItem> extends ArrayAdapter<UserItem>{
    private final Activity context;
    private final List<UserItem> itemList;
    public CustomList(Activity context, List<UserItem> itemList) {
        super(context, R.layout.list_of_user_routes, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
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

            holder.itemText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getLine1());
            holder.overlayText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID());

            String imageName = ((com.customlondontransport.UserRouteItem) itemList.get(position)).getRouteLine().getID().toLowerCase() + "_line_icon";

            if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Bus")) {
                holder.transportIcon.setImageResource(R.drawable.bus_icon);

            } else if (((com.customlondontransport.UserRouteItem) itemList.get(position)).getTransportForm().equals("Tube")) {
                holder.transportIcon.setImageResource(getContext().getResources().getIdentifier(imageName, "drawable", getContext().getPackageName()));
            }
        } else if (itemList.get(position) instanceof UserStationItem) {
            //TODO
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

            holder.itemText.setText(((com.customlondontransport.UserStationItem) itemList.get(position)).getLine1());
            String overLayText ="";
            if (((UserStationItem) itemList.get(position)).getTransportForm().equals("Bus")) {
                for (Object routeLine : ((com.customlondontransport.UserStationItem) itemList.get(position)).getRouteLineList()) {
                    overLayText = overLayText + ((String) routeLine);
                }
                holder.transportIcon.setImageResource(R.drawable.bus_stop);
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
