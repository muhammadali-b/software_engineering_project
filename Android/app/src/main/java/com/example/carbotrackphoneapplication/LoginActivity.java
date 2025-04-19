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
import android.widget.EditText;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {


    private String userRole = "";

    EditText etEmail, etPassword;
    Button btnLogin;
    ImageView imgTogglePassword;

    // Here is the URL of the RestAPI with the endpoint to login an employee or an employer after they register and get the appropriate approval.
    String apiURL = "https://softwareengineeringproject-production.up.railway.app/api/login";

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

        //Here we are making sure that the user enters in both their email and password.
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Please enter in both your email and password.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                loginUser(email, password);
            }
        });

        // Here is where we are adding a click event for when users will have to create an account
        TextView txtSignup = findViewById(R.id.txt_signup);
        txtSignup.setOnClickListener(view -> {
            Intent onboardingIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(onboardingIntent);
        });


//        // Here is where users will get directed to the forgot password page if users forget their password to login.
//        TextView txtForgotPassword = findViewById(R.id.txt_forgot_password);
//        txtForgotPassword.setOnClickListener(view -> {
//            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
//            startActivity(intent);
//        });

        // Here is where we are going to add the functionality of having users see the password that they are typing using the eye icon.
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

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

    // Here is where we are integrating our RESTAPI and backend to successfully login the user after making an account on the app.
    private void loginUser(String email, String password)
    {
        new Thread (() -> {
            try {
                // Here we are setting up the API endpoint as a post to log users in on the app.
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // Here are the parameters that we are looking for in JSON format.
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("email", email);
                jsonParam.put("password", password);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 300) ? conn.getInputStream() : conn.getErrorStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder();
                String line;
                while((line = reader.readLine()) != null)
                {
                    response.append(line);
                }
                reader.close();

                JSONObject result = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    if (code == 200 || code == 201)
                    {
                        // Here is where we are holding the role that came from the signup page.
                        String role = result.optString("role", "");
                        boolean isApproved = result.optBoolean("approved", false);

                        // Here we are checking to see if the employee/employer is approved before logging.
                        if (!isApproved)
                        {
                            Toast.makeText(this, "Approval is needed before you can log in.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Here we are making sure the employee/employer can login after they get their registration approved.
                        if (role.equalsIgnoreCase("employee"))
                        {
                            Intent employee_intent = new Intent(LoginActivity.this, Employeedaily_Activity.class);
                            startActivity(employee_intent);

                        }
                        else if (role.equalsIgnoreCase("employer"))
                        {
                            //Intent employer_intent = new Intent(LoginActivity.this, );
                            //startActivity(employer_intent);
                        }
                        else
                        {
                            Toast.makeText(this,"Your role is unknown. Please contact the System Admin.", Toast.LENGTH_SHORT).show(); // Check to make sure only an employee/employer is logging in
                        }
                        finish();

                    }
                    else
                    {
                        Toast.makeText(this, "Login failed: " + result.optString("message", "Credentials were invalid"), Toast.LENGTH_SHORT).show();
                    }

                });
                conn.disconnect();

            } catch (Exception e) {
                Log.e("LoginError", "Error: ", e);
                runOnUiThread(() ->
                    Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

            }
        }).start();
    }
}