package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmployeeSelectionActivity extends AppCompatActivity {

    ListView empList;
    FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_selection);

        fStore = FirebaseFirestore.getInstance();
        String appointmentID = getIntent().getStringExtra("appointmentID");

        List<String> names = new ArrayList<String>();
        List<String> emails = new ArrayList<String>();
        List<String> phones = new ArrayList<String>();
        List<String> employeeIDs = new ArrayList<String>();

        EmployeeListAdapter adapter = new EmployeeListAdapter(this, appointmentID, names, emails, phones, employeeIDs);
        empList=(ListView)findViewById(R.id.employeeList);
        empList.setAdapter(adapter);

        fStore.collection("users").whereEqualTo("userType", "employee").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        //Get address, dates and duration from document
                        Map<String, Object> data = document.getData();

                        String empFName = (String) data.get("fName");
                        String empLName = (String) data.get("lName");
                        String empEmail = (String) data.get("email");
                        String empPhone = (String) data.get("phone");
                        String empID = (String) document.getId();

                        names.add(empFName + " " + empLName);
                        emails.add(empEmail);
                        phones.add(empPhone);
                        employeeIDs.add(empID);

                        adapter.notifyDataSetChanged();
                    }
                } else {
                    System.out.println(task.getException().toString());
                }
            }
        });
    }
}