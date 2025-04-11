// Here is the Java code for the signup page
package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.text.Html;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // Here is where we are holding the role that came from the signup page.
    private String userRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Here is where we are getting the role of the user if they came from the signup page.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("role"))
        {
            userRole = intent.getStringExtra("role");
            Toast.makeText(this, "Registered as: " + userRole, Toast.LENGTH_SHORT).show();
        }

        // Here is where we are adding a click event for when users will have to create an account
        TextView txtSignup = findViewById(R.id.txt_signup);
        txtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
            startActivity(intent);
        });

        // Here is logic that will sign in the user to the appropriate home page on the app.
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(view -> {
            // Add logic to validate the password of the user.

            if(userRole.equalsIgnoreCase("Employer"))
            {
                Intent employer_intent = new Intent(LoginActivity.this, );
                startActivity(employer_intent);
            }
            else
            {
                Intent employee_intent = new Intent(LoginActivity.this, );
                startActivity(employee_intent);
            }
            finish();
        });

        // Here is where users will get directed to the forgot password page if users forget their password to login.
        TextView txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Here is where we are going to add the functionality of having users see the password that they are typing using the eye icon.
        EditText etPassword = findViewById(R.id.etPassword);
        ImageView imgTogglePassword = findViewById(R.id.imgTogglePassword);

        imgTogglePassword.setOnClickListener(view -> {
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD))
            {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye_open);
            }
            else
            {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            etPassword.setSelection(etPassword.getText().length());
        });


    }
}