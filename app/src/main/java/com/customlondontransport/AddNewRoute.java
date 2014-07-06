package com.customlondontransport;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
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

    private LinearLayout linearLayoutLeft;
    private LinearLayout linearLayoutRight;

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

        linearLayoutLeft = (LinearLayout) findViewById(R.id.linearLayoutLeft);
        linearLayoutRight = (LinearLayout) findViewById(R.id.linearLayoutRight);


        ArrayAdapter<CharSequence> transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);

        linearLayoutLeft.removeAllViewsInLayout();
        linearLayoutRight.removeAllViewsInLayout();
        linearLayoutLeft.addView(transportModeLabel);
        linearLayoutRight.addView(transportModeSpinner);

        setLayout();

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
                    ArrayAdapter<ComboItem> routeLineAdapter = new ArrayAdapter<ComboItem>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusRoutes());
                    routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    routeLineSpinner.setAdapter(routeLineAdapter);
                    isTransportModeSet = true;
                }
                else {
                    isTransportModeSet = false;
                }
                setLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isTransportModeSet = false;
                setLayout();
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

                } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem)routeLineSpinner.getSelectedItem()).getID().equals("")) {
                    ArrayAdapter<ComboItem> directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusDirections(routeLineSpinner.getSelectedItem().toString()));
                    directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    directionSpinner.setAdapter(directionAdapter);
                    isRouteLineSet = true;
                }
                else {
                    isRouteLineSet = false;
                }
                setLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isRouteLineSet = false;
                setLayout();
            }
        });

        startingStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem)startingStopSpinner.getSelectedItem()).getID().equals("")) {
                    ArrayAdapter<ComboItem> directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeDirectionsAndPlatforms(((ComboItem) routeLineSpinner.getSelectedItem()).getID(), ((ComboItem) startingStopSpinner.getSelectedItem()).getID()));
                    directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    directionSpinner.setAdapter(directionAdapter);
                    isStartingStopSet = true;

                } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem)startingStopSpinner.getSelectedItem()).getID().equals("")) {
                    isStartingStopSet = true;
                } else {
                    isStartingStopSet = false;
                }
                setLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isStartingStopSet = false;
                setLayout();
            }
        });

        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem)directionSpinner.getSelectedItem()).getID().equals("")) {
                    ArrayAdapter<ComboItem> startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStops(routeLineSpinner.getSelectedItem().toString(), Integer.parseInt(((ComboItem) directionSpinner.getSelectedItem()).getID())));
                    startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    startingStopSpinner.setAdapter(startingStopAdapter);
                    isDirectionSet = true;
                } else if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem)directionSpinner.getSelectedItem()).getID().equals(""))  {
                    isDirectionSet = true;
                } else {
                    isDirectionSet = false;
                }
                setLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isDirectionSet = false;
                setLayout();
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

    private void setLayout() {

        if (!isTransportModeSet) {
            linearLayoutLeft.removeAllViewsInLayout();
            linearLayoutRight.removeAllViewsInLayout();
            linearLayoutLeft.addView(transportModeLabel);
            linearLayoutRight.addView(transportModeSpinner);
            addRouteToUserListButton.setVisibility(View.INVISIBLE);
        } else if (!isRouteLineSet) {
            linearLayoutLeft.removeAllViewsInLayout();
            linearLayoutRight.removeAllViewsInLayout();
            linearLayoutLeft.addView(transportModeLabel);
            linearLayoutRight.addView(transportModeSpinner);
            linearLayoutLeft.addView(routeLineLabel);
            linearLayoutRight.addView(routeLineSpinner);
            addRouteToUserListButton.setVisibility(View.INVISIBLE);

        } else if (transportModeSpinner.getSelectedItem().equals("Bus")){
            if (!isDirectionSet) {
                linearLayoutLeft.removeAllViewsInLayout();
                linearLayoutRight.removeAllViewsInLayout();
                linearLayoutLeft.addView(transportModeLabel);
                linearLayoutRight.addView(transportModeSpinner);
                linearLayoutLeft.addView(routeLineLabel);
                linearLayoutRight.addView(routeLineSpinner);
                linearLayoutLeft.addView(directionLabel);
                linearLayoutRight.addView(directionSpinner);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            }
            else if (!isStartingStopSet) {
                linearLayoutLeft.removeAllViewsInLayout();
                linearLayoutRight.removeAllViewsInLayout();
                linearLayoutLeft.addView(transportModeLabel);
                linearLayoutRight.addView(transportModeSpinner);
                linearLayoutLeft.addView(routeLineLabel);
                linearLayoutRight.addView(routeLineSpinner);
                linearLayoutLeft.addView(directionLabel);
                linearLayoutRight.addView(directionSpinner);
                linearLayoutLeft.addView(startingStopLabel);
                linearLayoutRight.addView(startingStopSpinner);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else {
                addRouteToUserListButton.setVisibility(View.VISIBLE);
            }
        } else if (transportModeSpinner.getSelectedItem().equals("Tube")) {
            if (!isStartingStopSet) {
                linearLayoutLeft.removeAllViewsInLayout();
                linearLayoutRight.removeAllViewsInLayout();
                linearLayoutLeft.addView(transportModeLabel);
                linearLayoutRight.addView(transportModeSpinner);
                linearLayoutLeft.addView(routeLineLabel);
                linearLayoutRight.addView(routeLineSpinner);
                linearLayoutLeft.addView(startingStopLabel);
                linearLayoutRight.addView(startingStopSpinner);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else if (!isDirectionSet) {
                linearLayoutLeft.removeAllViewsInLayout();
                linearLayoutRight.removeAllViewsInLayout();
                linearLayoutLeft.addView(transportModeLabel);
                linearLayoutRight.addView(transportModeSpinner);
                linearLayoutLeft.addView(routeLineLabel);
                linearLayoutRight.addView(routeLineSpinner);
                linearLayoutLeft.addView(startingStopLabel);
                linearLayoutRight.addView(startingStopSpinner);
                linearLayoutLeft.addView(directionLabel);
                linearLayoutRight.addView(directionSpinner);
                addRouteToUserListButton.setVisibility(View.INVISIBLE);
            } else {
                addRouteToUserListButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private  List<ComboItem> fetchBusRoutes() {
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

    private  synchronized List<ComboItem>  fetchTubeDirectionsAndPlatforms(String tubeLineID, String tubeStationID)  {
            APIFetcher apifetcher = new APIFetcher();
            apifetcher.execute(tubeLineID, tubeStationID);
            return apifetcher.getTubeDirectionsAndPlatformList();

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

    class APIFetcher extends AsyncTask <String, Void, Void>{

        List<ComboItem> tubeDirectionsAndPlatformList;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            tubeDirectionsAndPlatformList = null;
            tubeDirectionsAndPlatformList = (new APIInterface().fetchTubeDirectionsAndPlatforms(strings[0], strings[1]));
            notifyAll();
            return null;
        }

        public synchronized List<ComboItem> getTubeDirectionsAndPlatformList() {
            while (tubeDirectionsAndPlatformList == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeDirectionsAndPlatformList;
        }

    }


}
