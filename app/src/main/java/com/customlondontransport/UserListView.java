package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private ListView userListView;
    private Button addNewRouteButton;
    private Button runQueryButton;

    public static List<UserRouteItem> userRouteValues = new ArrayList<UserRouteItem>();
    private static boolean hasDatabaseBeenLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_routes);

        // Get ListView object from xml
        userListView = (ListView) findViewById(R.id.userListView);
        registerForContextMenu(userListView);
        addNewRouteButton = (Button) findViewById(R.id.addNewRouteButton);
        runQueryButton = (Button) findViewById(R.id.RunQueryButton);


        // Assign adapter to ListView
        userListView.setAdapter(setUpNewArrayAdapter());

        // ListView Item Click Listener
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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

        addNewRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewRoute.class);
                startActivityForResult(intent, 1);
            }
        });

        runQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), QueryResults.class);
                startActivityForResult(intent, 2);
            }
        });
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_list_view_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                deleteItem(info.position);
                return true;
            case R.id.edit:
                editItem(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void deleteItem(int position) {
     userRouteValues.remove(position);
        // Show Alert
        Toast.makeText(getApplicationContext(),"Item Deleted", Toast.LENGTH_LONG).show();
        setUpNewArrayAdapter();
        userListView.setAdapter(setUpNewArrayAdapter());
    }

    public void editItem(int position) {
        Intent intent = new Intent(getApplicationContext(), AddNewRoute.class);
        Bundle b = new Bundle();
        b.putInt("Position", position);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }

    public ArrayAdapter<UserRouteItem> setUpNewArrayAdapter() {
        ArrayAdapter<UserRouteItem> adapter = new ArrayAdapter<UserRouteItem>(this, android.R.layout.simple_list_item_2, android.R.id.text1, userRouteValues) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                text1.setText(userRouteValues.get(position).getLine1());
                text2.setText(userRouteValues.get(position).getLine2());
                return view;
            }
        };
        return adapter;
    }

}

