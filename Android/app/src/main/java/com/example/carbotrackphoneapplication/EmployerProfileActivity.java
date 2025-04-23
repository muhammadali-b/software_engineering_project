package com.example.carbotrackphoneapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployerProfileActivity extends AppCompatActivity {

    TextView userName, userEmail;
    LinearLayout changePasswordBtn, logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        changePasswordBtn = findViewById(R.id.changePasswordBtn);
        logoutBtn = findViewById(R.id.logoutBtn);

        BottomNavigationView bottomNavigationView = findViewById(R.id.employer_bottom_navigation);

        // Here we are making sure that when a user is on a page from clicking it on the navigation bar, that page stays highlighted.
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);

        // Dummy user info
        userName.setText("Abcxyz");
        userEmail.setText("abcxyz@gmail.com");

        // Open password modal
        changePasswordBtn.setOnClickListener(view -> openChangePasswordModal());

        // Logout action
        logoutBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EmployerProfileActivity.this, OnboardingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears back stack
            startActivity(intent);
            finish(); // finish current activity
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
            {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerHomeActivity.class));
                return true;
            }
            else if(id == R.id.nav_activity)
            {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerActivity.class));
                return true;

            }
            else if (id == R.id.nav_profile)
            {
                return true;
            }
            return false;
        });
    }

    private void openChangePasswordModal() {
        View modalView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

        EditText currentPass = modalView.findViewById(R.id.currentPassword);
        EditText newPass = modalView.findViewById(R.id.newPassword);
        EditText repeatPass = modalView.findViewById(R.id.repeatPassword);

        ImageView toggleEye1 = modalView.findViewById(R.id.toggleEye1);
        ImageView toggleEye2 = modalView.findViewById(R.id.toggleEye2);
        ImageView toggleEye3 = modalView.findViewById(R.id.toggleEye3);

        // Show dialog
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(modalView)
                .setCancelable(true)
                .create();

        // 👈 Handle top-left back arrow to dismiss modal
        ImageView backArrow = modalView.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(view -> dialog.dismiss());

        // 👁 Toggle visibility on password fields
        setPasswordToggle(toggleEye1, currentPass);
        setPasswordToggle(toggleEye2, newPass);
        setPasswordToggle(toggleEye3, repeatPass);

        // 💾 Save logic
        Button saveBtn = modalView.findViewById(R.id.savePasswordBtn);
        saveBtn.setOnClickListener(v -> {
            if (validatePassword(currentPass, newPass, repeatPass)) {
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void setPasswordToggle(ImageView toggleIcon, EditText passwordField) {
        toggleIcon.setOnClickListener(view -> {
            if (passwordField.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_closed); // closed eye icon
            } else {
                passwordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                toggleIcon.setImageResource(R.drawable.ic_eye_open); // open eye icon
            }
            passwordField.setSelection(passwordField.getText().length());
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
