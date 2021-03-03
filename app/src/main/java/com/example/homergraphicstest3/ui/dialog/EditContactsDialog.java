package com.example.homergraphicstest3.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.homergraphicstest3.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditContactsDialog extends BottomSheetDialogFragment {
    public static EditContactsDialog newInstance() {
        EditContactsDialog fragment = new EditContactsDialog();
        return fragment;
    }

    @Override
    public void setupDialog(@NonNull Dialog dialog, int style) {
        View view = View.inflate(getContext(), R.layout.edit_contacts_dialog, null);
        dialog.setContentView(view);
        ((View) view.getParent()).setBackground(new ColorDrawable(Color.TRANSPARENT));
    }
}
