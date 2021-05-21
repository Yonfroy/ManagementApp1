package com.example.finalproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> addresses;
    private final List<String> dates;
    private final List<String> durations;

    public AppointmentListAdapter(Activity context, List<String> addresses, List<String> dates, List<String> durations) {
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

        addressText.setText(addresses.get(position));
        dateText.setText(dates.get(position));
        durationText.setText(durations.get(position));

        return rowView;
    }
}
