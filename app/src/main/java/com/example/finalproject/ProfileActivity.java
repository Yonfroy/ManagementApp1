package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    TextView fName,lName, email, address, postcode, phoneNumber, verifyMsg;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    Button returnBtn, resendCode, resetPasswordProfile, editProfileImg;
    ImageView profileImage;
    StorageReference storageReference;

    //TODO: Handle DB on the spot
    //Hello

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fName = findViewById(R.id.profileName);
        lName = findViewById(R.id.profileLastName);
        email = findViewById(R.id.profileEmail);
        address = findViewById(R.id.profileAddress);
        postcode = findViewById(R.id.profilePostcode);
        phoneNumber = findViewById(R.id.profilePhone);
        resetPasswordProfile = findViewById(R.id.profileResetPW);
        editProfileImg = findViewById(R.id.editProfile);
        profileImage = findViewById(R.id.profileImage);

        verifyMsg = findViewById(R.id.emailNotVerifeid);
        verifyMsg.setVisibility(View.INVISIBLE);
        resendCode = findViewById(R.id.verifyEmailBtn);
        resendCode.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        userId = fAuth.getCurrentUser().getUid();
        FirebaseUser user = fAuth.getCurrentUser();

        //Resending verification email if not yet verified
        if(!user.isEmailVerified()){
            resendCode.setVisibility(View.VISIBLE);
            verifyMsg.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseUser fUser = fAuth.getCurrentUser();
                    fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ProfileActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        private static final String TAG = "TAG:";
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent.");
                        }
                    });
                }
            });
        }

        DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                fName.setText(documentSnapshot.getString("fName"));
                lName.setText(documentSnapshot.getString("lName"));
                email.setText(documentSnapshot.getString("email"));
                address.setText(documentSnapshot.getString("address"));
                postcode.setText(documentSnapshot.getString("postcode"));
                phoneNumber.setText(documentSnapshot.getString("phone"));
            }
        });

        resetPasswordProfile.setOnClickListener(v -> {
            EditText resetPassword = new EditText(v.getContext());

            AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
            passwordResetDialog.setTitle("Reset password?");
            passwordResetDialog.setMessage("Enter your new password:");
            passwordResetDialog.setView(resetPassword);

            passwordResetDialog.setPositiveButton("Yes", (dialog, which) -> {
                String newPassword = resetPassword.getText().toString();
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Your password has been reset.", Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Your password has not been changed, try again.", Toast.LENGTH_SHORT);
                    }
                });
            });
            passwordResetDialog.setNegativeButton("No", (dialog, which) -> {
                // Close dialog
            });
            passwordResetDialog.create().show();
        });

        returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingActivity.class));
                finish();
            }
        });

        editProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Open gallery
                Intent i = new Intent(view.getContext(), EditProfileActivity.class);
                i.putExtra("firstName", fName.getText().toString());
                i.putExtra("lastName", lName.getText().toString());
                i.putExtra("address", address.getText().toString());
                i.putExtra("postcode", postcode.getText().toString());
                i.putExtra("phoneNumber", phoneNumber.getText().toString());
                i.putExtra("email", email.getText().toString());
                startActivity(i);
            }
        });
    }
}