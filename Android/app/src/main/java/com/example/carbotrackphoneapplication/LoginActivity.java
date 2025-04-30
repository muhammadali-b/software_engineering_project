// Here is the java code for the login page of the application.
package com.example.carbotrackphoneapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginActivity extends AppCompatActivity {

    private String userRole = "";

    EditText etEmail, etPassword;
    Button btnLogin;
    ImageView imgTogglePassword;

    String apiURL = "https://softwareengineeringproject-production.up.railway.app/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("role")) {
            userRole = intent.getStringExtra("role");
            Toast.makeText(this, "Registered as: " + userRole, Toast.LENGTH_SHORT).show();
        }

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btn_login);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter in both your email and password.", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });

        TextView txtSignup = findViewById(R.id.txt_signup);
        txtSignup.setOnClickListener(view -> {
            Intent onboardingIntent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(onboardingIntent);
        });


        imgTogglePassword.setOnClickListener(view -> {
            if (etPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgTogglePassword.setImageResource(R.drawable.ic_eye_closed);
            }
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void loginUser(String email, String password) {
        new Thread(() -> {
            try {
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

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
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject result = new JSONObject(response.toString());

                runOnUiThread(() -> {
                    if (code == 200 || code == 201) {
                        JSONObject user = result.optJSONObject("user");

                        if (user == null) {
                            Toast.makeText(this, "Invalid user data returned.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String role = user.optString("role", "");

                        boolean isApproved = true;


                        if (!isApproved) {
                            Toast.makeText(this, "Approval is needed before you can log in.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        getSharedPreferences("CarboPrefs", MODE_PRIVATE).edit()
                                .putInt("user_id", user.optInt("id"))
                                .putString("first_name", user.optString("first_name"))
                                .putString("last_name", user.optString("last_name"))
                                .putString("email", user.optString("email"))
                                .putString("role", user.optString("role"))
                                .apply();

                        if (role.equalsIgnoreCase("employee")) {
                            startActivity(new Intent(LoginActivity.this, Employeedaily_Activity.class));
                        } else if (role.equalsIgnoreCase("employer")) {
                            startActivity(new Intent(LoginActivity.this, EmployerHomeActivity.class));
                        } else {
                            Toast.makeText(this, "Unknown role. Contact admin.", Toast.LENGTH_SHORT).show();
                        }
                        finish();

                    } else {
                        Toast.makeText(this, "Login failed: " + result.optString("message", "Credentials were invalid"), Toast.LENGTH_SHORT).show();
                    }
                });


                conn.disconnect();

            } catch (Exception e) {
                Log.e("LoginError", "Error: ", e);
                runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void setPasswordToggle(ImageView toggleIcon, EditText passwordField) {
        toggleIcon.setOnClickListener(view -> {
            if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_closed);
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_open);
            }
            passwordField.setSelection(passwordField.getText().length());
        });
    }

}
