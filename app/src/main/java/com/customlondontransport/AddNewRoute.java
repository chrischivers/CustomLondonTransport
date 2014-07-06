package com.customlondontransport;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class AddNewRoute extends Activity {
    private Spinner transportModeSpinner;
    private Spinner routeLineSpinner;
    private Spinner directionSpinner;
    private Spinner startingStopSpinner;

    private TextView transportModeLabel;
    private TextView routeLineLabel;
    private TextView directionLabel;
    private TextView startingStopLabel;

    private Button addRouteToUserListButton;

    private MyDatabase db;

    private boolean isTransportModeSet = false;
    private boolean isRouteLineSet = false;
    private boolean isDirectionSet = false;
    private boolean isStartingStopSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);

        // pull in the database
        new Thread(new LoadDatabase()).run();

        addRouteToUserListButton = (Button) findViewById(R.id.addRouteToUserListButton);
        transportModeSpinner = (Spinner) findViewById(R.id.transportModeSpinner);
        routeLineSpinner = (Spinner) findViewById(R.id.RouteLineSpinner);
        directionSpinner = (Spinner) findViewById(R.id.DirectionSpinner);
        startingStopSpinner = (Spinner) findViewById(R.id.StartingStopSpinner);

        transportModeLabel = (TextView) findViewById(R.id.TransportModeLabel);
        routeLineLabel = (TextView) findViewById(R.id.RouteLineLabel);
        directionLabel = (TextView) findViewById(R.id.DirectionLabel);
        startingStopLabel = (TextView) findViewById(R.id.StartingStopLabel);

        ArrayAdapter<CharSequence> transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);

        setVisibilities();

        transportModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportModeSpinner.setAdapter(transportModeAdapter);

        transportModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                isRouteLineSet = false;
                isStartingStopSet = false;
                isDirectionSet = false;

                if (transportModeSpinner.getSelectedItem().equals("Tube")) {
                    ArrayAdapter<ComboItem> routeLineAdapter = new ArrayAdapter<ComboItem>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeLines());
                    routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    routeLineSpinner.setAdapter(routeLineAdapter);
                    isTransportModeSet = true;
                }
                else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
                    ArrayAdapter<String> routeLineAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusRoutes());
                    routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    routeLineSpinner.setAdapter(routeLineAdapter);
                    isTransportModeSet = true;
                }
                else {
                    isTransportModeSet = false;
                }
                setVisibilities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isTransportModeSet = false;
                setVisibilities();
            }
        });

        routeLineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                isStartingStopSet = false;
                isDirectionSet = false;

                if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem)routeLineSpinner.getSelectedItem()).getID().equals("")) {
                    ArrayAdapter<ComboItem> startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStations(((ComboItem) routeLineSpinner.getSelectedItem()).getID()));
                   startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    startingStopSpinner.setAdapter(startingStopAdapter);
                    isRouteLineSet = true;

                } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !routeLineSpinner.getSelectedItem().equals("")) {
                    ArrayAdapter<ComboItem> directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusDirections(routeLineSpinner.getSelectedItem().toString()));
                    directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    directionSpinner.setAdapter(directionAdapter);
                    isRouteLineSet = true;
                }
                else {
                    isRouteLineSet = false;
                }
                setVisibilities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isRouteLineSet = false;
                setVisibilities();
            }
        });

        startingStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (transportModeSpinner.getSelectedItem().equals("Tube") && !startingStopSpinner.getSelectedItem().equals("")) {
                    isStartingStopSet = true;

                } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !startingStopSpinner.getSelectedItem().equals("")) {
                    isStartingStopSet = true;
                } else {
                    isStartingStopSet = false;
                }
                setVisibilities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isStartingStopSet = false;
                setVisibilities();
            }
        });

        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Bus") && !directionSpinner.getSelectedItem().equals("")) {
                    ArrayAdapter<ComboItem> startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStops(routeLineSpinner.getSelectedItem().toString(), Integer.parseInt(((ComboItem) directionSpinner.getSelectedItem()).getID())));
                    startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    startingStopSpinner.setAdapter(startingStopAdapter);
                    isDirectionSet = true;
                } else if (transportModeSpinner.getSelectedItem().equals("Tube") && !directionSpinner.getSelectedItem().equals(""))  {
                    isDirectionSet = true;
                } else {
                    isDirectionSet = false;
                }
                setVisibilities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isDirectionSet = false;
                setVisibilities();
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

    private void setVisibilities() {

        if (!isTransportModeSet) {
            routeLineSpinner.setVisibility(View.INVISIBLE);
            routeLineLabel.setVisibility(View.INVISIBLE);
            directionSpinner.setVisibility(View.INVISIBLE);
            directionLabel.setVisibility(View.INVISIBLE);
            startingStopSpinner.setVisibility(View.INVISIBLE);
            startingStopLabel.setVisibility(View.INVISIBLE);
            addRouteToUserListButton.setVisibility(View.INVISIBLE);
        } else if (!isRouteLineSet) {
            routeLineSpinner.setVisibility(View.VISIBLE);
            routeLineLabel.setVisibility(View.VISIBLE);
            directionSpinner.setVisibility(View.INVISIBLE);
            directionLabel.setVisibility(View.INVISIBLE);
            startingStopSpinner.setVisibility(View.INVISIBLE);
            startingStopLabel.setVisibility(View.INVISIBLE);
            addRouteToUserListButton.setVisibility(View.INVISIBLE);


        } else if (transportModeSpinner.getSelectedItem().equals("Bus")){
            if (!isDirectionSet) {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.VISIBLE);
                directionLabel.setVisibility(View.VISIBLE);
                startingStopSpinner.setVisibility(View.INVISIBLE);
                startingStopLabel.setVisibility(View.INVISIBLE);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            }
            else if (!isStartingStopSet) {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.VISIBLE);
                directionLabel.setVisibility(View.VISIBLE);
                startingStopSpinner.setVisibility(View.VISIBLE);
                startingStopLabel.setVisibility(View.VISIBLE);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.VISIBLE);
                directionLabel.setVisibility(View.VISIBLE);
                startingStopSpinner.setVisibility(View.VISIBLE);
                startingStopLabel.setVisibility(View.VISIBLE);
                addRouteToUserListButton.setVisibility(View.VISIBLE);
            }
        } else if (transportModeSpinner.getSelectedItem().equals("Tube")) {
            if (!isStartingStopSet) {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.INVISIBLE);
                directionLabel.setVisibility(View.INVISIBLE);
                startingStopSpinner.setVisibility(View.VISIBLE);
                startingStopLabel.setVisibility(View.VISIBLE);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else if (!isDirectionSet) {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.VISIBLE);
                directionLabel.setVisibility(View.VISIBLE);
                startingStopSpinner.setVisibility(View.VISIBLE);
                startingStopLabel.setVisibility(View.VISIBLE);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else {
                routeLineSpinner.setVisibility(View.VISIBLE);
                routeLineLabel.setVisibility(View.VISIBLE);
                directionSpinner.setVisibility(View.VISIBLE);
                directionLabel.setVisibility(View.VISIBLE);
                startingStopSpinner.setVisibility(View.VISIBLE);
                startingStopLabel.setVisibility(View.VISIBLE);
                addRouteToUserListButton.setVisibility(View.VISIBLE);
            }
        }
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
