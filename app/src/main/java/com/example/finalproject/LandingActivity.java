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

        List<String> addresses = new ArrayList<String>();
        List<String> dates = new ArrayList<String>();
        List<String> durations = new ArrayList<String>();
        List<String> appointmentID = new ArrayList<String>();

        AppointmentListAdapter adapter = new AppointmentListAdapter(this, addresses, dates, durations, appointmentID);
        appList=(ListView)findViewById(R.id.appointmentList);
        appList.setAdapter(adapter);

        fStore.collection("appointments").whereEqualTo("customer", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void profile2(View view){
        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        finish();
    }

    public void scheduleAppointment (View view){
        startActivity(new Intent(getApplicationContext(), ScheduleAppointmentActivity.class));
    }
}