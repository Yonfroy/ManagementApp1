package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    
    EditText mFirstName, mLastName, mDoB,mAddress,mPostcode,mPhone, mEmail, mPassword,mCPassword;
    Button mRegisterBtn, mBackBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mFirstName = findViewById(R.id.registerFN);
        mLastName = findViewById(R.id.registerLN);
        mDoB = findViewById(R.id.registerDoB);
        mAddress = findViewById(R.id.registerAddress);
        mPostcode = findViewById(R.id.registerPostcode);
        mPhone = findViewById(R.id.registerPhone);
        mEmail = findViewById(R.id.registerEmail);
        mPassword = findViewById(R.id.registerPassword);
        mCPassword = findViewById(R.id.confirmPassword);

        mRegisterBtn = findViewById(R.id.completeBtn);
        mBackBtn = findViewById(R.id.backBtn);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();



        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        //Data format checks
        mRegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();
                String dOB = mDoB.getText().toString().trim();
                String address = mAddress.getText().toString().trim();
                String postcode = mPostcode.getText().toString().trim();
                String phone = mPhone.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String confirmPassword = mCPassword.getText().toString().trim();

                //Credentials check
                if(TextUtils.isEmpty(firstName)){
                    mEmail.setError("A first name is required.");
                    return;
                }

                if(TextUtils.isEmpty(lastName)){
                    mLastName.setError("A last name is required.");
                    return;
                }

                if(TextUtils.isEmpty(dOB)){
                    mDoB.setError("A date of birth is required.");
                    return;
                }

                if(TextUtils.isEmpty(address)){
                    mAddress.setError("An address is required.");
                    return;
                }

                if(TextUtils.isEmpty(postcode)){
                    mPostcode.setError("A valid postcode is required.");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    mPhone.setError("A phone number is required.");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("An email is required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("A password is required.");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError("Your password must contain at least 6 characters.");
                    return;
                }

                if(!confirmPassword.equals(password)) {
                    mCPassword.setError("Your passwords do not match.");
                    return;

                }

                //Registering user in the database
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            // Confirmation link

                            FirebaseUser fUser = fAuth.getCurrentUser();
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Register.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                private static final String TAG = "TAG:";
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Email not sent.");
                                }
                            });

                            Toast.makeText(Register.this, "User created.", Toast.LENGTH_SHORT).show();

                            //Retrieving current user ID
                            userID = fAuth.getCurrentUser().getUid();

                            //Specifying database collection path for data storage.
                            DocumentReference documentReference = fStore.collection("users").document(userID);

                            //Inserting user data into database.
                            Map<String,Object> user = new HashMap<>();
                            user.put("fName", firstName);
                            user.put("lName", lastName);
                            user.put("DoB", dOB);
                            user.put("address", address);
                            user.put("postcode", postcode);
                            user.put("phone", phone);
                            user.put("email", email);
                            user.put("password", password);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                private static final String TAG = "TAG";

                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: user profile created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                private static final String TAG = "TAG";

                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());
                                }
                            });

                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else {
                            Toast.makeText(Register.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }

    public void toMainActivity(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}