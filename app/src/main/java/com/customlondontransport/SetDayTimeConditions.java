package com.customlondontransport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TimePicker;;

public class SetDayTimeConditions  extends Activity{
    private TimePicker timePickerTo;
    private TimePicker timePickerFrom;

    private Button cancelButton;
    private Button OKButton;

    private DayTimeConditions dtc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conditions);

        timePickerTo = (TimePicker) findViewById(R.id.timePickerTo);
        timePickerFrom = (TimePicker) findViewById(R.id.timePickerFrom);
        timePickerTo.setIs24HourView(true);
        timePickerFrom.setIs24HourView(true);

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
                Intent intent=new Intent();
                intent.putExtra("DayTimeConditions", dtc);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }


}
