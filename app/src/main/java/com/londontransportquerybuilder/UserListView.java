package com.londontransportquerybuilder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.utils.ObjectSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserListView extends Activity {

    private ListView userListView;

    public static List<UserItem> userValues;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_routes);

       restoreListFromPrefs();


        // Get ListView object from xml
        userListView = (ListView) findViewById(R.id.userListView);
        registerForContextMenu(userListView);
        Button addNewRouteButton = (Button) findViewById(R.id.addNewRouteButton);
        Button addNewStationButton = (Button) findViewById(R.id.addNewStationButton);
        Button runQueryButton = (Button) findViewById(R.id.RunQueryButton);

        // Assign adapter to ListView
        if (userValues.size() > 0) {
            userListView.setAdapter(setUpNewArrayAdapter());
        }

        // ListView Item Click Listener
        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // Nothing happens on click
            }

        });

        addNewRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewRoute.class);
                startActivityForResult(intent, 1);
            }
        });

        addNewStationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddNewStation.class);
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

            saveListToPrefs();

            Intent refresh = new Intent(this, UserListView.class);
            startActivity(refresh);
            this.finish();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_list_view_popup_menu, menu);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_list_view_action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.clear_user_routes) {
            userValues.clear();
            // Show Alert
            Toast.makeText(getApplicationContext(),"List Cleared", Toast.LENGTH_LONG).show();
            saveListToPrefs();
            userListView.setAdapter(setUpNewArrayAdapter());
            return true;
        }
        if (id == R.id.settings_menu_button) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivityForResult(intent, 1);
        }
        if (id == R.id.about_dialog) {
            AboutDialog dialog = new AboutDialog(this);
            dialog.show();

        }

        return super.onOptionsItemSelected(item);
    }






    public void deleteItem(int position) {
        userValues.remove(position);
        // Show Alert
        Toast.makeText(getApplicationContext(),"Item Deleted", Toast.LENGTH_LONG).show();
        saveListToPrefs();
        userListView.setAdapter(setUpNewArrayAdapter());

    }

    public void editItem(int position) {
        Intent intent = null;
        if (userValues.get(position) instanceof  UserRouteItem) {
            intent = new Intent(getApplicationContext(), AddNewRoute.class);
        } else if (userValues.get(position) instanceof  UserStationItem) {
            intent = new Intent(getApplicationContext(), AddNewStation.class);
        }
        Bundle b = new Bundle();
        b.putInt("Position", position);
        intent.putExtras(b);
        startActivityForResult(intent, 1);
    }

    public ArrayAdapter<UserItem> setUpNewArrayAdapter() {
        return new CustomList<UserItem>(this, userValues);
    }

    @SuppressWarnings("unchecked")
    public void restoreListFromPrefs() {

        if (null == userValues) {
            userValues = new ArrayList<UserItem>();
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            userValues = (ArrayList<UserItem>) ObjectSerializer.deserialize(prefs.getString("User_Route_Values", ObjectSerializer.serialize(new ArrayList<UserItem>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveListToPrefs() {
        //save the task list to preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString("User_Route_Values", ObjectSerializer.serialize((java.io.Serializable) userValues));
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.apply();

    }



}

