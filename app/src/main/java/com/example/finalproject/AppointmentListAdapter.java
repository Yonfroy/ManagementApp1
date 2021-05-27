package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
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
    private final List<String> appointmentID;
    private String userType;

    public AppointmentListAdapter(Activity context, List<String> addresses, List<String> dates, List<String> durations, List<String> appointmentID, String userType) {
        super(context, R.layout.appointment_list_item, addresses);

        this.context = context;
        this.addresses = addresses;
        this.dates = dates;
        this.durations = durations;
        this.appointmentID = appointmentID;
        this.userType = userType;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.appointment_list_item, null, true);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), AppointmentInfoActivity.class);
                intent.putExtra("appointmentID", appointmentID.get(position));
                intent.putExtra("userType", userType);
                context.startActivity(intent);
            }
        });

        TextView addressText = (TextView) rowView.findViewById(R.id.app_address);
        TextView dateText = (TextView) rowView.findViewById(R.id.app_date);
        TextView durationText = (TextView) rowView.findViewById(R.id.app_duration);

        addressText.setText(addresses.get(position));
        dateText.setText(dates.get(position));
        durationText.setText(durations.get(position));

        return rowView;
    }
}
