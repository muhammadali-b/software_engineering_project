// Here is the Java code for the onboarding page.
package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.text.Html;
import androidx.appcompat.app.AppCompatActivity;


public class OnboardingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Here is the functionality for the user to click on the Login Button.
        Button btnlogin = findViewById(R.id.btn_login);
        btnlogin.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);

        });

        // Here is the functionality for users to click on the sign up button if they don't have an account.
        TextView txtSignup = findViewById(R.id.txt_signup);
        txtSignup.setText(Html.fromHtml("Don't have an account? <b><font color='#3AAD67'>Sign Up</font></b>"));

        txtSignup.setOnClickListener(view -> {
            Intent intent = new Intent(OnboardingActivity.this, SignupActivity.class);
            startActivity(intent);
        });

    }
}