package com.customlondontransport;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ArrayAdapter;

import java.util.List;


public class AddNewRoute extends Activity {
    private Spinner transportModeSpinner;
    private Spinner routeLineSpinner;
    private Spinner directionSpinner;
    private Spinner startingStopSpinner;

    public static boolean databaseLoaded = false;

    private MyDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);


        new Thread(new LoadDatabase()).run();


        transportModeSpinner = (Spinner) findViewById(R.id.transportModeSpinner);
        routeLineSpinner = (Spinner) findViewById(R.id.RouteLineSpinner);
        directionSpinner = (Spinner) findViewById(R.id.DirectionSpinner);
        startingStopSpinner = (Spinner) findViewById(R.id.StartingStopSpinner);
        ArrayAdapter<CharSequence> transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);

        routeLineSpinner.setEnabled(false);
        directionSpinner.setEnabled(false);
        startingStopSpinner.setEnabled(false);
       // addRouteToUserListButton.setEnabled(false);

        transportModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportModeSpinner.setAdapter(transportModeAdapter);

        transportModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Tube")) {
                    ArrayAdapter<ComboItem> routeLineAdapter = new ArrayAdapter<ComboItem>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeLines());
                    routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    routeLineSpinner.setAdapter(routeLineAdapter);
                    routeLineSpinner.setEnabled(true);

                } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
                    ArrayAdapter<String> routeLineAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusRoutes());
                    routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    routeLineSpinner.setAdapter(routeLineAdapter);
                    routeLineSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                routeLineSpinner.setEnabled(false);
            }
        });

        routeLineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Tube")) {
                    //Do something for tube

                } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
                    ArrayAdapter<ComboItem> directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusDirections(routeLineSpinner.getSelectedItem().toString()));
                    directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    directionSpinner.setAdapter(directionAdapter);
                    directionSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                directionSpinner.setEnabled(false);
            }
        });

        startingStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Tube")) {
                    //Do something for tube

                } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
                    //
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                startingStopSpinner.setEnabled(false);
            }
        });

        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Bus")) {
                   // ArrayAdapter<ComboItem> startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStops(routeLineSpinner.getSelectedItem().toString(), Integer.parseInt(((ComboItem) directionSpinner.getSelectedItem()).getID())));
                   // startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                  //  startingStopSpinner.setAdapter(startingStopAdapter);
                   // startingStopSpinner.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                startingStopSpinner.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.set_parameters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private  List<String> fetchBusRoutes() {
        return db.getBusRoutes();
    }

    private List<ComboItem> fetchBusDirections(String busRoute) {
        return db.getBusDirections(busRoute);
    }


    private List<ComboItem>  fetchBusStops(String busRoute, int busDirection) {
        return db.getBusStops(busRoute, busDirection);
    }

    private List<ComboItem> fetchTubeStations(String tubeLineID) {
        return db.getTubeStations(tubeLineID);
    }

    private List<ComboItem> fetchTubeLines () {
        return db.getTubeLines();
    }



    //button
    public void addAndReturnToUserListView(View view) {

        UserRouteItem userRouteItem = new UserRouteItem(transportModeSpinner.getSelectedItem().toString(), routeLineSpinner.getSelectedItem().toString(), ((ComboItem)directionSpinner.getSelectedItem()), ((ComboItem) startingStopSpinner.getSelectedItem()), null, 5);
        UserListView.values.add(userRouteItem);

        setResult(RESULT_OK, null);
        finish();
    }


    public class LoadDatabase implements Runnable  {

        @Override
        public void run() {
            db = new MyDatabase(getApplicationContext());
            System.out.println("Database Loaded");
        }
    }


}
