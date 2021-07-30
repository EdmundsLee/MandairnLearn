package com.example.mandairnlearn;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email_txt, password_txt;
    private Button login_btn, register_btn;
    public Boolean is_error;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TODO: Initialize Activity Toolbar
        toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Log-in");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
        });

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){
            finish();
        }

        email_txt = (EditText) findViewById(R.id.edit_Login_Username);
        password_txt = (EditText) findViewById(R.id.edit_Login_Password);
        login_btn = (Button) findViewById(R.id.btn_Login_Submit);
        register_btn = (Button) findViewById(R.id.btn_Login_Register);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_txt.getText().toString();
                final String password = password_txt.getText().toString();

                if(TextUtils.isEmpty(email)){
                    email_txt.setError("Please enter your e-mail");
                    email_txt.requestFocus();
                    is_error = true;
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    password_txt.setError("Please enter your password");
                    password_txt.requestFocus();
                    is_error = true;
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()){
                                    if (password.length() < 8){
                                        password_txt.setError("Password should not be less than 8 characters");
                                        password_txt.requestFocus();
                                        is_error = true;
                                    }
                                    else {
                                        password_txt.setError("Credentials are incorrect, check e-mail or password");
                                        password_txt.requestFocus();
                                        is_error = true;
                                    }
                                }
                                else {
                                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });



        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }
}
