package com.example.homergraphicstest3.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.homergraphicstest3.Properties;
import com.example.homergraphicstest3.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShortlistItemDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    Button shortlistItem_btn, dismissShortlist_btn;
    static Properties mProperty;
    DatabaseReference mReference;
    FirebaseUser currentUser;

    public static ShortlistItemDialog newInstance(Properties prop) {
        ShortlistItemDialog fragment = new ShortlistItemDialog();
        mProperty = prop;
        return fragment;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.shortlist_bottom_dialog, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackground(new ColorDrawable(Color.TRANSPARENT));

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        mReference = FirebaseDatabase.getInstance().getReference();

        shortlistItem_btn = view.findViewById(R.id.shortlistItem_btn);
        shortlistItem_btn.setOnClickListener(this);
//        dismissShortlist_btn = view.findViewById(R.id.dismissShortlist_btn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.shortlistItem_btn:
                saveToShortlist();
        }
    }

    private void saveToShortlist() {
        mReference.child("ShortlistData").child(currentUser.getUid()).child(mProperty.getProp_ID()).
                setValue(mProperty.getProp_ID()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                showMessage("Added to shortlist");
                dismiss();
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
