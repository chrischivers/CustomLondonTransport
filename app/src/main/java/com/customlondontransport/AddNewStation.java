package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class AddNewStation extends Activity {

    // get UserRouteValues position if any (applies if UserRouteItem is being edited)
    private boolean inEditMode = false;
    private int positionToRestore = -1; //-1 as default if not in edit mode;


    private TextView transportModeLabel;
    private Spinner transportModeSpinner;

    private TextView stopCodeLabel;
    private EditText stopCodeEditText;

    private TextView stationLabel;
    private Spinner stationSpinner;

    private TextView maxNumberLabel;
    private Spinner maxNumberSpinner;

    private TextView conditionsLabel;
    private Switch conditionsSwitch;
    private TextView conditionsPreviewText;

    private ArrayAdapter<StationStop> stationAdapter;


    private LinearLayout linearLayoutLeft1;
    private LinearLayout linearLayoutRight1;
    private LinearLayout linearLayoutLeft2;
    private LinearLayout linearLayoutRight2;


    private Button addToOrUpdateUserListButton;

    private MyDatabase db;

    private boolean isTransportModeSet = false;
    private boolean isStationSet = false;
    private boolean isStopsSet = false;

    private DayTimeConditions dtc;

    private Location currentLocation;
    private SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_station);

        getGPSLocation();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (getIntent().hasExtra("Position")) {
            Bundle b = getIntent().getExtras();
            positionToRestore = b.getInt("Position");
            inEditMode = true;
        }

        // pull in the database
        new Thread(new LoadDatabase()).run();

        addToOrUpdateUserListButton = (Button) findViewById(R.id.stationAddToOrUpdateUserListButton);
        if (!inEditMode) {
            addToOrUpdateUserListButton.setText("Add");
        } else {
            addToOrUpdateUserListButton.setText("Update");
        }

        transportModeSpinner = (Spinner) findViewById(R.id.stationTransportModeSpinner);
        stationSpinner = (Spinner) findViewById(R.id.stationStationSpinner);
        maxNumberSpinner = (Spinner) findViewById(R.id.stationMaxNumberSpinner);
        stopCodeEditText = (EditText) findViewById(R.id.stationStopCodeEditText);

        transportModeLabel = (TextView) findViewById(R.id.stationTransportModeLabel);
        stationLabel = (TextView) findViewById(R.id.stationStationLabel);
        stopCodeLabel = (TextView) findViewById(R.id.stationStopCodeLabel);
        maxNumberLabel = (TextView) findViewById(R.id.stationMaxNumberLabel);
        conditionsLabel = (TextView) findViewById(R.id.stationConditionsLabel);

        conditionsSwitch = (Switch) findViewById(R.id.stationConditionsSwitch);
        conditionsPreviewText = (TextView) findViewById(R.id.stationConditionsPreviewText);

        linearLayoutLeft1 = (LinearLayout) findViewById(R.id.stationLinearLayoutLeft1);
        linearLayoutRight1 = (LinearLayout) findViewById(R.id.stationLinearLayoutRight1);
        linearLayoutLeft2 = (LinearLayout) findViewById(R.id.stationLinearLayoutLeft2);
        linearLayoutRight2 = (LinearLayout) findViewById(R.id.stationLinearLayoutRight2);

        // Populate transport mode adapter and Max Number Adapter as default first step
        ArrayAdapter<CharSequence> transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> maxNumberAdapter = ArrayAdapter.createFromResource(this, R.array.max_number_array, android.R.layout.simple_spinner_item);

        linearLayoutLeft1.removeAllViewsInLayout();
        linearLayoutRight1.removeAllViewsInLayout();
        linearLayoutLeft1.addView(transportModeLabel);
        linearLayoutRight1.addView(transportModeSpinner);

        //TODO
        //setLayout();

        transportModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportModeSpinner.setAdapter(transportModeAdapter);

        maxNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxNumberSpinner.setAdapter(maxNumberAdapter);

        // Adjust transport spinner if in EDIT MODE
        if (inEditMode) {
            String transportMode = UserListView.userRouteValues.get(positionToRestore).getTransportForm();
            int adapterPosition = transportModeAdapter.getPosition(transportMode);
            transportModeSpinner.setSelection(adapterPosition);
            onTransportModeSpinnerChange();
        }
        // Set MaxNumber if in EDIT MODE
        if (inEditMode) {
           maxNumberSpinner.setSelection(UserListView.userRouteValues.get(positionToRestore).getMaxNumberToShow()); //add 1 to translate into spinner values
        }

        // Set conditions if in EDIT MODE
        if (inEditMode) {
            dtc = UserListView.userRouteValues.get(positionToRestore).getDayTimeConditions();
            if (dtc == null) {
                conditionsSwitch.setChecked(false);
            } else {
                conditionsSwitch.setChecked(true);
            }
        }


        transportModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                onTransportModeSpinnerChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isTransportModeSet = false;
               // setLayout();
            }
        });

        stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //onStationSpinnerChange();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                isStationSet = false;
                //setLayout();
            }
        });
        stopCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    //onStopCodeEditTextConfirm();
                }
                return true;

            }
        });



        conditionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadSetDayTimeConditions();
                } else {
                    dtc = null;
                    conditionsPreviewText.setText("");
                }
            }
        });


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_station_menu, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_station_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_station) {
            isTransportModeSet = false;
           // setLayout();
            conditionsSwitch.setChecked(false);
            conditionsPreviewText.setText("");
            transportModeSpinner.setSelection(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadSetDayTimeConditions() {
        Intent intent = new Intent(this, SetDayTimeConditions.class);
        startActivityForResult(intent, 1);
    }

    public void onTransportModeSpinnerChange() {
        isStationSet = false;
        isStopsSet = false;

        if (transportModeSpinner.getSelectedItem().equals("Tube")) {
            stationAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsOrderByNearest());
            stationAdapter.insert(new StationStop(),0);//insert empty to front
            stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stationSpinner.setAdapter(stationAdapter);
            isTransportModeSet = true;

        } else if (transportModeSpinner.getSelectedItem().equals("Bus")) {
            stationAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStopsOrderByNearest());
            stationAdapter.insert(new StationStop(),0);//insert empty to front
            stationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stationSpinner.setAdapter(stationAdapter);
            isTransportModeSet = true;

        } else {
            isTransportModeSet = false;
        }

        /*/ IF IN EDIT MODE
        if (inEditMode) {
            String routeLineID = UserListView.userRouteValues.get(positionToRestore).getRouteLine().getID();
            for (int i = 0; i < routeLineAdapter.getCount(); i++) {
                if (routeLineAdapter.getItem(i).getID().equals(routeLineID)) {
                    routeLineSpinner.setSelection(i, true);
                    break;
                }
            }
            onRouteLineSpinnerChange();
        }*/
       // setLayout();
    }
/*
    public void onRouteLineSpinnerChange() {
        isStartingStopSet = false;
        isDirectionSet = false;

        if (transportModeSpinner.getSelectedItem().equals("Tube") && !((RouteLine) routeLineSpinner.getSelectedItem()).getID().equals("")) {
            startingStopAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStations(((RouteLine) routeLineSpinner.getSelectedItem()).getID()));
            startingStopAdapter.insert(new StationStop(),0); //insert empty to front
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

        } else if (transportModeSpinner.getSelectedItem().equals("Bus") && !((RouteLine) routeLineSpinner.getSelectedItem()).getID().equals("")) {
            directionAdapter = new ArrayAdapter<Direction>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusDirections(((RouteLine) routeLineSpinner.getSelectedItem()).getID()));
            directionAdapter.insert(new Direction(),0); //insert empty to front
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
        if (transportModeSpinner.getSelectedItem().equals("Tube") && !((StationStop) startingStopSpinner.getSelectedItem()).getID().equals("")) {
            directionAdapter = new ArrayAdapter<Direction>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeDirectionsAndPlatforms(((RouteLine) routeLineSpinner.getSelectedItem()).getID(), ((StationStop) startingStopSpinner.getSelectedItem()).getID()));
            directionAdapter.insert(new Direction(),0); //insert empty to front
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

        } else
            isStartingStopSet = transportModeSpinner.getSelectedItem().equals("Bus") && !((StationStop) startingStopSpinner.getSelectedItem()).getID().equals("");
        setLayout();
    }

    public void onDirectionSpinnerChange() {
        if (transportModeSpinner.getSelectedItem().equals("Bus") && !((Direction) directionSpinner.getSelectedItem()).getID().equals("")) {
            startingStopAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchBusStops(routeLineSpinner.getSelectedItem().toString(), Integer.parseInt(((Direction) directionSpinner.getSelectedItem()).getID())));
            startingStopAdapter.insert(new StationStop(),0); //insert empty to front
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
        } else
            isDirectionSet = transportModeSpinner.getSelectedItem().equals("Tube") && !((Direction) directionSpinner.getSelectedItem()).getID().equals("");
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
                linearLayoutLeft.addView(maxNumberLabel);
                linearLayoutRight.addView(maxNumberSpinner);
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
                linearLayoutLeft.addView(maxNumberLabel);
                linearLayoutRight.addView(maxNumberSpinner);
                addToOrUpdateUserListButton.setVisibility(View.VISIBLE);
            }
        }
    }*/

    private List<StationStop> fetchBusStopsOrderByNearest() {
        return db.getAllBusStopsOrderByNearest(currentLocation);
    }

    private List<StationStop> fetchTubeStationsOrderByNearest() {
        return db.getAllTubeStationsByNearest(currentLocation);
    }

    public List<StationStop> sortStationsByNearest(List<StationStop> list) {
        Collections.sort(list, new Comparator<StationStop>(){

            @Override
            public int compare(StationStop lhs, StationStop rhs) {
                if (lhs.getLocation().distanceTo(currentLocation) > rhs.getLocation().distanceTo(currentLocation)) {
                    return 1;
                } else if (lhs.getLocation().distanceTo(currentLocation) < rhs.getLocation().distanceTo(currentLocation)) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return list;
    }

/*
    //button
    public void addToOrUpdateAndReturnToUserListView(View view) {
        int maxNumberToFetch = maxNumberSpinner.getSelectedItemPosition(); //0 = all
        UserRouteItem userRouteItem = new UserRouteItem(transportModeSpinner.getSelectedItem().toString(), ((RouteLine) routeLineSpinner.getSelectedItem()), ((Direction) directionSpinner.getSelectedItem()), ((StationStop) startingStopSpinner.getSelectedItem()), dtc, maxNumberToFetch);
        saveCustomSettingsToPrefs(); //Save custom prefs
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
*/

    public class LoadDatabase implements Runnable {
        @Override
        public void run() {
            db = new MyDatabase(getApplicationContext());
            System.out.println("Database Loaded");
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

    public void getGPSLocation() {
        GPSTracker gps = new GPSTracker(AddNewStation.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());

            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + currentLocation.getLatitude() + "\nLong: " + currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }

        //TODO
        //For emulator testing GPS being set manually
        currentLocation.setLatitude(51.465053721837);
        currentLocation.setLongitude(-0.29280117154);
    }

    class APIFetcher extends AsyncTask<String, Void, Void> {

        List<Direction> tubeDirectionsAndPlatformList;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            tubeDirectionsAndPlatformList = null;
            tubeDirectionsAndPlatformList = (new APIInterface().fetchTubeDirectionsAndPlatforms(strings[0], strings[1]));
            notifyAll();
            return null;
        }

        public synchronized List<Direction> getTubeDirectionsAndPlatformList() {
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
