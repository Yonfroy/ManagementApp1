package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ScheduleAppointmentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView address;
    EditText date_time_in;
    Spinner dropdown;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button confirmAppBtn;
    Timestamp selectedTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_appointment);

        address = findViewById(R.id.address);
        confirmAppBtn = findViewById(R.id.confirmAppBtn);
        date_time_in = findViewById(R.id.date_time_in);
        date_time_in.setInputType(InputType.TYPE_NULL);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userId = fAuth.getCurrentUser().getUid();

        Integer[] length = new Integer[]{1,2,3,4,5};

        dropdown = findViewById(R.id.spinner1);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, length);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        dropdown.setOnItemSelectedListener(this);

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                address.setText(documentSnapshot.getString("address"));
            }
        });

        confirmAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Add app to database
                if(validateData()){
                    setAppointment();
                    Toast.makeText(ScheduleAppointmentActivity.this, "Appointment booked.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                }
            }
        });

        date_time_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(date_time_in);
            }
        });
    }

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yy HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                        selectedTimestamp = new Timestamp(calendar.getTime());
                    }
                };

                new TimePickerDialog(ScheduleAppointmentActivity.this,timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(ScheduleAppointmentActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    private void setAppointment(){
        DocumentReference documentReference = fStore.collection("appointments").document();

        Map<String,Object> appointment = new HashMap<>();
        appointment.put("address", address.getText().toString());
        appointment.put("customer", userId);
        appointment.put("date", selectedTimestamp);
        appointment.put("employee", "");
        appointment.put("length", (Integer) dropdown.getSelectedItem());
        appointment.put("status", "Pending");


        documentReference.set(appointment).addOnSuccessListener(new OnSuccessListener<Void>() {
            private static final String TAG = "TAG";

            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: appointment created for " + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            private static final String TAG = "TAG";

            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    private boolean validateData() {
        if(selectedTimestamp == null){
            Toast.makeText(ScheduleAppointmentActivity.this, "Select a time and date.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(selectedTimestamp.getSeconds() < (Timestamp.now().getSeconds() + 86400)){
            Toast.makeText(ScheduleAppointmentActivity.this, "Please select a time and date a day ahead.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(dropdown.getSelectedItem() == null){
            Toast.makeText(ScheduleAppointmentActivity.this, "Select the length of your appointment.", Toast.LENGTH_LONG).show();
            return false;
        }

        if(address == null){
            Toast.makeText(ScheduleAppointmentActivity.this, "Select an address for your appointment.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String text = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}