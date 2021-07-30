
package com.example.mandairnlearn;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email_txt, password_txt, confirm_txt;
    private Button submit_btn;
    public Boolean is_error;
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private String email, password, confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Register");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {finish();}
        });

        email_txt =     (EditText) findViewById(R.id.edit_Register_Email);
        password_txt =  (EditText) findViewById(R.id.edit_Register_Password);
        confirm_txt =   (EditText) findViewById(R.id.edit_Register_Confirm);
        submit_btn =    (Button)   findViewById(R.id.btn_Register_Submit);

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email =     email_txt.getText().toString().trim();
                password =  password_txt.getText().toString().trim();
                confirm =  confirm_txt.getText().toString().trim();


                if (TextUtils.isEmpty(email)){
                    email_txt.setError("Please enter your e-mail");
                    email_txt.requestFocus();
                    is_error = true;
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    password_txt.setError("Please enter your password");
                    password_txt.requestFocus();
                    is_error = true;
                    return;
                }
                if (TextUtils.isEmpty(confirm)){
                    confirm_txt.setError("Please confirm your password");
                    confirm_txt.requestFocus();
                    is_error = true;
                    return;
                }
                if (password.length() < 8){
                    password_txt.setError("Password should not be less than 8 characters");
                    password_txt.requestFocus();
                    is_error = true;
                    return;
                }
                if (!password.equals(confirm)){
                    confirm_txt.setError("Passwords does not match");
                    confirm_txt.requestFocus();
                    is_error = true;
                    return;
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(RegisterActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();

                                if (!task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
