package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AppointmentInfoActivity extends AppCompatActivity {

    String appointmentID, employeeID;
    TextView appCusName, appEmp, appAddress, appDate, appLength, appStatus;
    Button cancelApp, assignEmp;
    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    //TODO: Check for user type
    //TODO: Rename cancel app button to something else as it will be for assigning emp too
    String userType = "manager";
    //String userType = "customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_info);

        appCusName = findViewById(R.id.appCusfName);
        appEmp = findViewById(R.id.appEmp);
        appAddress = findViewById(R.id.appAddress);
        appDate = findViewById(R.id.appDate);
        appLength = findViewById(R.id.appLength);
        appStatus = findViewById(R.id.appStatus);

        cancelApp = findViewById(R.id.cancelAppointmentBtn);

        appointmentID = getIntent().getStringExtra("appointmentID");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference documentReference = fStore.collection("appointments").document(appointmentID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                loadCustomerData(documentSnapshot.getString("customer"));

                Timestamp datetime = (Timestamp) documentSnapshot.get("date");
                long duration = (Long) documentSnapshot.get("length");
                String durationText = String.valueOf(duration);
                if(duration > 1) {
                    durationText += " Hours";
                } else {
                    durationText += " Hour";
                }

                String employeeID = documentSnapshot.getString("employee");
                if(employeeID.isEmpty()){
                    appEmp.setText("Not yet assigned");
                } else {
                    loadEmployeeData(employeeID);
                }
                appAddress.setText(documentSnapshot.getString("address"));
                appDate.setText(datetime.toDate().toLocaleString());
                appLength.setText(durationText);
                appStatus.setText(documentSnapshot.getString("status"));
            }
        });

        //Changing btn text based on user type
        if(userType.equals("manager")) {
            cancelApp.setText("Assign Employee");
        } else if(userType.equals("customer")) {
            cancelApp.setText("Cancel Appointment");
        }

        cancelApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType.equals("manager")) {
                    //TODO: Assign employees here (aka show pop up for selecting emps or something)
                } else if(userType.equals("customer")) {
                    startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                    documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Your appointment has been cancelled.", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Your appointment cannot be cancelled, submit a bug report.", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void loadEmployeeData(String employeeID){
        DocumentReference documentReference = fStore.collection("users").document(employeeID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String firstName = documentSnapshot.getString("fName");
                String lastName = documentSnapshot.getString("lName");
                appEmp.setText(firstName + " " + lastName);
            }
        });
    }

    private void loadCustomerData(String userID) {
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String firstName = documentSnapshot.getString("fName");
                String lastName = documentSnapshot.getString("lName");
                appCusName.setText(firstName + " " + lastName);
            }
        });
    }
}