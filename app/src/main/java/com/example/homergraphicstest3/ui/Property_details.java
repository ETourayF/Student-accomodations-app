package com.example.homergraphicstest3.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.homergraphicstest3.Properties;
import com.example.homergraphicstest3.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Property_details extends AppCompatActivity {
    String propID;
    DatabaseReference mReference;
    FirebaseDatabase mdatabase;
    TextView propDescription_txt, rentState_txt, billState_txt, furnState_txt, ownerName_txt;
    ImageView propImage_img, furnState_img, billState_img, rentState_img;
    CircleImageView profileImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.property_details);
        Toolbar toolbar = findViewById(R.id.propDetails_Toolbar);

        propDescription_txt = findViewById(R.id.propDescription_txt);
        propImage_img = findViewById(R.id.displayImg_datails);
        profileImage = findViewById(R.id.profileImgDetails_btn);
        rentState_txt = findViewById(R.id.rentState_txt);
        billState_txt = findViewById(R.id.billState_txt);
        furnState_txt = findViewById(R.id.furnState_txt);
        ownerName_txt = findViewById(R.id.ownerName_txt);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_btn);

        Bundle extras = getIntent().getExtras();
        if(extras!=null){
            propID = extras.getString("propID");
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mdatabase = FirebaseDatabase.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference().child("Properties").child(propID);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Properties properties = new Properties();
                properties.setProp_ID(snapshot.child("PropertyID").getValue(String.class));
                properties.setOwner_ID(snapshot.child("OwnerID").getValue(String.class));
                properties.setDescription(snapshot.child("Description").getValue(String.class));
                properties.setAddLine_1(snapshot.child("AddressLine1").getValue(String.class));
                properties.setAddLine_2(snapshot.child("AddressLine2").getValue(String.class));
                properties.setCity(snapshot.child("City").getValue(String.class));
                properties.setPostCode(snapshot.child("PostCode").getValue(String.class));
                properties.setPrice(snapshot.child("Price").getValue(String.class));
                properties.setPayScheme(snapshot.child("PayScheme").getValue(String.class));
                properties.setBillsIncluded(snapshot.child("BillsIncluded").getValue(String.class));
                properties.setFurnished(snapshot.child("Furnished").getValue(String.class));
                properties.setImgUrl(snapshot.child("ImageUrl").getValue(String.class));

                setDetails(properties.getProp_ID(), properties.getOwner_ID(), properties.getDescription(),
                        properties.getAddLine_1(), properties.getAddLine_2(), properties.getCity(),
                        properties.getPostCode(), properties.getPrice(), properties.getPayScheme(),
                        properties.getBillsIncluded(), properties.getFurnished(), properties.getImgUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setDetails(String prop_id, String owner_id, String description, String addLine_1,
                            String addLine_2, String city, String postCode, String price,
                            String payScheme, String billsIncluded, String furnished, String imgUrl) {

        propDescription_txt.setText(description);
        Picasso.get().load(imgUrl).into(propImage_img);
        rentState_txt.setText(String.format("£%s %s", price, payScheme));

        if(Boolean.parseBoolean(furnished)){furnState_txt.setText("+Furniture");}
        else {furnState_txt.setText("-Furniture");}

        if(Boolean.parseBoolean(billsIncluded)){billState_txt.setText("+Bills");}
        else {billState_txt.setText("-Bills");}

        loadOwnerInfo(owner_id);
    }

    private void loadOwnerInfo(String owner_id) {
        mReference = mdatabase.getReference().child("Users").child(owner_id);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ownerName = snapshot.child("fullname").getValue(String.class);
                String ownerProfilePic = snapshot.child("imageurl").getValue(String.class);
                Glide.with(Property_details.this).load(ownerProfilePic).into(profileImage);
                ownerName_txt.setText(ownerName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}