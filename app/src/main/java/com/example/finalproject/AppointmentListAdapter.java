package com.example.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AppointmentListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] addresses;
    private final String[] dates;
    private final String[] durations;

    public AppointmentListAdapter(Activity context, String[] addresses, String[] dates, String[] durations) {
        super(context, R.layout.appointment_list_item, addresses);

        this.context = context;
        this.addresses = addresses;
        this.dates = dates;
        this.durations = durations;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.appointment_list_item, null, true);

        TextView addressText = (TextView) rowView.findViewById(R.id.app_address);
        TextView dateText = (TextView) rowView.findViewById(R.id.app_date);
        TextView durationText = (TextView) rowView.findViewById(R.id.app_duration);

        addressText.setText(addresses[position]);
        dateText.setText(dates[position]);
        durationText.setText(durations[position]);

        return rowView;
    }
}
