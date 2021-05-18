package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    EditText profileFName, profileLName, profileAddress, profilePostcode, profilePhoneNum, profileEmail;
    Button returnBtn, confirmBtn;
    ImageView profileImage;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent data = getIntent();
        String firstName = data.getStringExtra("firstName");
        String lastName = data.getStringExtra("lastName");
        String address = data.getStringExtra("address");
        String postcode = data.getStringExtra("postcode");
        String phoneNum = data.getStringExtra("phoneNumber");
        String email = data.getStringExtra("email");

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        user = fAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        StorageReference profileImg = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        profileImg.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        returnBtn = findViewById(R.id.editReturnBtn);
        confirmBtn = findViewById(R.id.editConfirmBtn);

        profileFName = findViewById(R.id.editFirstName);
        profileLName = findViewById(R.id.editLastName);
        profileAddress = findViewById(R.id.editAddress);
        profilePostcode = findViewById(R.id.editPostcode);
        profilePhoneNum = findViewById(R.id.editPhoneNum);
        profileEmail = findViewById(R.id.editEmail);
        profileImage = findViewById(R.id.editProfileImg);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });


        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                finish();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (profileFName.getText().toString().isEmpty() || profileLName.getText().toString().isEmpty() || profileAddress.getText().toString().isEmpty() ||
                        profilePostcode.getText().toString().isEmpty() || profilePhoneNum.getText().toString().isEmpty() || profileEmail.getText().toString().isEmpty()) {

                    Toast.makeText(EditProfileActivity.this, "One or more fields are empty.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = profileEmail.getText().toString();
                String firstName = profileFName.getText().toString();
                String lastName = profileLName.getText().toString();
                String address = profileAddress.getText().toString();
                String postcode = profilePostcode.getText().toString();
                String phoneNum = profilePhoneNum.getText().toString();

                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DocumentReference docRef = fStore.collection("users").document(user.getUid());
                        Map<String, Object> updatedVars = new HashMap<>();
                        updatedVars.put("fName", firstName);
                        updatedVars.put("lName", lastName);
                        updatedVars.put("address", address);
                        updatedVars.put("postcode", postcode);
                        updatedVars.put("phone", phoneNum);
                        updatedVars.put("email", email);
                        docRef.update(updatedVars);
                        Toast.makeText(EditProfileActivity.this, "Details.", Toast.LENGTH_SHORT);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        profileFName.setText(firstName);
        profileLName.setText(lastName);
        profileAddress.setText(address);
        profilePostcode.setText(postcode);
        profilePhoneNum.setText(phoneNum);
        profileEmail.setText(email);

        Log.d(TAG, "onCreate: " + firstName + " " + lastName + " " + address + " " + postcode + " " + phoneNum + " " + email);
    }

    private void uploadImageToFirebase(Uri imageUri){
        //Upload image to storage
        StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfileActivity.this, "Image upload failed.", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);
            }
        }
    }

}