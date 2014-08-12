package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TimePicker;;import java.text.ParseException;

public class SetDayTimeConditions  extends Activity{

    private ArrayAdapter<CharSequence> radiusStartingStopAdapter;

    private TimePicker timePickerTo;
    private TimePicker timePickerFrom;
    private Spinner radiusStartingStopSpinner;

    private Button cancelButton;
    private Button OKButton;


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
        radiusStartingStopAdapter = ArrayAdapter.createFromResource(this, R.array.radius_starting_station_array, android.R.layout.simple_spinner_item);
        radiusStartingStopAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radiusStartingStopSpinner.setAdapter(radiusStartingStopAdapter);

        cancelButton = (Button) findViewById(R.id.CancelConditionPopupButton);
        OKButton = (Button) findViewById(R.id.OkConditionPopupButton);

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
                    dtc = new DayTimeConditions(timePickerFrom.getCurrentHour(), timePickerFrom.getCurrentMinute(), timePickerTo.getCurrentHour(), timePickerTo.getCurrentMinute(),selectedDays, radiusFromStartingStop);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Intent intent=new Intent();
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
                if (checked) {selectedDays[0] = true; }
                else { selectedDays[0] = false;}
                break;
            case R.id.checkbox_monday:
                if (checked) {selectedDays[1] = true; }
                else { selectedDays[1] = false;}
                break;
            case R.id.checkbox_tuesday:
                if (checked) {selectedDays[2] = true; }
                else { selectedDays[2] = false;}
                break;
            case R.id.checkbox_wednesday:
                if (checked) {selectedDays[3] = true; }
                else { selectedDays[3] = false;}
                break;
            case R.id.checkbox_thursday:
                if (checked) {selectedDays[4] = true; }
                else { selectedDays[4] = false;}
                break;
            case R.id.checkbox_friday:
                if (checked) {selectedDays[5] = true; }
                else { selectedDays[5] = false;}
                break;
            case R.id.checkbox_saturday:
                if (checked) {selectedDays[6] = true; }
                else { selectedDays[6] = false;}
                break;
        }
    }


}
