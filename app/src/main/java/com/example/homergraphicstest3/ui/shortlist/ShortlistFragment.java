package com.example.homergraphicstest3.ui.shortlist;

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

import com.example.homergraphicstest3.Home_Rec_View_Adpt;
import com.example.homergraphicstest3.Properties;
import com.example.homergraphicstest3.R;
import com.example.homergraphicstest3.ui.Property_details;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShortlistFragment extends Fragment {
    RecyclerView shortlist_recyclerView;
    ArrayList<Properties> shortlistArrayList;
    Home_Rec_View_Adpt adapter;
    Properties property;
    DatabaseReference mReference;
    FirebaseUser currentUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shortlist, container, false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        shortlist_recyclerView = view.findViewById(R.id.shortlist_recyclervView);
        shortlist_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shortlistArrayList = new ArrayList<>();

        getShortlistedID(new GetShortlistedIDCallback() {
            @Override
            public void onCallback(final ArrayList<String> IDArrayList) {
                for(final String id : IDArrayList){
                    getProperties(new GetPropertiesCallback() {
                        @Override
                        public void onCallback(Properties property) {
                            shortlistArrayList.add(property);
                            saveList(shortlistArrayList);
                            if(shortlistArrayList.size() == IDArrayList.size()){
                                adapter = new Home_Rec_View_Adpt(getContext(), shortlistArrayList);
                                adapter.setOnItemClickListener(new Home_Rec_View_Adpt.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(int position) {
                                        String propID = shortlistArrayList.get(position).getProp_ID();
                                        Intent intent = new Intent(getActivity(), Property_details.class);
                                        intent.putExtra("propID", propID);
                                        startActivity(intent);
                                    }
                                });
                                new ItemTouchHelper(simpleCallback).attachToRecyclerView(shortlist_recyclerView);
                                shortlist_recyclerView.setAdapter(adapter);
                            }
                        }
                    }, id);
                }
            }
        });
        return view;
    }

    private void saveList(ArrayList<Properties> shortlistArrayList) {
        this.shortlistArrayList = shortlistArrayList;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
            final int itemPosition = viewHolder.getAdapterPosition();
            final String propID = shortlistArrayList.get(viewHolder.getAdapterPosition()).getProp_ID();
            final Properties removedItem = shortlistArrayList.get(viewHolder.getAdapterPosition());
            shortlistArrayList.remove(viewHolder.getAdapterPosition());
            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            mReference.child("ShortlistData").child(currentUser.getUid()).child(propID).removeValue();

            final Snackbar snackbar = Snackbar.make(viewHolder.itemView, "Removed", Snackbar.LENGTH_SHORT);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mReference.child("ShortlistData").child(currentUser.getUid()).child(propID).setValue(propID);
                    shortlistArrayList.add(itemPosition, removedItem);
                    adapter.notifyItemInserted(itemPosition);
                    shortlist_recyclerView.scrollToPosition(itemPosition);
                }
            });
            snackbar.show();
        }
    };

    private void getShortlistedID(final GetShortlistedIDCallback getShortlistedIDCallback){
        mReference.child("ShortlistData").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> tmpID_list = new ArrayList<>();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    tmpID_list.add(dataSnapshot.getValue().toString());
                }
                getShortlistedIDCallback.onCallback(tmpID_list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProperties(final GetPropertiesCallback getPropertiesCallback, final String id){
        mReference.child("Properties").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                property = new Properties();
                property.setProp_ID(snapshot.child("PropertyID").getValue(String.class));
                property.setOwner_ID(snapshot.child("OwnerID").getValue(String.class));
                property.setDescription(snapshot.child("Description").getValue(String.class));
                property.setAddLine_1(snapshot.child("AddressLine1").getValue(String.class));
                property.setAddLine_2(snapshot.child("AddressLine2").getValue(String.class));
                property.setCity(snapshot.child("City").getValue(String.class));
                property.setPostCode(snapshot.child("PostCode").getValue(String.class));
                property.setPrice(snapshot.child("Price").getValue(String.class));
                property.setPayScheme(snapshot.child("PayScheme").getValue(String.class));
                property.setBillsIncluded(snapshot.child("BillsIncluded").getValue(String.class));
                property.setFurnished(snapshot.child("Furnished").getValue(String.class));
                property.setImgUrl(snapshot.child("ImageUrl").getValue(String.class));
                getPropertiesCallback.onCallback(property);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface GetShortlistedIDCallback{
        void onCallback(ArrayList<String> list);
    }

    private interface GetPropertiesCallback{
        void onCallback(Properties property);
    }
}