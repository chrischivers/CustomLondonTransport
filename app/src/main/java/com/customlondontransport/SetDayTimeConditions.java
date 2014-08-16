package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;
import java.text.ParseException;

public class SetDayTimeConditions  extends Activity{

    private TimePicker timePickerTo;
    private TimePicker timePickerFrom;
    private Spinner radiusStartingStopSpinner;


    private DayTimeConditions dtc;
    private boolean[] selectedDays = new boolean[7]; //false by default




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        timePickerTo = (TimePicker) findViewById(R.id.timePickerTo);
        timePickerFrom = (TimePicker) findViewById(R.id.timePickerFrom);
        timePickerTo.setIs24HourView(true);
        timePickerFrom.setIs24HourView(true);

        radiusStartingStopSpinner = (Spinner) findViewById(R.id.radiusStartingStopSpinner);
        ArrayAdapter<CharSequence> radiusStartingStopAdapter = ArrayAdapter.createFromResource(this, R.array.radius_starting_station_array, android.R.layout.simple_spinner_item);
        radiusStartingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusStartingStopSpinner.setAdapter(radiusStartingStopAdapter);

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
                    System.out.println(timePickerFrom.getCurrentHour());
                    System.out.println(timePickerFrom.getCurrentMinute());
                    int radiusFromStartingStop = -1; // -1 if none selected
                    if (!radiusStartingStopSpinner.getSelectedItem().toString().equals("OFF")) {
                        radiusFromStartingStop = Integer.parseInt(radiusStartingStopSpinner.getSelectedItem().toString());
                    }
                    dtc = new DayTimeConditions(timePickerFrom.getCurrentHour(), timePickerFrom.getCurrentMinute(), timePickerTo.getCurrentHour(), timePickerTo.getCurrentMinute(), selectedDays, radiusFromStartingStop);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent();
                intent.putExtra("DayTimeConditions", dtc);
                setResult(RESULT_OK, intent);
                finish();
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
