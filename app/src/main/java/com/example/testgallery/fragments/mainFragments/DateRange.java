package com.example.testgallery.fragments.mainFragments;

import android.app.Fragment;
import android.os.Bundle;

import java.util.Calendar;

/**
 * Created by Muhammad on 12/9/2015.
 */
public class DateRange extends Fragment {

    private int year_x;
    private int month_x;
    private int day_x;
    private Calendar fromDate;
    private Calendar toDate;
    private long dateDifference;
    static final int FROM_DIALOG_ID = 0, TO_DIALOG_ID = 1;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public DateRange() {
        final Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR);
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public String doCalculation() {
        dateDifference = getToDate().getTimeInMillis() - getFromDate().getTimeInMillis();
        dateDifference = dateDifference / (24 * 60 * 60 * 1000);
        String noOfDays = String.valueOf(dateDifference);
        return noOfDays;
    }
    public int getYear_x() {
        return year_x;
    }

    public int getMonth_x() {
        return month_x;
    }

    public int getDay_x() {
        return day_x;
    }


    public void setFromDate(Calendar fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Calendar toDate) {
        this.toDate = toDate;
    }
    public Calendar getFromDate() {
        return fromDate;
    }

    public Calendar getToDate() {
        return toDate;
    }

    public String setFromDate(int year, int monthOfYear, int dayOfMonth) {
        setFromDate(initCalendar());
        getFromDate().set(Calendar.YEAR, year);
        getFromDate().set(Calendar.MONTH, monthOfYear);
        getFromDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return (year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
    }

    public String setToDate(int year, int monthOfYear, int dayOfMonth) {
        setToDate(initCalendar());
        getToDate().set(Calendar.YEAR, year);
        getToDate().set(Calendar.MONTH, monthOfYear);
        getToDate().set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return (year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
    }

    private Calendar initCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }
}
