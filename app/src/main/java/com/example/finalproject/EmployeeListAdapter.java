package com.example.finalproject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EmployeeListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> empName;
    private final List<String> empEmail;
    private final List<String> empPhone;
    private final List<String> userID;
    private String appointmentID;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    public EmployeeListAdapter(Activity context, String appointmentID, List<String> empName, List<String> empEmail, List<String> empPhone, List<String> userID) {
        super(context, R.layout.employee_list_item, empName);

        this.context = context;
        this.empName = empName;
        this.empEmail = empEmail;
        this.empPhone = empPhone;
        this.userID = userID;
        this.appointmentID = appointmentID;

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.employee_list_item, null, true);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Confirmation pop-up
                EditText empConfirmation = new EditText(view.getContext());
                AlertDialog.Builder empConfirmationDialog = new AlertDialog.Builder(view.getContext());
                empConfirmationDialog.setTitle("Confirmation");
                empConfirmationDialog.setMessage("Do you want to assign " + empName.get(position) + " to this appointment?" );
                empConfirmationDialog.setView(empConfirmation);

                empConfirmationDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Save to DB
                        DocumentReference documentReference = fStore.collection("appointments").document(appointmentID);
                        documentReference.update("employee", userID.get(position));
                        documentReference.update("status", "Confirmed");
                        //Going back to app info
                        Intent intent = new Intent(context.getApplicationContext(), AppointmentInfoActivity.class);
                        intent.putExtra("appointmentID", appointmentID);
                        context.startActivity(intent);
                    }
                });

                empConfirmationDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                empConfirmationDialog.create().show();
            }
        });

        TextView nameText = (TextView) rowView.findViewById(R.id.emp_name);
        TextView emailText = (TextView) rowView.findViewById(R.id.emp_email);
        TextView phoneText = (TextView) rowView.findViewById(R.id.emp_phone);

        nameText.setText(empName.get(position));
        emailText.setText(empEmail.get(position));
        phoneText.setText(empPhone.get(position));

        return rowView;
    }
}
