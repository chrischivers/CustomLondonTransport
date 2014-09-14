package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
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

import java.io.File;
import java.util.List;


public class AddNewRoute extends Activity {

    // get UserRouteValues position if any (applies if UserRouteItem is being edited)
    private boolean inEditMode = false;
    private boolean restoreInProgress = false;
    private boolean switchingMode = false;
    private int positionToRestore = -1; //-1 as default if not in edit mode;

    private Menu menu;

    private Spinner transportModeSpinner;
    private Spinner routeLineSpinner;
    private Spinner directionSpinner;
    private Spinner startingStopSpinner;
    private Spinner maxNumberSpinner;

    private ArrayAdapter<RouteLine> tubeLineAdapter;
    private ArrayAdapter<RouteLine> busRouteAdapter;
    private ArrayAdapter<CharSequence> transportModeAdapter;

    private TextView transportModeLabel;
    private TextView routeLineLabel;
    private TextView directionLabel;
    private TextView startingStopLabel;
    private TextView maxNumberLabel;
    private TextView conditionsLabel;

    private Switch conditionsSwitch;
    private TextView conditionsPreviewText;

    private LinearLayout linearLayoutLeft1;
    private LinearLayout linearLayoutRight1;
    private LinearLayout linearLayoutLeft2;
    private LinearLayout linearLayoutRight2;

    private String transportModeSelected = "";
    private RouteLine routeLineSelected;
    private Direction directionSelected;
    private StationStop startingStopSelected;
    private DayTimeConditions dayTimeConditionsSelected;
    private int maxNumberSelected;

    private boolean allFieldsValid;

    TubeLinesAndBusRoutesDatabaseFetcher fetchTubeStationsAndBusStopsByNearest;

    private Location currentLocation;
    private MyDatabase db;
    private SharedPreferences prefs;

    private boolean localModeOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_route);

        getGPSLocation();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        db = new MyDatabase(this);

        // Set LOCAL MODE from prefs
        localModeOn = (prefs.getBoolean("Local_Mode", false));

        if (getIntent().hasExtra("Position")) {
            Bundle b = getIntent().getExtras();
            positionToRestore = b.getInt("Position");
            inEditMode = true;
            restoreInProgress = true;
        }

        Button addToOrUpdateUserListButton = (Button) findViewById(R.id.routeAddToOrUpdateUserListButton);
        if (!inEditMode) {
            addToOrUpdateUserListButton.setText("Add");
        } else {
            addToOrUpdateUserListButton.setText("Update");
        }


        transportModeSpinner = (Spinner) findViewById(R.id.routeTransportModeSpinner);
        routeLineSpinner = (Spinner) findViewById(R.id.routeRouteLineSpinner);
        directionSpinner = (Spinner) findViewById(R.id.routeDirectionSpinner);
        startingStopSpinner = (Spinner) findViewById(R.id.routeStartingStopSpinner);
        maxNumberSpinner = (Spinner) findViewById(R.id.routeMaxNumberSpinner);

        transportModeLabel = (TextView) findViewById(R.id.routeTransportModeLabel);
        routeLineLabel = (TextView) findViewById(R.id.routeRouteLineLabel);
        directionLabel = (TextView) findViewById(R.id.routeDirectionLabel);
        startingStopLabel = (TextView) findViewById(R.id.routeStartingStopLabel);
        maxNumberLabel = (TextView) findViewById(R.id.routeMaxNumberLabel);
        conditionsLabel = (TextView) findViewById(R.id.routeConditionsLabel);

        conditionsSwitch = (Switch) findViewById(R.id.routeConditionsSwitch);
        conditionsPreviewText = (TextView) findViewById(R.id.routeConditionsPreviewText);

        linearLayoutLeft1 = (LinearLayout) findViewById(R.id.routeLinearLayoutLeft1);
        linearLayoutRight1 = (LinearLayout) findViewById(R.id.routeLinearLayoutRight1);
        linearLayoutLeft2 = (LinearLayout) findViewById(R.id.routeLinearLayoutLeft2);
        linearLayoutRight2 = (LinearLayout) findViewById(R.id.routeLinearLayoutRight2);

        // Populate transport mode adapter and Max Number Adapter as default first step
        transportModeAdapter = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);
        transportModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportModeSpinner.setAdapter(transportModeAdapter);
        ArrayAdapter<CharSequence> maxNumberAdapter = ArrayAdapter.createFromResource(this, R.array.max_number_array, android.R.layout.simple_spinner_item);
        maxNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maxNumberSpinner.setAdapter(maxNumberAdapter);

        // Start fetching lists on a separate thread
        fetchTubeStationsAndBusStopsByNearest = new TubeLinesAndBusRoutesDatabaseFetcher();
        fetchTubeStationsAndBusStopsByNearest.execute();

        // Restore variables if restoreInProgress
        if (restoreInProgress) {
            transportModeSelected = UserListView.userValues.get(positionToRestore).getTransportForm();
            routeLineSelected = ((UserRouteItem) UserListView.userValues.get(positionToRestore)).getRouteLine();
            startingStopSelected = UserListView.userValues.get(positionToRestore).getStartingStop();
            directionSelected = ((UserRouteItem) UserListView.userValues.get(positionToRestore)).getDirection();
            maxNumberSelected = UserListView.userValues.get(positionToRestore).getMaxNumberToShow();
            dayTimeConditionsSelected = UserListView.userValues.get(positionToRestore).getDayTimeConditions();
            if (dayTimeConditionsSelected == null) {
                conditionsSwitch.setChecked(false);
            } else {
                conditionsSwitch.setChecked(true);
            }
            maxNumberSpinner.setSelection(maxNumberSelected, false);
        }
        updateLayout();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_route_menu, menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_route_menu, menu);
        // Set initial local mode icon
        if (localModeOn) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_location_found));
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_location_off));
        }
        return true;
    }

    public void updateLayout() {
        System.out.println("Update layout called");
        linearLayoutRight1.removeAllViewsInLayout();
        linearLayoutLeft1.removeAllViewsInLayout();

        routeLineSpinner.setOnItemSelectedListener(null);
        startingStopSpinner.setOnItemSelectedListener(null);
        directionSpinner.setOnItemSelectedListener(null);

        // Add Transport Mode Spinner
        linearLayoutLeft1.addView(transportModeLabel);
        linearLayoutRight1.addView(transportModeSpinner);

        if (restoreInProgress) {
            int adapterPosition = transportModeAdapter.getPosition(transportModeSelected);
            transportModeSpinner.setSelection(adapterPosition, false);
        }

        if (transportModeSelected.equals("Tube")) {

            // With tube lines there is no local mode difference - always in alphabetical order
            if (restoreInProgress || switchingMode || tubeLineAdapter == null) {
                tubeLineAdapter = new ArrayAdapter<RouteLine>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsAndBusStopsByNearest.fetchTubeLinesOrderByAlphabetical());

            }
            tubeLineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            linearLayoutLeft1.addView(routeLineLabel);
            linearLayoutRight1.addView(routeLineSpinner);
            routeLineSpinner.setAdapter(tubeLineAdapter);
            if (routeLineSelected != null) {
                String routeLineID = routeLineSelected.getID();
                for (int i = 0; i < tubeLineAdapter.getCount(); i++) {
                    if (tubeLineAdapter.getItem(i).getID().equals(routeLineID)) {
                        routeLineSpinner.setSelection(i, false);
                        break;
                    }
                }
            }
        } else if (transportModeSelected.equals("Bus")) {
            if (restoreInProgress || switchingMode || busRouteAdapter == null) {
                if (localModeOn) {
                    busRouteAdapter = new ArrayAdapter<RouteLine>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsAndBusStopsByNearest.fetchBusRoutesOrderByNearest());
                } else {
                    busRouteAdapter = new ArrayAdapter<RouteLine>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsAndBusStopsByNearest.fetchBusRoutesOrderByAlphabetical());
                }
            }
            busRouteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            linearLayoutLeft1.addView(routeLineLabel);
            linearLayoutRight1.addView(routeLineSpinner);
            routeLineSpinner.setAdapter(busRouteAdapter);

            if (routeLineSelected != null) {

                String routeLineID = routeLineSelected.getID();
                for (int i = 0; i < busRouteAdapter.getCount(); i++) {
                    if (busRouteAdapter.getItem(i).getID().equals(routeLineID)) {
                        routeLineSpinner.setSelection(i, false);
                        break;
                    }
                }
            }
        }

        if (routeLineSelected != null) {
            if (!routeLineSelected.getID().equals("")) {
                ArrayAdapter<StationStop> startingStopAdapter;
                ArrayAdapter<Direction> directionAdapter;
                if (transportModeSelected.equals("Tube")) {
                    List<StationStop> tubeStartingStops;

                    //Start fetching Tube Starting Stops
                    TubeLinesStartingStopsFetcher fetchTubeStartingStops = new TubeLinesStartingStopsFetcher();
                    fetchTubeStartingStops.execute(routeLineSelected.getID());
                    if (localModeOn) {
                        tubeStartingStops = fetchTubeStartingStops.fetchTubeStartingStopsByNearest();
                    } else {
                        tubeStartingStops = fetchTubeStartingStops.fetchTubeStartingStopsByAlphabetical();
                    }
                    linearLayoutLeft1.addView(startingStopLabel);
                    linearLayoutRight1.addView(startingStopSpinner);
                    startingStopAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, tubeStartingStops);
                    startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    startingStopSpinner.setAdapter(startingStopAdapter);

                    if (startingStopSelected != null) {

                        String startingStopID = startingStopSelected.getID();
                        for (int i = 0; i < startingStopAdapter.getCount(); i++) {
                            if (startingStopAdapter.getItem(i).getID().equals(startingStopID)) {
                                startingStopSpinner.setSelection(i, false);
                                break;
                            }
                        }
                    }
                    if (startingStopSelected != null) {
                        if (!startingStopSelected.getID().equals("")) {
                            List<Direction> tubeDirectionsAndPlatforms;

                            // Start fetching Tube Directions
                            TubeDirectionFetcher fetchTubeDirectionsAndPlatforms = new TubeDirectionFetcher();
                            fetchTubeDirectionsAndPlatforms.execute(routeLineSelected.getID(), startingStopSelected.getID());
                            tubeDirectionsAndPlatforms = fetchTubeDirectionsAndPlatforms.getTubeDirectionsAndPlatformList();

                            linearLayoutLeft1.addView(directionLabel);
                            linearLayoutRight1.addView(directionSpinner);
                            directionAdapter = new ArrayAdapter<Direction>(getBaseContext(), android.R.layout.simple_spinner_item, tubeDirectionsAndPlatforms);
                            directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            directionSpinner.setAdapter(directionAdapter);

                            if (directionSelected != null) {
                                for (int i = 0; i < directionAdapter.getCount(); i++) {
                                    if (directionAdapter.getItem(i).toString().equals(directionSelected.toString())) {
                                        directionSpinner.setSelection(i, false);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (transportModeSelected.equals("Bus")) {
                    List<Direction> busRouteDirections;

                    //Start fetching Bus Directions
                    BusDirectionFetcher fetchBusDirections = new BusDirectionFetcher();
                    fetchBusDirections.execute(routeLineSelected.getID());
                    busRouteDirections = fetchBusDirections.fetchBusDirection();

                    linearLayoutLeft1.addView(directionLabel);
                    linearLayoutRight1.addView(directionSpinner);
                    directionAdapter = new ArrayAdapter<Direction>(getBaseContext(), android.R.layout.simple_spinner_item, busRouteDirections);
                    directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    directionSpinner.setAdapter(directionAdapter);

                    if (directionSelected != null) {

                        int directionID = directionSelected.getID();
                        System.out.println("DirectionID: " + directionID);
                        for (int i = 0; i < directionAdapter.getCount(); i++) {
                            if (directionAdapter.getItem(i).getID() == directionID) {
                                directionSpinner.setSelection(i, false);
                                break;
                            }
                        }
                    }
                    if (directionSelected != null) {
                        if (directionSelected.getID() != 0) {

                            List<StationStop> busStartingStops;

                            //Start fetching Tube Starting Stops
                            BusRouteStartingStopsFetcher fetchBusStartingStops = new BusRouteStartingStopsFetcher();
                            fetchBusStartingStops.execute(routeLineSelected.getID(), Integer.toString(directionSelected.getID()));
                            if (localModeOn) {
                                busStartingStops = fetchBusStartingStops.fetchBusStartingStopsByNearest();
                            } else {
                                busStartingStops = fetchBusStartingStops.fetchBusStartingStopsByAlphabetical();
                            }
                            linearLayoutLeft1.addView(startingStopLabel);
                            linearLayoutRight1.addView(startingStopSpinner);
                            startingStopAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, busStartingStops);
                            startingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            startingStopSpinner.setAdapter(startingStopAdapter);

                            if (startingStopSelected != null) {

                                String startingStopID = startingStopSelected.getID();
                                for (int i = 0; i < startingStopAdapter.getCount(); i++) {
                                    if (startingStopAdapter.getItem(i).getID().equals(startingStopID)) {
                                        startingStopSpinner.setSelection(i, false);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        updateLayoutWithMaxNumberAndConditions();
        setListeners();
        restoreInProgress = false;
        switchingMode = false;
    }

    private void updateLayoutWithMaxNumberAndConditions() {
        linearLayoutLeft2.removeAllViewsInLayout();
        linearLayoutRight2.removeAllViewsInLayout();
        if (transportModeSelected.equals("Tube") && directionSelected != null) {
            if (!directionSelected.getLabel().equals("")) {
                linearLayoutLeft2.addView(maxNumberLabel);
                linearLayoutRight2.addView(maxNumberSpinner);
                linearLayoutLeft2.addView(conditionsLabel);
                linearLayoutRight2.addView(conditionsSwitch);
                if (dayTimeConditionsSelected != null) {
                    conditionsPreviewText.setText(dayTimeConditionsSelected.toString());
                }
                allFieldsValid = true;
            }
        } else if (transportModeSelected.equals("Bus") && startingStopSelected != null) {
            if (!startingStopSelected.getID().equals("")) {
                linearLayoutLeft2.addView(maxNumberLabel);
                linearLayoutRight2.addView(maxNumberSpinner);
                linearLayoutLeft2.addView(conditionsLabel);
                linearLayoutRight2.addView(conditionsSwitch);
                conditionsPreviewText.setText("");
                allFieldsValid = true;
            }
        }
        else {
            linearLayoutLeft2.removeAllViewsInLayout();
            linearLayoutRight2.removeAllViewsInLayout();
            allFieldsValid = false;
        }
        linearLayoutLeft2.postInvalidate();
        linearLayoutRight2.postInvalidate();
    }

    public void setListeners() {
        transportModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                transportModeSelected = ((String) adapterView.getItemAtPosition(i));
                routeLineSelected = null;
                updateLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        routeLineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                routeLineSelected = ((RouteLine) adapterView.getItemAtPosition(i));
                directionSelected = null;
                startingStopSelected = null;
                updateLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        startingStopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                startingStopSelected = ((StationStop) adapterView.getItemAtPosition(i));
                updateLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        directionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                directionSelected = ((Direction) adapterView.getItemAtPosition(i));
                updateLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        conditionsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loadSetDayTimeConditions();
                } else {
                    dayTimeConditionsSelected = null;
                    conditionsPreviewText.setText("");
                }
            }
        });

        maxNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maxNumberSelected = parent.getSelectedItemPosition()-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                maxNumberSelected = -1;
            }
        });

    }

    public void loadSetDayTimeConditions() {
        Intent intent = new Intent(this, SetDayTimeConditions.class);
        startActivityForResult(intent, 1);
    }

    //button
    public void addToOrUpdateAndReturnToUserListView(View view) {
        if (allFieldsValid) {
            UserRouteItem userRouteItem;
            int maxNumberToFetch = maxNumberSpinner.getSelectedItemPosition(); //0 = all
            userRouteItem = new UserRouteItem(transportModeSelected, routeLineSelected, directionSelected, startingStopSelected, dayTimeConditionsSelected, maxNumberToFetch);
            saveCustomSettingsToPrefs(); //Save custom prefs
            if (!inEditMode) {
                // If not in EDIT MODE then add to List
                UserListView.userValues.add(userRouteItem);
            } else {
                // Replace at position
                UserListView.userValues.set(positionToRestore, userRouteItem);
            }

            setResult(RESULT_OK, null);
            finish();
        } else {
            Toast.makeText(this, "Please select a valid stop or station from the drop down list", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reset_route) {
            transportModeSpinner.setSelection(0,false);
            transportModeSelected = "";
            transportModeSpinner.setSelection(0);
            routeLineSelected = null;
            directionSelected = null;
            startingStopSelected = null;
            conditionsSwitch.setChecked(false);
            conditionsPreviewText.setText("");
            maxNumberSpinner.setSelection(0);
            maxNumberSelected = 0;
            updateLayout();
            return true;
        } else if (id == R.id.local_mode_toggle) {
            switchingMode = true;
            if (localModeOn) {
                localModeOn = false;
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_location_off));
                updateLayout();
            } else {
                localModeOn = true;
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_location_found));
                updateLayout();
            }
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    //Return from conditions method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            dayTimeConditionsSelected = (DayTimeConditions) data.getSerializableExtra("DayTimeConditions");
            Toast.makeText(this, "Conditions set", Toast.LENGTH_SHORT).show();
            conditionsPreviewText.setText(dayTimeConditionsSelected.toString());
        } else {
            Toast.makeText(this, "No conditions set", Toast.LENGTH_SHORT).show();
            conditionsSwitch.setChecked(false);
            conditionsPreviewText.setText("");

        }
    }

    public void saveCustomSettingsToPrefs() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("Local_Mode", localModeOn);
        editor.apply();
    }

    public void getGPSLocation() {
        GPSTracker gps = new GPSTracker(AddNewRoute.this);

        // check if GPS enabled
        if(gps.canGetLocation()){
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
         } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }
    }

    /*private void developerTubeDirectionDump() {
        for (RouteLine routeLine : db.getTubeLinesAlphabetical()) {
            for (StationStop stationStop : db.getTubeStationsAlphabetical(routeLine.getID())) {
                TubeDirectionFetcher fetchTubeDirectionsAndPlatforms = new TubeDirectionFetcher();
                fetchTubeDirectionsAndPlatforms.execute(routeLine.getID(), stationStop.getID());
                for (Direction direction :  fetchTubeDirectionsAndPlatforms.getTubeDirectionsAndPlatformList()) {
                    System.out.print(routeLine.getID()+",");
                    System.out.print(stationStop.getID()+",");
                    System.out.print(direction.getID()+",");
                    System.out.print(direction.getLabel()+",");
                    System.out.println(direction.getLine());
                }

            }
        }
    }*/




    class TubeDirectionFetcher extends AsyncTask<String, Void, Void> {
    //strings[0] is TubeLineID, strings[1] is TubeStationID
        List<Direction> tubeDirectionsAndPlatformList;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            tubeDirectionsAndPlatformList = null;
            tubeDirectionsAndPlatformList = db.getTubeDirections(strings[0], strings[1]);
            notify();
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

    class TubeLinesAndBusRoutesDatabaseFetcher extends AsyncTask<String, Void, Void> {

        private List<RouteLine> tubeLines;
        private List<RouteLine> busRoutesOrderByNearest;
        private List<RouteLine> busRoutesOrderByAlphabetical;

        @Override
        protected synchronized Void doInBackground(String... strings) {

            tubeLines = null;
            tubeLines = db.getTubeLinesAlphabetical();
            tubeLines.add(0, new RouteLine());//insert empty to front
            notify();

            busRoutesOrderByAlphabetical = null;
            busRoutesOrderByAlphabetical = db.getBusRoutesAlphabetical();
            busRoutesOrderByAlphabetical.add(0, new RouteLine());//insert empty to front)
            notify();

            busRoutesOrderByNearest = null;
            busRoutesOrderByNearest = db.getNearestBusRoutes(currentLocation);
            busRoutesOrderByNearest.add(0, new RouteLine());//insert empty to front)
            notify();

            return null;
        }

        public synchronized List<RouteLine> fetchTubeLinesOrderByAlphabetical() {
            while (tubeLines == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeLines;
        }


        public synchronized List<RouteLine> fetchBusRoutesOrderByNearest() {
            while (busRoutesOrderByNearest == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busRoutesOrderByNearest;
        }

        public synchronized List<RouteLine> fetchBusRoutesOrderByAlphabetical() {
            while (busRoutesOrderByAlphabetical == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busRoutesOrderByAlphabetical;
        }


    }

    class TubeLinesStartingStopsFetcher extends AsyncTask<String, Void, Void> {

        private List<StationStop> tubeStartingStopsAlphabetical;
        private List<StationStop> tubeStartingStopsNearest;

        @Override
        protected synchronized Void doInBackground(String... strings) {

            tubeStartingStopsAlphabetical = null;
            tubeStartingStopsAlphabetical = db.getTubeStationsAlphabetical(strings[0]);
            tubeStartingStopsAlphabetical.add(0, new StationStop());//insert empty to front
            notify();

            tubeStartingStopsNearest = null;
            tubeStartingStopsNearest = db.getTubeStationsbyNearest(currentLocation, strings[0]);
            tubeStartingStopsNearest.add(0, new StationStop());//insert empty to front
            notify();

            return null;
        }

        public synchronized List<StationStop> fetchTubeStartingStopsByAlphabetical() {
            while (tubeStartingStopsAlphabetical == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeStartingStopsAlphabetical;
        }


        public synchronized List<StationStop> fetchTubeStartingStopsByNearest() {
            while (tubeStartingStopsNearest == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeStartingStopsNearest;
        }
    }

    class BusDirectionFetcher extends AsyncTask<String, Void, Void> {

        private List<Direction> busDirections;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            busDirections = null;
            busDirections = db.getBusDirections(strings[0]);
            busDirections.add(0, new Direction());//insert empty to front
            notify();

            return null;
        }

        public synchronized List<Direction> fetchBusDirection() {
            while (busDirections == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busDirections;
        }
    }

    class BusRouteStartingStopsFetcher extends AsyncTask<String, Void, Void> {

        private List<StationStop> busStartingStopsAlphabetical;
        private List<StationStop> busStartingStopsNearest;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            //strings[0] is busRoute ID, strings[1] is busDirection ID
            busStartingStopsAlphabetical = null;
            busStartingStopsAlphabetical = db.getBusStopsForRouteAlphabetical(strings[0], Integer.parseInt(strings[1]));
            busStartingStopsAlphabetical.add(0, new StationStop());//insert empty to front
            notify();

            busStartingStopsNearest = null;
            busStartingStopsNearest = db.getBusStopsForRouteNearest(strings[0],Integer.parseInt(strings[1]),currentLocation);
            busStartingStopsNearest.add(0, new StationStop());//insert empty to front
            notify();

            return null;
        }

        public synchronized List<StationStop> fetchBusStartingStopsByAlphabetical() {
            while (busStartingStopsAlphabetical == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busStartingStopsAlphabetical;
        }


        public synchronized List<StationStop> fetchBusStartingStopsByNearest() {
            while (busStartingStopsNearest == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busStartingStopsNearest;
        }
    }

}
