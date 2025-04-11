package com.example.carbotrackphoneapplication;

import android.os.Bundle;
import android.content.Intent;
import android.text.InputType;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.text.Html;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsCompat.Insets;

public class SignupActivity extends AppCompatActivity {

    String[] roles = {"Employer", "Employee"};
    String selectedRole="";

    AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Here is where we are creating the dropdown menu for users to choose whether they are an Employee or an Employer.
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        Button signUpButton = findViewById(R.id.btnSignUp);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roles);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener((adapterView, view, position, id) -> {
            selectedRole = adapterView.getItemAtPosition(position).toString();
            Toast.makeText(SignupActivity.this, "Selected Role: " + selectedRole, Toast.LENGTH_SHORT).show();
        });


        signUpButton.setOnClickListener(view -> {
            //Here we are checking to see if the user has selected a user role.
            if (selectedRole.isEmpty())
            {
                Toast.makeText(SignupActivity.this, "Please select a role.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Here is where the logic for if an Employer registers on the platform, then a Carbon Credit Bank will have to approve that registration.
            // If an Employee registers on the platform then the Employer will have to go and approve the registration of the Employee.
            if(selectedRole.equals("Employer"))
            {
                Toast.makeText(SignupActivity.this, "Signed up as an Employer. Now awaiting Carbon Credit Bank approval.", Toast.LENGTH_LONG).show();
                //Once approved then do the lines below
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else if (selectedRole.equals("Employee"))
            {
                Toast.makeText(SignupActivity.this, "Signed up as an Employee. Now awaiting Employer approval.", Toast.LENGTH_LONG).show();
                //Once approved then do the lines below
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

        });

        // Here is where users will get directed straight to the login page if they have an account already on the app.
        TextView txt_Login = findViewById(R.id.txtLogin);
        txt_Login.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });

    }
}