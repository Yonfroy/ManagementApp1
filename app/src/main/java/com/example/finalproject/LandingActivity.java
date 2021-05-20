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

import java.util.HashMap;
import java.util.Map;

public class LandingActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    String userID;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    ListView appList;

    String[] addresses = {
      "63 Northcote Road", "19 Wessex Gate", "Trump tower 102",
    };

    String[] dates = {
            "05/04/1999 12:00:00", "17/02/2021 16:30:00", "01/11/2021 06:00:00",
    };

    String[] durations = {
            "2H", "6H", "1H",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        //testBtn = findViewById(R.id.testBtn);

        //LIST TEST BELOW
        AppointmentListAdapter adapter = new AppointmentListAdapter(this, addresses, dates, durations);
        appList=(ListView)findViewById(R.id.appointmentList);
        appList.setAdapter(adapter);

        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });



        int time = (int) (System.currentTimeMillis());

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        userID = fAuth.getCurrentUser().getUid();
//        DocumentReference documentReference = fStore.collection("appointments").document();
//
//        Map<String,Object> user = new HashMap<>();
//        user.put("address", "BH8 8PD");
//        user.put("customer", userID);
//        user.put("date", Timestamp.now());
//        user.put("employee", "");
//        user.put("length", 1);
//        user.put("status", "Pending");
//
//
//        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
//            private static final String TAG = "TAG";
//
//            @Override
//            public void onSuccess(Void aVoid) {
//                Log.d(TAG, "onSuccess: user profile created for " + userID);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            private static final String TAG = "TAG";
//
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.d(TAG, "onFailure: " + e.toString());
//            }
//        });

//        testBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               fStore.collection("appointments").whereEqualTo("customer", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                   @Override
//                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                       if(task.isSuccessful()){
//                           for(QueryDocumentSnapshot document : task.getResult()){
//                               System.out.println(document.getId() + " => " + document.getData());
//                           }
//                       } else {
//                           Log.d(TAG, "Error retrieving documents: ", task.getException());
//                       }
//                   }
//               });
//            }
//        });
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