package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class AddNewRoute extends Activity {

    // get UserRouteValues position if any (applies if UserRouteItem is being edited)
    boolean inEditMode = false;
    int positionToRestore = -1; //-1 as default if not in edit mode;

    private Spinner transportModeSpinner;
    private Spinner routeLineSpinner;
    private Spinner directionSpinner;
    private Spinner startingStopSpinner;

    private ArrayAdapter<ComboItem> routeLineAdapter;
    private ArrayAdapter<CharSequence> transportModeAdapter;
    private ArrayAdapter<ComboItem> startingStopAdapter;
    private ArrayAdapter<ComboItem> directionAdapter;

    private TextView transportModeLabel;
    private TextView routeLineLabel;
    private TextView directionLabel;
    private TextView startingStopLabel;
    private TextView conditionsLabel;
    private TextView headingText;

    private Switch conditionsSwitch;
    private TextView conditionsPreviewText;

    private LinearLayout linearLayoutLeft;
    private LinearLayout linearLayoutRight;

    private Button addToOrUpdateUserListButton;

    private MyDatabase db;

    private DayTimeConditions dtc;

    private boolean isTransportModeSet = false;
    private boolean isRouteLineSet = false;
    private boolean isDirectionSet = false;
    private boolean isStartingStopSet = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);


        if (getIntent().hasExtra("Position")) {
            Bundle b = getIntent().getExtras();
            positionToRestore = b.getInt("Position");
            inEditMode = true;
        }

        // pull in the database
        new Thread(new LoadDatabase()).run();

        addToOrUpdateUserListButton = (Button) findViewById(R.id.addToOrUpdateUserListButton);
        if (!inEditMode) {
            addToOrUpdateUserListButton.setText("Add");
        } else {
            addToOrUpdateUserListButton.setText("Update");
        }
        transportModeSpinner = (Spinner) findViewById(R.id.transportModeSpinner);
        routeLineSpinner = (Spinner) findViewById(R.id.RouteLineSpinner);
        directionSpinner = (Spinner) findViewById(R.id.DirectionSpinner);
        startingStopSpinner = (Spinner) findViewById(R.id.StartingStopSpinner);

        transportModeLabel = (TextView) findViewById(R.id.TransportModeLabel);
        routeLineLabel = (TextView) findViewById(R.id.RouteLineLabel);
        directionLabel = (TextView) findViewById(R.id.DirectionLabel);
        startingStopLabel = (TextView) findViewById(R.id.StartingStopLabel);
        conditionsLabel = (TextView) findViewById(R.id.ConditionsLabel);
        headingText = (TextView) findViewById(R.id.HeadingText);

        conditionsSwitch = (Switch) findViewById(R.id.ConditionsSwitch);
        conditionsPreviewText = (TextView) findViewById(R.id.ConditionsPreviewText);

        linearLayoutLeft = (LinearLayout) findViewById(R.id.linearLayoutLeft);
        linearLayoutRight = (LinearLayout) findViewById(R.id.linearLayoutRight);

        // Populate transport mode adapter as default first step
        transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);

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
                onTransportModeSpinnerChange();
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
                onRouteLineSpinnerChange();
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
                onStartingStopSpinnerChange();
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
               onDirectionSpinnerChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isDirectionSet = false;
                setLayout();
            }
        });

        conditionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadSetDayTimeConditions();
                } else {
                    dtc = null;
                    conditionsPreviewText.setText("");
                }
            }
        });

        // Adjust transport spinner if in EDIT MODE
        if (inEditMode) {
            //transport Spinner
            String transportMode = UserListView.userRouteValues.get(positionToRestore).getTransportForm();
            int adapterPosition = transportModeAdapter.getPosition(transportMode);
            transportModeSpinner.setSelection(adapterPosition);
            onTransportModeSpinnerChange();
        }
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

    public void loadSetDayTimeConditions() {
        Intent intent = new Intent(this, SetDayTimeConditions.class);
        startActivityForResult(intent, 1);
    }

    public void onTransportModeSpinnerChange() {
        isRouteLineSet = false;
        isStartingStopSet = false;
        isDirectionSet = false;

        if (transportModeSpinner.getSelectedItem().equals("Tube")) {
            routeLineAdapter = new ArrayAdapter<ComboItem>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeLines());
            routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            routeLineSpinner.setAdapter(routeLineAdapter);
            isTransportModeSet = true;

        } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
            routeLineAdapter = new ArrayAdapter<ComboItem>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusRoutes());
            routeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            routeLineSpinner.setAdapter(routeLineAdapter);
            isTransportModeSet = true;

        } else {
            isTransportModeSet = false;
        }
        setLayout();

        // IF IN EDIT MODE
        if (inEditMode) {
            String routeLineID = UserListView.userRouteValues.get(positionToRestore).getRouteLine().getID();
            for (int i = 0; i < routeLineAdapter.getCount(); i++) {
                if (routeLineAdapter.getItem(i).getID().equals(routeLineID)) {
                    routeLineSpinner.setSelection(i, true);
                    break;
                }
            }
            onRouteLineSpinnerChange();
        }
    }

    public void onRouteLineSpinnerChange() {
        isStartingStopSet = false;
        isDirectionSet = false;

        if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem) routeLineSpinner.getSelectedItem()).getID().equals("")) {
            startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStations(((ComboItem) routeLineSpinner.getSelectedItem()).getID()));
            startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startingStopSpinner.setAdapter(startingStopAdapter);
            isRouteLineSet = true;

            // IF IN EDIT MODE
            if (inEditMode) {
                String startingStopID = UserListView.userRouteValues.get(positionToRestore).getStartingStop().getID();
                for(int i=0 ; i<startingStopAdapter.getCount() ; i++){
                    if (startingStopAdapter.getItem(i).getID().equals(startingStopID)) {
                        startingStopSpinner.setSelection(i, true);
                        break;
                    }
                }
                onStartingStopSpinnerChange();
            }

        } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem) routeLineSpinner.getSelectedItem()).getID().equals("")) {
            directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusDirections(routeLineSpinner.getSelectedItem().toString()));
            directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            directionSpinner.setAdapter(directionAdapter);
            isRouteLineSet = true;

            // IF IN EDIT MODE
            if (inEditMode) {
                String directionID = UserListView.userRouteValues.get(positionToRestore).getDirection().getID();
                for(int i=0 ; i<directionAdapter.getCount() ; i++){
                    if (directionAdapter.getItem(i).getID().equals(directionID)) {
                        directionSpinner.setSelection(i, true);
                        break;
                    }
                }
                onDirectionSpinnerChange();
            }
        } else {
            isRouteLineSet = false;
        }
        setLayout();
    }

    public void onStartingStopSpinnerChange() {
        if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem) startingStopSpinner.getSelectedItem()).getID().equals("")) {
            directionAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeDirectionsAndPlatforms(((ComboItem) routeLineSpinner.getSelectedItem()).getID(), ((ComboItem) startingStopSpinner.getSelectedItem()).getID()));
            directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            directionSpinner.setAdapter(directionAdapter);
            isStartingStopSet = true;

            // IF IN EDIT MODE
            if (inEditMode) {
                String directionID = UserListView.userRouteValues.get(positionToRestore).getDirection().getID();
                for(int i=0 ; i<directionAdapter.getCount() ; i++){
                    if (directionAdapter.getItem(i).getID().equals(directionID)) {
                        directionSpinner.setSelection(i, true);
                        break;
                    }
                }
                onDirectionSpinnerChange();
            }

        } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem) startingStopSpinner.getSelectedItem()).getID().equals("")) {
            isStartingStopSet = true;
        } else {
            isStartingStopSet = false;
        }
        setLayout();
    }

    public void onDirectionSpinnerChange() {
        if (transportModeSpinner.getSelectedItem().equals("Bus") && !((ComboItem) directionSpinner.getSelectedItem()).getID().equals("")) {
            startingStopAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStops(routeLineSpinner.getSelectedItem().toString(), Integer.parseInt(((ComboItem) directionSpinner.getSelectedItem()).getID())));
            startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            startingStopSpinner.setAdapter(startingStopAdapter);
            isDirectionSet = true;

            // IF IN EDIT MODE
            if (inEditMode) {
                String startingStopID = UserListView.userRouteValues.get(positionToRestore).getStartingStop().getID();
                for(int i=0 ; i<startingStopAdapter.getCount() ; i++){
                    if (startingStopAdapter.getItem(i).getID().equals(startingStopID)) {
                        startingStopSpinner.setSelection(i, true);
                        break;
                    }
                }
                onStartingStopSpinnerChange();
            }
        } else if (transportModeSpinner.getSelectedItem().equals("Tube") && !((ComboItem) directionSpinner.getSelectedItem()).getID().equals("")) {
            isDirectionSet = true;
        } else {
            isDirectionSet = false;
        }
        setLayout();
    }

    private void setLayout() {

        if (!isTransportModeSet) {
            linearLayoutLeft.removeAllViewsInLayout();
            linearLayoutRight.removeAllViewsInLayout();
            linearLayoutLeft.addView(transportModeLabel);
            linearLayoutRight.addView(transportModeSpinner);
            addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);
        } else if (!isRouteLineSet) {
            linearLayoutLeft.removeAllViewsInLayout();
            linearLayoutRight.removeAllViewsInLayout();
            linearLayoutLeft.addView(transportModeLabel);
            linearLayoutRight.addView(transportModeSpinner);
            linearLayoutLeft.addView(routeLineLabel);
            linearLayoutRight.addView(routeLineSpinner);
            addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);

        } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
            if (!isDirectionSet) {
                linearLayoutLeft.removeAllViewsInLayout();
                linearLayoutRight.removeAllViewsInLayout();
                linearLayoutLeft.addView(transportModeLabel);
                linearLayoutRight.addView(transportModeSpinner);
                linearLayoutLeft.addView(routeLineLabel);
                linearLayoutRight.addView(routeLineSpinner);
                linearLayoutLeft.addView(directionLabel);
                linearLayoutRight.addView(directionSpinner);
                addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);
            } else if (!isStartingStopSet) {
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
                addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);
            } else {
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
                linearLayoutLeft.addView(conditionsLabel);
                linearLayoutRight.addView(conditionsSwitch);
                addToOrUpdateUserListButton.setVisibility(View.VISIBLE);
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
                addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);
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
                addToOrUpdateUserListButton.setVisibility(View.INVISIBLE);
            } else {
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
                linearLayoutLeft.addView(conditionsLabel);
                linearLayoutRight.addView(conditionsSwitch);
                addToOrUpdateUserListButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<ComboItem> fetchBusRoutes() {
        return db.getBusRoutes();
    }

    private List<ComboItem> fetchBusDirections(String busRoute) {
        return db.getBusDirections(busRoute);
    }


    private List<ComboItem> fetchBusStops(String busRoute, int busDirection) {
        return db.getBusStops(busRoute, busDirection);
    }

    private List<ComboItem> fetchTubeStations(String tubeLineID) {
        return db.getTubeStations(tubeLineID);
    }

    private List<ComboItem> fetchTubeLines() {
        return db.getTubeLines();
    }

    private synchronized List<ComboItem> fetchTubeDirectionsAndPlatforms(String tubeLineID, String tubeStationID) {
        APIFetcher apifetcher = new APIFetcher();
        apifetcher.execute(tubeLineID, tubeStationID);
        return apifetcher.getTubeDirectionsAndPlatformList();

    }


    //button
    public void addToOrUpdateAndReturnToUserListView(View view) {

        UserRouteItem userRouteItem = new UserRouteItem(transportModeSpinner.getSelectedItem().toString(), ((ComboItem) routeLineSpinner.getSelectedItem()), ((ComboItem) directionSpinner.getSelectedItem()), ((ComboItem) startingStopSpinner.getSelectedItem()), dtc, 5);

        if (!inEditMode) {
            // If not in EDIT MODE then add to List
            UserListView.userRouteValues.add(userRouteItem);
        } else {
            // Replace at position
            UserListView.userRouteValues.set(positionToRestore, userRouteItem);
        }

        setResult(RESULT_OK, null);
        finish();
    }


    public class LoadDatabase implements Runnable {
        @Override
        public void run() {
            db = new MyDatabase(getApplicationContext());
            System.out.println("Database Loaded");
        }
    }

    class APIFetcher extends AsyncTask<String, Void, Void> {

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

    //Return from conditions method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            dtc = (DayTimeConditions) data.getSerializableExtra("DayTimeConditions");
            Toast.makeText(this, "Conditions set", Toast.LENGTH_SHORT).show();
            conditionsPreviewText.setText(dtc.toString());
        } else {
            Toast.makeText(this, "No conditions set", Toast.LENGTH_SHORT).show();
            conditionsSwitch.setChecked(false);
            conditionsPreviewText.setText("");

        }
    }
}
