// Here is the Java code for the signup page.
package com.example.carbotrackphoneapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignupActivity extends AppCompatActivity {

    String[] roles = {"Employer", "Employee"};
    String selectedRole = "";

    AutoCompleteTextView autoCompleteTextView;
    EditText etfirstName, etlastName, etEmail, etPassword, etConfirmPassword;
    Button signUpButton;
    ImageView imgTogglePassword, imgToggleConfirmPassword;

    String apiUrl = "https://softwareengineeringproject-production.up.railway.app/api/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        etfirstName = findViewById(R.id.etfirstName);
        etlastName = findViewById(R.id.etlastName);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        signUpButton = findViewById(R.id.btnSignUp);
        imgTogglePassword = findViewById(R.id.imgTogglePassword);
        imgToggleConfirmPassword = findViewById(R.id.imgToggleConfirmPassword);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedRole = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(SignupActivity.this, "Selected Role: " + selectedRole, Toast.LENGTH_SHORT).show();
        });

        signUpButton.setOnClickListener(v -> {
            String first_name = etfirstName.getText().toString().trim();
            String last_name = etlastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            String confirmPassword = etConfirmPassword.getText().toString();

            if (selectedRole.isEmpty() || first_name.isEmpty() || last_name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill all fields and select a role.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(first_name, last_name, email, password, selectedRole);
        });

        TextView txtLogin = findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        //Here is the functionality of changing the visibility of passwords written
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

        imgToggleConfirmPassword.setOnClickListener(view -> {
            if (etConfirmPassword.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                imgToggleConfirmPassword.setImageResource(R.drawable.ic_eye_open);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                imgToggleConfirmPassword.setImageResource(R.drawable.ic_eye_closed);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });


    }


    private void registerUser(String first_name, String last_name, String email, String password, String role) {
        new Thread(() -> {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                // These will be the input fields for the Employee/Employer to enter their information when signing up on the app.
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("f_name", first_name);
                jsonParam.put("l_name", last_name);
                jsonParam.put("email", email);
                jsonParam.put("password", password);
                jsonParam.put("role", role.toLowerCase());

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

                runOnUiThread(() -> {
                    if (code == 200 || code == 201) {
                        Toast.makeText(SignupActivity.this, "Registered as " + role + ". Awaiting approval.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignupActivity.this, "Error: " + response.toString(), Toast.LENGTH_LONG).show();
                    }
                });

                conn.disconnect();

            } catch (Exception e) {
                Log.e("SignupError", "Error: ", e);
                runOnUiThread(() -> Toast.makeText(SignupActivity.this, "Exception: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }
}
