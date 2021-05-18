package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    Button logOutBtn;
    FirebaseAuth fAuth;
    StorageReference storageReference;
    FirebaseFirestore fStore;
    Button testBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        testBtn = findViewById(R.id.testBtn);

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

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               fStore.collection("appointments").whereEqualTo("customer", userID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull Task<QuerySnapshot> task) {
                       if(task.isSuccessful()){
                           for(QueryDocumentSnapshot document : task.getResult()){
                               System.out.println(document.getId() + " => " + document.getData());
                           }
                       } else {
                           Log.d(TAG, "Error retrieving documents: ", task.getException());
                       }
                   }
               });
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
}