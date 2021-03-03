package com.example.homergraphicstest3.ui.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homergraphicstest3.My_properties_quick_access_adpt;
import com.example.homergraphicstest3.Properties;
import com.example.homergraphicstest3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity implements View.OnClickListener {
    //Storage
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseDatabase mDatabase;
    StorageReference storageReference;
    DatabaseReference mReference;

    //item variables
    TextInputEditText addDescription_edt;
    TextView userDisplayName_txt, userEmail_txt, userPhone_txt;
    ImageButton editContact_btn, saveContacts_btn; //ToDo: add edit contacts button and (update dialog view layout)
    Button saveNewProp_btn;
    EditText editEmail_edt, editPhone_edt, addAddressLine1_edt,
            addAddressLine2_edt, addCity_edt,
            addPostCode_edt, addPrice_edt;
    CircleImageView profile_Pic;
    ImageView displayNewProp_img;
    RadioGroup paySchedule_rGrp, addFurniture_rgrp, addBills_rgrp;
    RecyclerView myProperties_rv;

    //views/dialogs
    AlertDialog.Builder dialogBuilder;
    AlertDialog alertDialog;

    //globals
    Integer imagePickerContext = 0; // 1 = profile picture, 2 = property image
    boolean ispropImgPicked = false; // set to 1 when image is added, remains 0 otherwise (reset after each use)
    HashMap<Integer, Uri> propertyImg_hm;
    ArrayList<Properties> propertyArrayList;
    Properties property;
    My_properties_quick_access_adpt adapter;


    private static final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        //resource connections
        userDisplayName_txt = findViewById(R.id.userDisplayName_txt);
        userEmail_txt = findViewById(R.id.userEmail_txt);
        userPhone_txt = findViewById(R.id.userPhone_txt);
        editContact_btn = findViewById(R.id.editContacts_btn);
        profile_Pic = findViewById(R.id.profile_Pic);
        Toolbar toolbar = findViewById(R.id.userProfile_Toolbar);
        myProperties_rv = findViewById(R.id.myProperties_rv);

        //on click listeners
        editContact_btn.setOnClickListener(this);
        profile_Pic.setOnClickListener(this);

        //firebase reference initialisations
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        currentUser = mAuth.getCurrentUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUsersInfo();
        getUsersProperty();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsersInfo();
    }


    private void getUsersProperty() {
        //get users property
        mReference = mDatabase.getReference().child("Properties");
        Query userPropQuery = mReference.orderByChild("OwnerID").equalTo(currentUser.getUid());

        //properties recycler view adapter
        myProperties_rv.setLayoutManager(new LinearLayoutManager(this));

        userPropQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyArrayList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    property = new Properties();
                    property.setAddLine_1(dataSnapshot.child("AddressLine1").getValue(String.class));
                    property.setAddLine_2(dataSnapshot.child("AddressLine2").getValue(String.class));
                    property.setCity(dataSnapshot.child("City").getValue(String.class));
                    property.setPostCode(dataSnapshot.child("PostCode").getValue(String.class));
                    property.setPrice(dataSnapshot.child("Price").getValue(String.class));
                    property.setPayScheme(dataSnapshot.child("PayScheme").getValue(String.class));
                    property.setBillsIncluded(dataSnapshot.child("BillsIncluded").getValue(String.class));
                    property.setFurnished(dataSnapshot.child("Furnished").getValue(String.class));
                    property.setImgUrl(dataSnapshot.child("ImageUrl").getValue(String.class));
                    propertyArrayList.add(property);
                }
                adapter = new My_properties_quick_access_adpt(UserProfile.this, propertyArrayList);
                myProperties_rv.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getUsersInfo() {
        if (currentUser != null) {
            //get user info
            mReference = mDatabase.getReference().child("Users");
            mReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userDisplayName = snapshot.child("fullname").getValue(String.class);
                    String userEmail = snapshot.child("Email").getValue(String.class);
                    String userPhone = snapshot.child("Phone").getValue(String.class);
                    userPhone_txt.setText(userPhone);
                    userEmail_txt.setText(userEmail);
                    userDisplayName_txt.setText(userDisplayName);
                    Glide.with(UserProfile.this).load(snapshot.child("imageurl").getValue(String.class)).into(profile_Pic);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    throw error.toException();
                }
            });
        }
    }

    //action bar menu items(plus button)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cart:
                addNewProperty();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //add new property dialog
    private void addNewProperty() {
        //Alert dialog
        dialogBuilder = new AlertDialog.Builder(UserProfile.this);
        View layoutView = getLayoutInflater().inflate(R.layout.new_property_dialog, null);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        propertyImg_hm = new HashMap<>();

        addDescription_edt = layoutView.findViewById(R.id.addDescription_edt);
        displayNewProp_img = layoutView.findViewById(R.id.displayNewProp_img);
        saveNewProp_btn = layoutView.findViewById(R.id.saveNewProperty_Btn);
        addAddressLine1_edt = layoutView.findViewById(R.id.addAddressLine1_edt);
        addAddressLine2_edt = layoutView.findViewById(R.id.addAddressLine2_edt);
        addCity_edt = layoutView.findViewById(R.id.addCity_edt);
        addPostCode_edt =layoutView.findViewById(R.id.addPostCode_edt);
        addPrice_edt = layoutView.findViewById(R.id.addPrice_edt);
        paySchedule_rGrp = layoutView.findViewById(R.id.paySchedule_rGrp);
        addFurniture_rgrp = layoutView.findViewById(R.id.addFurniture_rGrp);
        addBills_rgrp = layoutView.findViewById(R.id.addBills_rGrp);

        displayNewProp_img.setOnClickListener(this);
        saveNewProp_btn.setOnClickListener(this);
    }

    private boolean saveProperty() {
        String weeklyOrMonthly = "";
        Boolean furnished = false;
        Boolean billsIncluded = false;
        String description = addDescription_edt.getText().toString();
        String addressLine1 = addAddressLine1_edt.getText().toString();
        String addressLine2 = addAddressLine2_edt.getText().toString();
        String city = addCity_edt.getText().toString();
        String postCode = addPostCode_edt.getText().toString();
        String price = addPrice_edt.getText().toString();
        String ownerId = currentUser.getUid();
        switch (paySchedule_rGrp.getCheckedRadioButtonId()){
            case R.id.weekly_rbtn: weeklyOrMonthly = "P/W"; break;
            case R.id.monthly_rbtn: weeklyOrMonthly = "P/M"; break;
        }

        switch (addFurniture_rgrp.getCheckedRadioButtonId()){
            case R.id.furnished_rbtn: furnished = true; break;
        }

        switch (addBills_rgrp.getCheckedRadioButtonId()){
            case R.id.includingBills_rbtn: billsIncluded = true; break;
        }

        mReference = mDatabase.getReference().child("Properties");
        final String newPropertyKey = mReference.push().getKey();

        final HashMap<String, Object> propertyDet_hm = new HashMap<>();
        propertyDet_hm.put("PropertyID", newPropertyKey);
        propertyDet_hm.put("OwnerID", ownerId);
        propertyDet_hm.put("Description", description);
        propertyDet_hm.put("AddressLine1", addressLine1);
        propertyDet_hm.put("AddressLine2", addressLine2);
        propertyDet_hm.put("City", city);
        propertyDet_hm.put("PostCode", postCode);
        propertyDet_hm.put("Price", price);
        propertyDet_hm.put("PayScheme", weeklyOrMonthly);
        propertyDet_hm.put("BillsIncluded", billsIncluded.toString());
        propertyDet_hm.put("Furnished", furnished.toString());

        if(addressLine1.isEmpty() || city.isEmpty() || postCode.isEmpty() || price.isEmpty() || ispropImgPicked == false){
            showMessage("please fill out all required fields");
            return false;
        }
        else {
            final StorageReference imageFilePath = storageReference.child("user_photos").child(currentUser.getUid()).child("properties").child(newPropertyKey).child(propertyImg_hm.get(1).getLastPathSegment());
            imageFilePath.putFile(propertyImg_hm.get(1)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            propertyDet_hm.put("ImageUrl", uri.toString());
                            mReference.child(newPropertyKey).setValue(propertyDet_hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()) {
                                        showMessage("property added");
                                    } else {
                                        showMessage(task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }
        return true;
    }

    //edit user contacts dialogue
    private void editContacts() {
//        EditContactsDialog editContactsDialog = EditContactsDialog.newInstance();
//        editContactsDialog.show(getSupportFragmentManager(), "Edit Contacts Dialog Fragment");
        dialogBuilder = new AlertDialog.Builder(UserProfile.this);
        View layoutView = getLayoutInflater().inflate(R.layout.edit_contacts_dialog, null);
        dialogBuilder.setView(layoutView);
        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        editEmail_edt = layoutView.findViewById(R.id.editEmail_edt);
        editPhone_edt = layoutView.findViewById(R.id.editPhone_edt);
        saveContacts_btn = layoutView.findViewById(R.id.saveContacts_Btn);

        saveContacts_btn.setOnClickListener(this);

        mReference = mDatabase.getReference().child("Users");
        mReference.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userEmail = snapshot.child("Email").getValue(String.class);
                String userPhone = snapshot.child("Phone").getValue(String.class);
                editPhone_edt.setText(userPhone);
                editEmail_edt.setText(userEmail);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });
    }

    //saves new contacts in firebase
    private void updateContacts() {
        DatabaseReference emailRef = mReference.child(currentUser.getUid()).child("Email");
        DatabaseReference phoneRef = mReference.child(currentUser.getUid()).child("Phone");
        emailRef.setValue(editEmail_edt.getText().toString());
        phoneRef.setValue(editPhone_edt.getText().toString());
    }

    //opens image picker gallery ToDo: add option to take a photo (dialog box)
    public void chooseImage(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            switch (imagePickerContext){
                case 1:
                    updateUserInfo(selectedImage);
                    break;
                case 2:
                    //Todo: allow for selection of multiple images (Carousel or viewpager)
                    displayNewProp_img.setImageURI(selectedImage);
                    displayNewProp_img.setScaleType(ImageView.ScaleType.MATRIX.CENTER_CROP);
//                    Integer imgNumber = propertyImg_hm.size() + 1;
                    propertyImg_hm.put(1, selectedImage);
                    ispropImgPicked = true;
                    break;
            }
        }
    }

    //Updates users profile information in firebase
    private void updateUserInfo(final Uri selectedImage) {
        final StorageReference imageFilePath = storageReference.child("user_photos").child(currentUser.getUid()).child("profile_pic").child(selectedImage.getLastPathSegment());
        imageFilePath.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(final Uri uri) {
                        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showMessage("profile picture updated");
                                    mDatabase.getReference().child("Users").child(currentUser.getUid()).child("imageurl").setValue(currentUser.getPhotoUrl().toString());
                                    profile_Pic.setImageURI(selectedImage);
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_Pic:
                imagePickerContext = 1;
                chooseImage();
                break;
            case R.id.editContacts_btn:
                editContacts();
                break;
            case  R.id.saveContacts_Btn:
                updateContacts();
                alertDialog.dismiss();
                showMessage("Saved");
                break;
            case R.id.saveNewProperty_Btn:
                if(saveProperty()) { alertDialog.dismiss();}
                adapter.notifyDataSetChanged();
                break;
            case R.id.displayNewProp_img:
                imagePickerContext = 2;
                chooseImage();
        }
    }

}