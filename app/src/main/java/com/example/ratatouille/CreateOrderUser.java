package com.example.ratatouille;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class CreateOrderUser extends AppCompatActivity {

    Button dtBtn;
    Context context = this;
    TextView shwTm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order_user);

        shwTm = findViewById(R.id.txTime);
        dtBtn = findViewById(R.id.dtBtn);
        Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);
        dtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        shwTm.setText(i+":"+i1);
                    }
                },hour,min,android.text.format.DateFormat.is24HourFormat(context));
                timePickerDialog.show();
            }
        });
    }
}
