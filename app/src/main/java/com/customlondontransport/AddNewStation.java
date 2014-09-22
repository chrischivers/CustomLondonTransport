package com.customlondontransport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AddNewStation extends Activity {

    // get UserRouteValues position if any (applies if UserRouteItem is being edited)
    private boolean inEditMode = false;
    private boolean restoreInProgress = false;
    private boolean switchingMode = false;
    private int positionToRestore = -1; //-1 as default if not in edit mode;

    private Menu menu;

    private TextView transportModeLabel;
    private Spinner transportModeSpinner;

    private TextView stopCodeLabel;
    private AutoCompleteTextView stopCodeEditText;

    private TextView stationLabel;
    private Spinner stationSpinner;

    private TextView maxNumberLabel;
    private Spinner maxNumberSpinner;

    private TextView conditionsLabel;
    private Switch conditionsSwitch;
    private TextView conditionsPreviewText;

    private ArrayAdapter<StationStop> tubeStationAdapter;
    private ArrayAdapter<StationStop> busStopAdapter;
    private ArrayAdapter<CharSequence> transportModeAdapter;

    private LinearLayout linearLayoutLeft1;
    private LinearLayout linearLayoutRight1;
    private LinearLayout linearLayoutLeft2;
    private LinearLayout linearLayoutRight2;
    private LinearLayout linearLayoutRouteGrid;

    private String transportModeSelected = "";
    private List<Direction> dynamicCheckBoxesTubeArray;
    private List<String> dynamicCheckBoxesBusRouteArray;
    private StationStop stationStopSelected; //keep track!
    private DayTimeConditions dayTimeConditionsSelected;
    private int maxNumberSelected;

    private boolean allFieldsValid;

    TubStationsAndBusStopsDatabaseFetcher fetchTubeStationsAndBusStopsByNearest;

    private Location currentLocation;
    private MyDatabase db;
    private SharedPreferences prefs;

    private boolean localModeOn;

    private int screenWidth;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_station);

        //Get display dimensions
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = metrics.widthPixels;
        imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);

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

        dynamicCheckBoxesTubeArray = new ArrayList<Direction>();
        dynamicCheckBoxesBusRouteArray = new ArrayList<String>();



        // Set button text
        Button addToOrUpdateUserListButton = (Button) findViewById(R.id.stationAddToOrUpdateUserListButton);
        if (!inEditMode) {
            addToOrUpdateUserListButton.setText("Add");
        } else {
            addToOrUpdateUserListButton.setText("Update");
        }


        transportModeSpinner = (Spinner) findViewById(R.id.stationTransportModeSpinner);
        stationSpinner = (Spinner) findViewById(R.id.stationStationSpinner);
        maxNumberSpinner = (Spinner) findViewById(R.id.stationMaxNumberSpinner);
        stopCodeEditText = (AutoCompleteTextView) findViewById(R.id.stationStopCodeEditText);

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
        linearLayoutRouteGrid = (LinearLayout) findViewById(R.id.stationLinearLayoutRouteGrid);

        // Populate transport mode adapter and Max Number Adapter as default first step
        transportModeAdapter  = ArrayAdapter.createFromResource(this, R.array.transport_mode_array, android.R.layout.simple_spinner_item);
        transportModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        transportModeSpinner.setAdapter(transportModeAdapter);
        transportModeSpinner.setSelection(0, false);
        ArrayAdapter<CharSequence> maxNumberAdapter = ArrayAdapter.createFromResource(this, R.array.max_number_array, android.R.layout.simple_spinner_item);
        maxNumberSpinner.setAdapter(maxNumberAdapter);

        // Start fetching lists on a separate thread
        fetchTubeStationsAndBusStopsByNearest = new TubStationsAndBusStopsDatabaseFetcher();
        fetchTubeStationsAndBusStopsByNearest.execute();


        // Restore variables if restoreInProgress
        if (restoreInProgress) {
            transportModeSelected = UserListView.userValues.get(positionToRestore).getTransportForm();
            if (transportModeSelected.equals("Bus")) {
                dynamicCheckBoxesBusRouteArray = (List<String>) ((UserStationItem) UserListView.userValues.get(positionToRestore)).getRouteLineList();
            } else if (transportModeSelected.equals("Tube")) {
                dynamicCheckBoxesTubeArray = (List<Direction>) ((UserStationItem) UserListView.userValues.get(positionToRestore)).getRouteLineList();
            }
            stationStopSelected = UserListView.userValues.get(positionToRestore).getStartingStop();
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
        inflater.inflate(R.menu.add_new_station_menu, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_new_station_menu, menu);

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

        linearLayoutLeft1.removeAllViewsInLayout();
        linearLayoutRight1.removeAllViewsInLayout();
        linearLayoutLeft2.removeAllViewsInLayout();
        linearLayoutRight2.removeAllViewsInLayout();

        linearLayoutRouteGrid.removeAllViewsInLayout();

        stationSpinner.setOnItemSelectedListener(null);

        //Add Transport Mode Spinner
        linearLayoutLeft1.addView(transportModeLabel);
        linearLayoutRight1.addView(transportModeSpinner);


        if (restoreInProgress) {
            int adapterPosition = transportModeAdapter.getPosition(transportModeSelected);
            transportModeSpinner.setSelection(adapterPosition,false);
        }

        if (transportModeSelected.equals("Tube")) {
            stationLabel.setText("Tube station (closest first)");
            // Only load adapter if null
            if (restoreInProgress || switchingMode || tubeStationAdapter == null) {
                tubeStationAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsAndBusStopsByNearest.fetchTubeStationsOrderByNearest());
                tubeStationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            if (localModeOn) {
                linearLayoutLeft1.addView(stationLabel);
                linearLayoutRight1.addView(stationSpinner);
                stationSpinner.setAdapter(tubeStationAdapter);
                if (stationStopSelected != null) {
                    String stationID = stationStopSelected.getID();
                    for (int i = 0; i < tubeStationAdapter.getCount(); i++) {
                        if (tubeStationAdapter.getItem(i).getID().equals(stationID)) {
                            stationSpinner.setSelection(i, false);
                            break;
                        }
                    }
                }
            } else {
                System.out.println("Here to add");
                linearLayoutLeft1.addView(stopCodeLabel);
                linearLayoutRight1.addView(stopCodeEditText);
                stopCodeEditText.setAdapter(tubeStationAdapter);
                stopCodeLabel.setText("Enter tube station name");

                if (stationStopSelected != null) {
                    StationStop temporaryStationStop = stationStopSelected;
                    stopCodeEditText.setText(stationStopSelected.toString());
                    stopCodeEditText.dismissDropDown();
                    stopCodeEditText.performClick();
                    stationStopSelected = temporaryStationStop;
                }
            }

        } else if (transportModeSelected.equals("Bus")) {
            stationLabel.setText("Bus stop (closest first)");
          if (restoreInProgress || switchingMode || busStopAdapter == null) {
              busStopAdapter = new ArrayAdapter<StationStop>(getBaseContext(), android.R.layout.simple_spinner_item, fetchTubeStationsAndBusStopsByNearest.fetchBusStopsOrderByNearest());
              busStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
          }
            if (localModeOn) {
                linearLayoutLeft1.addView(stationLabel);
                linearLayoutRight1.addView(stationSpinner);

                stationSpinner.setAdapter(busStopAdapter);

                if (stationStopSelected != null) {

                    String stationID = stationStopSelected.getID();
                    for (int i = 0; i < busStopAdapter.getCount(); i++) {
                        if (busStopAdapter.getItem(i).getID().equals(stationID)) {
                            stationSpinner.setSelection(i, false);
                            break;
                        }
                    }
                }
            } else {
                linearLayoutLeft1.addView(stopCodeLabel);
                linearLayoutRight1.addView(stopCodeEditText);
                stopCodeEditText.setAdapter(busStopAdapter);
                stopCodeLabel.setText("Enter bus stop name or code");

                if (stationStopSelected != null) {
                    StationStop temporaryStationStop = stationStopSelected;
                    stopCodeEditText.setText(stationStopSelected.toString());
                    stopCodeEditText.dismissDropDown();
                    stopCodeEditText.performClick();
                    stationStopSelected = temporaryStationStop;
                }
            }
        }



        if (stationStopSelected != null) {
            if (!stationStopSelected.getID().equals("")) {
                if (transportModeSelected.equals("Tube")) {

                    List<Direction> tubeDirectionsStations;

                    // Start fetching Tube Directions and Stations
                    TubeLinesAndDirectionsByPlatformDatabaseFetcher fetchTubeLinesDirectionsPlatformsByStation = new TubeLinesAndDirectionsByPlatformDatabaseFetcher();
                    fetchTubeLinesDirectionsPlatformsByStation.execute(stationStopSelected.getID());
                    tubeDirectionsStations = fetchTubeLinesDirectionsPlatformsByStation.fetchTubeLinesDirectionsPlatformsByStation();

                    int numberTubeDirectionsStations = tubeDirectionsStations.size();
                    int numberColumns = 2;
                    int maxRoutesPerColumn = (numberTubeDirectionsStations / numberColumns) + 1;
                    int recordNumber = 0;

                    for (int i = 0; i < numberColumns; i++) {
                        int columnCounter = 0;
                        LinearLayout column = new LinearLayout(getApplicationContext());
                        column.setOrientation(LinearLayout.VERTICAL);
                        column.setMinimumWidth(screenWidth / numberColumns);
                        while (columnCounter < maxRoutesPerColumn && recordNumber < numberTubeDirectionsStations) {
                            final CheckBox cb = new CheckBox(getApplicationContext());
                            cb.setText(tubeDirectionsStations.get(recordNumber).toString());
                            cb.setTextColor(Color.BLACK);
                            cb.setId(recordNumber);
                            cb.setMaxWidth(screenWidth / numberColumns);

                            final int finalRecordNumber = recordNumber;

                            if (restoreInProgress || switchingMode) {
                                for (Direction direction : dynamicCheckBoxesTubeArray) {
                                    if (direction.getLabel().equals(tubeDirectionsStations.get(finalRecordNumber).getLabel())) {
                                        cb.setChecked(true);
                                    }
                                }
                            }

                            // Set check box listener to update dynamicCheckBoxesBusRouteArray
                            final List<Direction> finalTubeDirectionsStations = tubeDirectionsStations;
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        dynamicCheckBoxesTubeArray.add(new Direction(cb.getId(), finalTubeDirectionsStations.get(finalRecordNumber).getLabel(), finalTubeDirectionsStations.get(finalRecordNumber).getLine()));
                                    } else {
                                        for (int i = 0; i < dynamicCheckBoxesTubeArray.size(); i++) {
                                            if (dynamicCheckBoxesTubeArray.get(i).getID() == cb.getId()) {
                                                dynamicCheckBoxesTubeArray.remove(i);
                                            }
                                        }
                                    }
                                    updateLayoutWithMaxNumberAndConditions();
                                }
                            });
                            column.addView(cb);
                            columnCounter++;
                            recordNumber++;
                        }
                        linearLayoutRouteGrid.addView(column);
                    }

                } else if (transportModeSelected.equals("Bus")) {

                    System.out.println("Station Stop Selected: " + stationStopSelected);
                    List<RouteLine> busRoutes;

                    // Start fetching Bus routes by Stop ID
                    BusRoutesByStopFetcher fetchBusRoutesByStop = new BusRoutesByStopFetcher();
                    fetchBusRoutesByStop.execute(stationStopSelected.getID());
                    busRoutes = fetchBusRoutesByStop.fetchBusRoutesByStop();
                    System.out.println(busRoutes.size());

                    int numberBusRoutes = busRoutes.size();
                    int numberColumns = 3;
                    int maxRoutesPerColumn = (numberBusRoutes / numberColumns) + 1;
                    int recordNumber = 0;

                    for (int i = 0; i < numberColumns; i++) {
                        int columnCounter = 0;
                        LinearLayout column = new LinearLayout(getApplicationContext());
                        column.setOrientation(LinearLayout.VERTICAL);
                        column.setMinimumWidth(screenWidth / numberColumns);
                        while (columnCounter < maxRoutesPerColumn && recordNumber < numberBusRoutes) {
                            final CheckBox cb = new CheckBox(getApplicationContext());
                            cb.setText(busRoutes.get(recordNumber).getID());
                            cb.setTextColor(Color.BLACK);
                            cb.setMaxWidth(screenWidth / numberColumns);

                            if (restoreInProgress || switchingMode) {
                                if (dynamicCheckBoxesBusRouteArray.contains(busRoutes.get(recordNumber).getID())) {
                                    cb.setChecked(true);
                                }
                            }
                            final int finalRecordNumber = recordNumber;

                            // Set check box listener to update dynamicCheckBoxesBusRouteArray
                            final List<RouteLine> finalBusRoutes = busRoutes;
                            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        dynamicCheckBoxesBusRouteArray.add((finalBusRoutes.get(finalRecordNumber).getID()));
                                    } else {
                                        dynamicCheckBoxesBusRouteArray.remove(dynamicCheckBoxesBusRouteArray.indexOf(cb.getText()));
                                    }

                                    updateLayoutWithMaxNumberAndConditions();
                                }
                            });
                            column.addView(cb);
                            columnCounter++;
                            recordNumber++;
                        }
                        linearLayoutRouteGrid.addView(column);

                        System.out.println("Child count:" + linearLayoutRouteGrid.getChildCount());

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
        if (dynamicCheckBoxesBusRouteArray.size() > 0 || dynamicCheckBoxesTubeArray.size() > 0) {
                linearLayoutLeft2.addView(maxNumberLabel);
                linearLayoutRight2.addView(maxNumberSpinner);
                linearLayoutLeft2.addView(conditionsLabel);
                linearLayoutRight2.addView(conditionsSwitch);
            if (dayTimeConditionsSelected != null) {
                conditionsPreviewText.setText(dayTimeConditionsSelected.toString());
            }
                allFieldsValid = true;
            } else {
                linearLayoutLeft2.removeAllViewsInLayout();
                linearLayoutRight2.removeAllViewsInLayout();
            conditionsPreviewText.setText("");
                allFieldsValid = false;
        }
        linearLayoutLeft2.postInvalidate();
        linearLayoutRight2.postInvalidate();
    }

    public void setListeners() {
        transportModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                transportModeSelected = ((String) parent.getItemAtPosition(position));
                stationStopSelected = null;
                stopCodeEditText.setText(null);
                dynamicCheckBoxesBusRouteArray.clear();
                dynamicCheckBoxesTubeArray.clear();

                updateLayout();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        stationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // Do not fire if the empty record is selected
                System.out.println("Station Spinner Listener being fired");
                if (((StationStop) adapterView.getItemAtPosition(i)).getID().equals(""))  {
                    stationStopSelected = null;
                    linearLayoutRouteGrid.removeAllViewsInLayout();
                } else {
                    stationStopSelected = ((StationStop) adapterView.getItemAtPosition(i));
                    updateLayout();
                }
                dynamicCheckBoxesTubeArray.clear();
                dynamicCheckBoxesBusRouteArray.clear();
                updateLayoutWithMaxNumberAndConditions();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        stopCodeEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                stationStopSelected = ((StationStop) parent.getItemAtPosition(position));
                imm.hideSoftInputFromWindow(stopCodeEditText.getWindowToken(), 0);
                updateLayout();
            }
        });



        stopCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                stationStopSelected = null;

            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        stopCodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == 6) {
                    if (stopCodeEditText.getAdapter().getCount() == 1) {
                        stationStopSelected = ((StationStop) stopCodeEditText.getAdapter().getItem(0));
                        imm.hideSoftInputFromWindow(stopCodeEditText.getWindowToken(), 0);
                        updateLayout();
                    }
                }
                return false;
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
        if (allFieldsValid && stationStopSelected != null) {
            UserStationItem userStationItem = null;
            int maxNumberToFetch = maxNumberSpinner.getSelectedItemPosition(); //0 = all

            if (transportModeSpinner.getSelectedItem().toString().equals("Bus")) {
                userStationItem = new UserStationItem(transportModeSelected, stationStopSelected, dynamicCheckBoxesBusRouteArray, dayTimeConditionsSelected, maxNumberToFetch);
            } else if (transportModeSpinner.getSelectedItem().toString().equals("Tube")) {
                userStationItem = new UserStationItem(transportModeSelected, stationStopSelected, dynamicCheckBoxesTubeArray, dayTimeConditionsSelected, maxNumberToFetch);
            }
            saveCustomSettingsToPrefs();
            if (!inEditMode) {
                // If not in EDIT MODE then add to List
                UserListView.userValues.add(userStationItem);
            } else {
                // Replace at position
                UserListView.userValues.set(positionToRestore, userStationItem);
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
        if (id == R.id.reset_station) {
            transportModeSpinner.setSelection(0,false);
            transportModeSelected = "";
            transportModeSpinner.setSelection(0);
            stationStopSelected = null;
            stopCodeEditText.setText("");
            dynamicCheckBoxesBusRouteArray.clear();
            dynamicCheckBoxesTubeArray.clear();
            conditionsSwitch.setChecked(false);
            conditionsPreviewText.setText("");
            maxNumberSpinner.setSelection(0);
            maxNumberSelected = 0;
            updateLayout();
            return true;
        } else if (id == R.id.local_mode_toggle) {
            switchingMode = true; // Set true to ensure widgets retain previous values on local mode change
            //stationSpinner.setOnItemSelectedListener(null);
            //stopCodeEditText.setOnItemClickListener(null);

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
        GPSTracker gps = new GPSTracker(AddNewStation.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            currentLocation = new Location("");
            currentLocation.setLatitude(gps.getLatitude());
            currentLocation.setLongitude(gps.getLongitude());
        } else {
            currentLocation = null;
            gps.showSettingsAlert();
        }

    }


    class TubStationsAndBusStopsDatabaseFetcher extends AsyncTask<String, Void, Void> {

        private List<StationStop> tubeStations;
        private List<StationStop> busStops;

        @Override
        protected synchronized Void doInBackground(String... strings) {

            tubeStations = null;
            tubeStations = db.getAllTubeStationsByNearest(currentLocation);
            tubeStations.add(0, new StationStop());//insert empty to front
            notify();

            busStops = null;
            busStops = db.getAllBusStopsOrderByNearest(currentLocation);
            busStops.add(0, new StationStop());//insert empty to front)
            notify();

            return null;
        }

        public synchronized List<StationStop> fetchTubeStationsOrderByNearest() {
            while (tubeStations == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeStations;
        }

        public synchronized List<StationStop> fetchBusStopsOrderByNearest() {
            while (busStops == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return busStops;
        }


    }

    class TubeLinesAndDirectionsByPlatformDatabaseFetcher extends AsyncTask<String, Void, Void> {

        private  List<Direction> tubeDirectionsPlatformsLineList;
        private boolean loadingComplete = false;

        @Override
        protected synchronized Void doInBackground(String... strings) {
            //string[0] is tube station ID
            loadingComplete = false;
            tubeDirectionsPlatformsLineList = new ArrayList<Direction>();
            for (RouteLine tubeLine : db.getTubeLinesByStation(strings[0])) {
                List<Direction> tubeDirectionsAndPlatformList;
                tubeDirectionsAndPlatformList = db.getTubeDirections(tubeLine.getID(), strings[0]);
                System.out.println(tubeDirectionsAndPlatformList.size());
                for (Direction direction : tubeDirectionsAndPlatformList) {
                    tubeDirectionsPlatformsLineList.add(new Direction(0, direction.getLabel(), tubeLine));
                }
            }
            loadingComplete = true;

            notify();
            return null;
        }

        public synchronized List<Direction> fetchTubeLinesDirectionsPlatformsByStation() {
            while (!loadingComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return tubeDirectionsPlatformsLineList;
        }

    }

    class BusRoutesByStopFetcher extends AsyncTask<String, Void, Void> {

    private  List<RouteLine> busRoutes;

    @Override
    protected synchronized Void doInBackground(String... strings) {

        busRoutes = null;
        busRoutes = db.getBusRoutesForAStop(strings[0]);

        notify();
        return null;
    }

    public synchronized List<RouteLine> fetchBusRoutesByStop () {
        while (busRoutes == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return busRoutes;
    }

}



}