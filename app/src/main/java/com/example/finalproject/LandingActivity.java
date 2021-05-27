package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LandingActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    String userID;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    ListView appList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userID = fAuth.getCurrentUser().getUid();

        String userType = getIntent().getStringExtra("userType");

        List<String> addresses = new ArrayList<String>();
        List<String> dates = new ArrayList<String>();
        List<String> durations = new ArrayList<String>();
        List<String> appointmentID = new ArrayList<String>();

        AppointmentListAdapter adapter = new AppointmentListAdapter(this, addresses, dates, durations, appointmentID, userType);
        appList=(ListView)findViewById(R.id.appointmentList);
        appList.setAdapter(adapter);

        Query appointmentQuery = null;
        System.out.println(userType);
        if(userType.equals("customer")){
            System.out.println(userType);
            appointmentQuery = fStore.collection("appointments").whereEqualTo("customer", userID);
        } else if(userType.equals("manager")){
            System.out.println(userType);
            appointmentQuery = fStore.collection("appointments").orderBy("date");
        } else if(userType.equals("employee")){
            System.out.println(userType);
            appointmentQuery = fStore.collection("appointments").whereEqualTo("employee", userID);
        }
        appointmentQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        //Get address, dates and duration from document
                        Map<String, Object> data = document.getData();

                        Timestamp datetime = (Timestamp) data.get("date");
                        long duration = (Long) data.get("length");
                        String durationText = String.valueOf(duration);
                        if(duration > 1) {
                            durationText += " Hours";
                        } else {
                            durationText += " Hour";
                        }

                        addresses.add(data.get("address").toString());
                        dates.add(datetime.toDate().toLocaleString());
                        durations.add(durationText);
                        appointmentID.add(document.getId());

                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "Error retrieving documents: ", task.getException());
                }
            }
        });
    }

    public void profile2(View view){
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        finish();
    }

    public void settings(View view){
        Toast.makeText(this, "Under construction!", Toast.LENGTH_LONG).show();
    }
    public void scheduleAppointment (View view){
        startActivity(new Intent(getApplicationContext(), ScheduleAppointmentActivity.class));
    }
}