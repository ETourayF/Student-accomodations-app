package com.example.homergraphicstest3.ui.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.homergraphicstest3.R;
import com.example.homergraphicstest3.ui.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class UserRegistration extends AppCompatActivity implements View.OnClickListener {

    Button regBtn;
    EditText regName, regEmail, regPwd, regPwd2;
    private FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration);

        regBtn = findViewById(R.id.regSignUp_btn);
        regName =findViewById(R.id.regName_edt);
        regEmail =findViewById(R.id.regEmail_edt);
        regPwd =findViewById(R.id.regPwd_edt);
        regPwd2 =findViewById(R.id.regConfirmPwd_edt);

        regBtn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = findViewById(R.id.userRegistration_Toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int[] scrcoords = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.regSignUp_btn:
                registerUser();
        }
    }

    public void registerUser(){
        final String name = regName.getText().toString().trim();
        final String email = regEmail.getText().toString().trim();
        final String password = regPwd.getText().toString().trim();
        final String password2 = regPwd2.getText().toString().trim();

        if(email.isEmpty() || name.isEmpty() || password.isEmpty() || !password2.equals(password)){
            showMessage("please complete all fields");
        }
        else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String userID = firebaseUser.getUid();
                        String profilePicUrl = "https://firebasestorage.googleapis.com/v0/b/homer-8e526.appspot.com/o/guest_profile.png?alt=media&token=0e5b46d0-766a-4b2e-9417-54477f4e7fd5";

                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", userID);
                        hashMap.put("fullname", name);
                        hashMap.put("Email", email);
                        hashMap.put("Phone", "");
                        hashMap.put("imageurl", profilePicUrl);

                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    updateUserInfo(name, password, email, mAuth.getCurrentUser());
                                }
                                else{
                                    showMessage("error" + task.getException().getMessage());
                                }
                            }
                        });
                    }
                    else{
                        showMessage("registration failed" + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void updateUserInfo(String name, final String password, final String email, FirebaseUser currentUser) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        currentUser.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    showMessage("Welcome");
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                updateUI();
                            }
                            else{
                                showMessage("something went wrong" + task.getException().getMessage());
                            }
                        }
                    });
                }
                else{
                    return;
                }
            }
        });
    }

    private void updateUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
