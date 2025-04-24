package com.example.carbotrackphoneapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmployeeProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageView profileImageView;
    TextView userName, userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        LinearLayout activityBtn = findViewById(R.id.activityBtn);
        LinearLayout changePasswordBtn = findViewById(R.id.changePasswordBtn);
        LinearLayout logoutBtn = findViewById(R.id.logoutBtn);

        profileImageView = findViewById(R.id.profileImageView);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

        // Load image if previously saved
        loadSavedProfileImage();

        // Fetch employee info
        fetchEmployeeInfo();

        // Image picker
        profileImageView.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        activityBtn.setOnClickListener(v -> startActivity(new Intent(this, TravelHistoryActivity.class)));
        changePasswordBtn.setOnClickListener(v -> openChangePasswordModal());

        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, OnboardingActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, Employeedaily_Activity.class));
                return true;
            } else if (id == R.id.nav_travel) {
                startActivity(new Intent(this, InitialTravelActivity.class));
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void fetchEmployeeInfo() {
        new Thread(() -> {
            try {
                SharedPreferences prefs = getSharedPreferences("CarboTrackPrefs", MODE_PRIVATE);
                int employeeId = prefs.getInt("employee_id", -1);
                if (employeeId == -1) return;

                URL url = new URL("https://softwareengineeringproject-production.up.railway.app/api/approved-employees");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder jsonResponse = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) jsonResponse.append(line);
                in.close();

                JSONObject response = new JSONObject(jsonResponse.toString());
                JSONArray employees = response.getJSONArray("data");

                for (int i = 0; i < employees.length(); i++) {
                    JSONObject emp = employees.getJSONObject(i);
                    if (emp.getInt("id") == employeeId) {
                        String fullName = emp.getString("f_name") + " " + emp.getString("l_name");
                        String email = emp.getString("email");

                        runOnUiThread(() -> {
                            userName.setText(fullName);
                            userEmail.setText(email);
                        });
                        break;
                    }
                }

            } catch (Exception e) {
                Log.e("ProfileError", "Error fetching info: " + e.getMessage());
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to fetch profile info.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }


    private void loadSavedProfileImage() {
        SharedPreferences prefs = getSharedPreferences("CarboTrackPrefs", MODE_PRIVATE);
        String imageBase64 = prefs.getString("profile_image_base64", null);
        if (imageBase64 != null) {
            byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            profileImageView.setImageBitmap(bitmap);
        }
    }

    private void saveProfileImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
        String imageBase64 = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        getSharedPreferences("CarboTrackPrefs", MODE_PRIVATE)
                .edit()
                .putString("profile_image_base64", imageBase64)
                .apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                profileImageView.setImageBitmap(bitmap);
                saveProfileImage(bitmap);
            } catch (Exception e) {
                Log.e("ImageUpload", "Error: " + e.getMessage());
            }
        }
    }

    private void openChangePasswordModal() {
        View modalView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

        EditText currentPass = modalView.findViewById(R.id.currentPassword);
        EditText newPass = modalView.findViewById(R.id.newPassword);
        EditText repeatPass = modalView.findViewById(R.id.repeatPassword);
        ImageView toggleEye1 = modalView.findViewById(R.id.toggleEye1);
        ImageView toggleEye2 = modalView.findViewById(R.id.toggleEye2);
        ImageView toggleEye3 = modalView.findViewById(R.id.toggleEye3);
        ImageView backArrow = modalView.findViewById(R.id.backArrow);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(modalView).setCancelable(true).create();

        backArrow.setOnClickListener(view -> dialog.dismiss());

        setPasswordToggle(toggleEye1, currentPass);
        setPasswordToggle(toggleEye2, newPass);
        setPasswordToggle(toggleEye3, repeatPass);

        Button saveBtn = modalView.findViewById(R.id.savePasswordBtn);
        saveBtn.setOnClickListener(v -> {
            if (validatePassword(currentPass, newPass, repeatPass)) {
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setPasswordToggle(ImageView toggleIcon, EditText field) {
        toggleIcon.setOnClickListener(v -> {
            if (field.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_closed);
            } else {
                field.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_open);
            }
            field.setSelection(field.getText().length());
        });
    }

    private boolean validatePassword(EditText current, EditText newP, EditText repeat) {
        String cur = current.getText().toString();
        String np = newP.getText().toString();
        String rp = repeat.getText().toString();

        if (cur.isEmpty() || np.isEmpty() || rp.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (np.equals(cur)) {
            Toast.makeText(this, "New password must be different.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!np.equals(rp)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
