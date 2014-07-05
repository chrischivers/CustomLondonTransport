package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class UserListView extends Activity {
    private ListView listView ;
    public static List<UserRouteItem> values = new ArrayList<UserRouteItem>();
    private static boolean hasDatabaseBeenLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_routes);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.userListView);

        ArrayAdapter<UserRouteItem> adapter = new ArrayAdapter<UserRouteItem>(this, android.R.layout.simple_list_item_2, android.R.id.text1, values) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(values.get(position).getLine1());
                text2.setText(values.get(position).getLine2());
                return view;
            }
        };


        // Assign adapter to ListView
        listView.setAdapter(adapter);


        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value

                // Show Alert
                Toast.makeText(getApplicationContext(),
                        "Position :" + itemPosition, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

    public void loadAddNewRoute(View view) {
        Intent intent = new Intent(this, AddNewRoute.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            Intent refresh = new Intent(this, UserListView.class);
            startActivity(refresh);
            this.finish();
        }
    }

}