package com.customlondontransport;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import java.text.ParseException;

public class SetDayTimeConditions  extends Activity{

    private TimePicker timePickerTo;
    private TimePicker timePickerFrom;
    private Spinner radiusStartingStopSpinner;

    private LinearLayout timeLayout;
    private LinearLayout daysLayout;
    private LinearLayout radiusLayout;
    private LinearLayout containerLayout;

    private CheckBox timeCheckBox;
    private CheckBox daysCheckBox;
    private CheckBox radiusCheckBox;


    private DayTimeConditions dtc;
    private boolean[] selectedDays = new boolean[7]; //false by default

    private Context context;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        context = getApplicationContext();

        timePickerTo = (TimePicker) findViewById(R.id.timePickerTo);
        timePickerFrom = (TimePicker) findViewById(R.id.timePickerFrom);
        timePickerTo.setIs24HourView(true);
        timePickerFrom.setIs24HourView(true);

        timeLayout = (LinearLayout) findViewById(R.id.conditionsLinearLayoutTimes);
        daysLayout = (LinearLayout) findViewById(R.id.conditionsLinearLayoutDays);
        radiusLayout = (LinearLayout) findViewById(R.id.conditionsLinearLayoutRadius);
        containerLayout = (LinearLayout) findViewById(R.id.condiitonsLinearLayoutContainer);
        containerLayout.removeAllViewsInLayout();

        timeCheckBox = (CheckBox) findViewById(R.id.conditionsTimeCheckBox);
        daysCheckBox = (CheckBox) findViewById(R.id.conditionsDaysCheckBox);
        radiusCheckBox = (CheckBox) findViewById(R.id.conditionsRadiusCheckBox);





        Button cancelButton = (Button) findViewById(R.id.CancelConditionPopupButton);
        Button OKButton = (Button) findViewById(R.id.OkConditionPopupButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });

        OKButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int radiusFromStartingStop = -1; // -1 if none selected
                    if (radiusCheckBox.isChecked()) {
                        if (!radiusStartingStopSpinner.getSelectedItem().toString().equals("OFF")) {
                            radiusFromStartingStop = Integer.parseInt(radiusStartingStopSpinner.getSelectedItem().toString());
                        }
                    }

                    // Time and Day conditions set
                    if (timeCheckBox.isChecked() && daysCheckBox.isChecked()) {
                        dtc = new DayTimeConditions(timePickerFrom.getCurrentHour(), timePickerFrom.getCurrentMinute(), timePickerTo.getCurrentHour(), timePickerTo.getCurrentMinute(), selectedDays, radiusFromStartingStop, context);
                    }
                    // Time conditions set, Day conditions not set
                    else if (timeCheckBox.isChecked() && !daysCheckBox.isChecked()) {
                        dtc = new DayTimeConditions(timePickerFrom.getCurrentHour(), timePickerFrom.getCurrentMinute(), timePickerTo.getCurrentHour(), timePickerTo.getCurrentMinute(), radiusFromStartingStop, context);
                    }
                    // Days conditions set, Time conditions not set
                    else if (!timeCheckBox.isChecked() && daysCheckBox.isChecked()) {
                        dtc = new DayTimeConditions(selectedDays, radiusFromStartingStop, context);
                    }
                    // Days conditions not set, Time conditions not set
                    else if (!timeCheckBox.isChecked() && !daysCheckBox.isChecked()) {
                        if (radiusFromStartingStop != -1) {
                            dtc = new DayTimeConditions(radiusFromStartingStop, context);
                        } else { //No selection criteria. Treat as cancel
                            setResult(RESULT_CANCELED, null);
                            finish();
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.putExtra("DayTimeConditions", dtc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        timeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    containerLayout.addView(timeLayout);
                } else {
                    containerLayout.removeView(timeLayout);
                }
            }
        });

        daysCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    containerLayout.addView(daysLayout);
                } else {
                    containerLayout.removeView(daysLayout);
                }
            }
        });

        radiusCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    containerLayout.addView(radiusLayout);
                    radiusStartingStopSpinner = (Spinner) findViewById(R.id.radiusStartingStopSpinner);
                    ArrayAdapter<CharSequence> radiusStartingStopAdapter = ArrayAdapter.createFromResource(getBaseContext(), R.array.radius_starting_station_array, android.R.layout.simple_spinner_item);
                    radiusStartingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    radiusStartingStopSpinner.setAdapter(radiusStartingStopAdapter);
                } else {
                    containerLayout.removeView(radiusLayout);
                }
            }
        });

    }



    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_sunday:
                selectedDays[0] = checked;
                break;
            case R.id.checkbox_monday:
                selectedDays[1] = checked;
                break;
            case R.id.checkbox_tuesday:
                selectedDays[2] = checked;
                break;
            case R.id.checkbox_wednesday:
                selectedDays[3] = checked;
                break;
            case R.id.checkbox_thursday:
                selectedDays[4] = checked;
                break;
            case R.id.checkbox_friday:
                selectedDays[5] = checked;
                break;
            case R.id.checkbox_saturday:
                selectedDays[6] = checked;
                break;
        }
    }


}
