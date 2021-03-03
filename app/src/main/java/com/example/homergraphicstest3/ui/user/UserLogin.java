package com.example.homergraphicstest3.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLogin extends AppCompatActivity implements View.OnClickListener {

    Button loginReg_btn, signIn_btn;
    TextView loginEmail_edt, loginPwd_edt;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login);

        loginEmail_edt = findViewById(R.id.loginEmail_edt);
        loginPwd_edt = findViewById(R.id.loginPwd_edt);

        loginReg_btn = findViewById(R.id.loginReg_btn);
        loginReg_btn.setOnClickListener(this);

        signIn_btn = findViewById(R.id.signIn_btn);
        signIn_btn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.userLogin_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
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
            case R.id.loginReg_btn:
                Intent intent = new Intent(this,UserRegistration.class);
                startActivity(intent);

            case R.id.signIn_btn:
                login();

        }
    }

    private void login() {


        String email = loginEmail_edt.getText().toString().trim();
        String pwd = loginPwd_edt.getText().toString().trim();

        if(email.isEmpty() || pwd.isEmpty()){
            showMessage("please complete all fields");
        }
        else {
            mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
//                        reference.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                updateUI();
//                                finish();
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                            }
//                        });
                        updateUI();
                    }
                    else{
                        showMessage(task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message , Toast.LENGTH_SHORT).show();
    }

    private void updateUI() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
