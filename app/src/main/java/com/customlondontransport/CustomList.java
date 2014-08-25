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
    public CustomList(Activity context, List<UserItem> itemList) {
        super(context, R.layout.list_of_user_routes, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {
        ViewHolder holder;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_of_user_routes, parent, false);

            holder = new ViewHolder();
            holder.itemText = (TextView) rowView.findViewById(R.id.txt1);
            holder.transportIcon = (ImageView) rowView.findViewById(R.id.img);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }


        if (itemList.get(position) instanceof UserRouteItem) {
            holder.itemText.setText(((com.customlondontransport.UserRouteItem) itemList.get(position)).getItemText());
            rowView.setBackgroundColor(context.getResources().getColor(R.color.user_list_item_route));
            if (((UserRouteItem) itemList.get(position)).getTransportForm().equals("Bus")) {
                holder.transportIcon.setImageResource(R.drawable.bus_icon);
            } else if (((UserRouteItem) itemList.get(position)).getTransportForm().equals("Tube")) {
                holder.transportIcon.setImageResource(R.drawable.underground_logo);
            }
        } else if (itemList.get(position) instanceof UserStationItem) {
            holder.itemText.setText(((com.customlondontransport.UserStationItem) itemList.get(position)).getItemText());
            rowView.setBackgroundColor(context.getResources().getColor(R.color.user_list_item_station));
            if (((UserStationItem) itemList.get(position)).getTransportForm().equals("Bus")) {
                holder.transportIcon.setImageResource(R.drawable.bus_icon);
            } else if (((UserStationItem) itemList.get(position)).getTransportForm().equals("Tube")) {
                holder.transportIcon.setImageResource(R.drawable.underground_logo);
            }
        }

        return rowView;
    }

    private static class ViewHolder {
        public TextView itemText;
        public ImageView transportIcon;
    }
}
