package com.example.carbotrackphoneapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Random;
import de.hdodenhof.circleimageview.CircleImageView;

public class EmployerHomeActivity extends AppCompatActivity {

    Spinner employeeSpinner;
    TextView employeeName, employeeEmail, employeeMiles, employeeCredits, employeeTotal;
    CircleImageView employeeImage;
    Button buyBtn, sellBtn;

    private int creditQuantity = 15;
    private String transactionType = "buy"; // "buy" or "sell"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Here we are making sure that when a user is on a page from clicking it on the navigation bar, that page stays highlighted.
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        // Initialize UI references
        employeeSpinner = findViewById(R.id.employeeSpinner);
        employeeName = findViewById(R.id.employeeName);
        employeeEmail = findViewById(R.id.employeeEmail);
        employeeMiles = findViewById(R.id.employeeMiles);
        employeeCredits = findViewById(R.id.employeeCredits);
        employeeTotal = findViewById(R.id.employeeTotal);
        employeeImage = findViewById(R.id.employeeImage);
        buyBtn = findViewById(R.id.buyBtn);
        sellBtn = findViewById(R.id.sellBtn);

        // Setup Spinner adapter using string-array
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.employee_list,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        employeeSpinner.setAdapter(adapter);

        // Spinner selection logic
        employeeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                updateEmployeeCard(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Buy Button
        buyBtn.setOnClickListener(v -> {
            transactionType = "buy";
            openBuySellModal();
        });

        // Sell Button
        sellBtn.setOnClickListener(v -> {
            transactionType = "sell";
            openBuySellModal();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home)
            {
                startActivity(new Intent(EmployerHomeActivity.this, EmployerHomeActivity.class));
                return true;
            }
            else if(id == R.id.nav_activity)
            {
                startActivity(new Intent(EmployerHomeActivity.this, EmployerActivity.class));
                return true;

            }
            else if (id == R.id.nav_profile)
            {
                startActivity(new Intent(EmployerHomeActivity.this, EmployerProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void updateEmployeeCard(String name) {
        employeeName.setText(name);
        employeeEmail.setText(name.toLowerCase() + "@trackcarbon.com");
        employeeMiles.setText("900");
        employeeCredits.setText("1000");
        employeeTotal.setText("Total: 2000");

        employeeImage.setImageResource(R.drawable.person_24);
    }

    private void openBuySellModal() {
        View modalView = LayoutInflater.from(this).inflate(R.layout.dialog_buy_sell_modal, null);

        TextView modalTitle = modalView.findViewById(R.id.modalTitle);
        TextView dropdownLabel = modalView.findViewById(R.id.dropdownLabel);
        TextView quantityText = modalView.findViewById(R.id.quantityText);
        Spinner partnerSpinner = modalView.findViewById(R.id.partnerSpinner);
        ImageButton incrementBtn = modalView.findViewById(R.id.incrementBtn);
        ImageButton decrementBtn = modalView.findViewById(R.id.decrementBtn);
        Button cancelBtn = modalView.findViewById(R.id.cancelBtn);
        Button checkoutBtn = modalView.findViewById(R.id.checkoutBtn);

        modalTitle.setText(transactionType.equals("buy") ? "Buy Carbon Credits" : "Sell Carbon Credits");
        dropdownLabel.setText(transactionType.equals("buy") ? "From" : "To");

        quantityText.setText(String.valueOf(creditQuantity));

        String[] partners = {"ABC", "XYZ", "SOS", "LOL", "SUM", "SUB", "MUL", "DIV", "BTS"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, partners);
        partnerSpinner.setAdapter(adapter);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(modalView)
                .setCancelable(true)
                .create();

        incrementBtn.setOnClickListener(v -> {
            creditQuantity++;
            quantityText.setText(String.valueOf(creditQuantity));
        });

        decrementBtn.setOnClickListener(v -> {
            if (creditQuantity > 1) {
                creditQuantity--;
                quantityText.setText(String.valueOf(creditQuantity));
            }
        });

        cancelBtn.setOnClickListener(v -> dialog.dismiss());

        checkoutBtn.setOnClickListener(v -> {
            dialog.dismiss();
            showTransactionSummary(partnerSpinner.getSelectedItem().toString(), creditQuantity);
        });

        dialog.show();
    }

    private void showTransactionSummary(String partner, int quantity) {
        View modalView = LayoutInflater.from(this).inflate(R.layout.dialog_transaction_summary, null);

        TextView transactionTitle = modalView.findViewById(R.id.transactionTitle);
        TextView txnNumber = modalView.findViewById(R.id.txnNumber);
        TextView txnQuantity = modalView.findViewById(R.id.txnQuantity);
        TextView txnPrice = modalView.findViewById(R.id.txnPrice);
        TextView txnTo = modalView.findViewById(R.id.txnTo);
        TextView txnTotal = modalView.findViewById(R.id.txnTotal);
        Button okBtn = modalView.findViewById(R.id.txnOkBtn);

        boolean isBuy = transactionType.equals("buy");
        double unitPrice = isBuy ? 0.95 : 0.85;
        double totalPrice = quantity * unitPrice;

        transactionTitle.setText(isBuy ? "Purchase Transaction" : "Sales Transaction");
        txnNumber.setText("Transaction No: " + (new Random().nextInt(900000) + 100000));
        txnQuantity.setText("Quantity: " + quantity + " Carbon Credits");
        txnPrice.setText("Price Traded: $" + String.format("%.2f", unitPrice) + " / 1 carbon credit");
        txnTo.setText((isBuy ? "From" : "To") + ": " + partner);
        txnTotal.setText("Total Price: $" + String.format("%.2f", totalPrice));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(modalView)
                .setCancelable(true)
                .create();

        okBtn.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
