package com.ericmguimaraes.gaso.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by ericm on 3/25/2016.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    TimePickerInterface timePickerInterface;

    public TimePickerFragment(){
    }

    @SuppressLint("ValidFragment")
    public TimePickerFragment(TimePickerInterface timePickerInterface){
        this.timePickerInterface = timePickerInterface;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timePickerInterface.onTimeSet(view,hourOfDay,minute);
    }

    public interface TimePickerInterface {
        void onTimeSet(TimePicker view, int hourOfDay, int minute);
    }
}