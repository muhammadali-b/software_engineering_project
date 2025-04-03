// Here is the Java code for the signup page
package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.text.Html;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Here is where we are adding a click event for when users will have to create an account
        TextView txtSignup = findViewById(R.id.txt_signup);
        txtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
            startActivity(intent);
        });

        //Here is where users will get directed to the home page of the app when users login
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
            startActivity(intent);
        });

        // Here is where users will get directed to the forgot password page if users forget their password to login.
        TextView txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


    }
}