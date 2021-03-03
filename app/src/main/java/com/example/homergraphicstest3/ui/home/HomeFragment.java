package com.example.homergraphicstest3.ui.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.homergraphicstest3.Home_Rec_View_Adpt;
import com.example.homergraphicstest3.Properties;
import com.example.homergraphicstest3.R;
import com.example.homergraphicstest3.ui.Property_details;
import com.example.homergraphicstest3.ui.dialog.ShortlistItemDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    DatabaseReference reference;
    RecyclerView recyclerView;
    Home_Rec_View_Adpt adapter;
    ArrayList<Properties> propertyArrayList;
    Properties properties;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        reference = FirebaseDatabase.getInstance().getReference().child("Properties");

        recyclerView = view.findViewById(R.id.home_recyclervView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                propertyArrayList = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    properties = new Properties();
                    properties.setProp_ID(dataSnapshot.child("PropertyID").getValue(String.class));
                    properties.setOwner_ID(dataSnapshot.child("OwnerID").getValue(String.class));
                    properties.setDescription(snapshot.child("Description").getValue(String.class));
                    properties.setAddLine_1(dataSnapshot.child("AddressLine1").getValue(String.class));
                    properties.setAddLine_2(dataSnapshot.child("AddressLine2").getValue(String.class));
                    properties.setCity(dataSnapshot.child("City").getValue(String.class));
                    properties.setPostCode(dataSnapshot.child("PostCode").getValue(String.class));
                    properties.setPrice(dataSnapshot.child("Price").getValue(String.class));
                    properties.setPayScheme(dataSnapshot.child("PayScheme").getValue(String.class));
                    properties.setBillsIncluded(dataSnapshot.child("BillsIncluded").getValue(String.class));
                    properties.setFurnished(dataSnapshot.child("Furnished").getValue(String.class));
                    properties.setImgUrl(dataSnapshot.child("ImageUrl").getValue(String.class));
                    propertyArrayList.add(properties);
                }
                adapter = new Home_Rec_View_Adpt(getContext(), propertyArrayList);
                adapter.setOnItemClickListener(new Home_Rec_View_Adpt.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        String propID = propertyArrayList.get(position).getProp_ID();
                        Intent intent = new Intent(getActivity(), Property_details.class);
                        intent.putExtra("propID", propID);
                        startActivity(intent);
                    }
                });
                adapter.setOnItemLongClickListener(new Home_Rec_View_Adpt.OnLongClickListener() {
                    @Override
                    public void onLongClick(int position) {
                        Properties mProperty = propertyArrayList.get(position);
                        ShortlistItemDialog shortlistItemDialog = ShortlistItemDialog.newInstance(mProperty);
                        shortlistItemDialog.show(getParentFragmentManager(), shortlistItemDialog.getTag());
                    }
                });
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        return view;
    }
}