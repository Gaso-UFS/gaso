package com.ericmguimaraes.gaso.util;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by ericm on 3/25/2016.
 */
@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private DatePickerInterface datePickerInterface;

    public DatePickerFragment(){
    }

    @SuppressLint("ValidFragment")
    public DatePickerFragment(DatePickerInterface datePickerInterface){
        this.datePickerInterface = datePickerInterface;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        datePickerInterface.onDateSet(view,year,month,day);
    }

    public interface DatePickerInterface {
        void onDateSet(DatePicker view, int year, int month, int day);
    }

}
