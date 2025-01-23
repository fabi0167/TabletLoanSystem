package com.example.tabletloansystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText editTextBorrowerName, editTextContactInfo;
    private Spinner spinnerTabletBrand, spinnerCableType;
    private Button buttonRegisterLoan;
    private LoanDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new LoanDatabaseHelper(this);

        // Initialize UI elements
        editTextBorrowerName = findViewById(R.id.editTextBorrowerName);
        editTextContactInfo = findViewById(R.id.editTextContactInfo);
        spinnerTabletBrand = findViewById(R.id.spinnerTabletBrand);
        spinnerCableType = findViewById(R.id.spinnerCableType);
        buttonRegisterLoan = findViewById(R.id.buttonRegisterLoan);

        // Populate spinners
        String[] tabletBrands = {"Acer", "Samsung"};
        ArrayAdapter<String> adapterTabletBrand = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tabletBrands);
        adapterTabletBrand.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTabletBrand.setAdapter(adapterTabletBrand);

        String[] cableTypes = {"None", "USB-C", "Micro-USB"};
        ArrayAdapter<String> adapterCableType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cableTypes);
        adapterCableType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCableType.setAdapter(adapterCableType);

        // Set up button click listener
        buttonRegisterLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLoan();
            }
        });
    }

    /**
     * ✅ Registers a loan in the database.
     */
    private void registerLoan() {
        String borrowerName = editTextBorrowerName.getText().toString();
        String contactInfo = editTextContactInfo.getText().toString();
        String tabletBrand = spinnerTabletBrand.getSelectedItem().toString();
        String cableType = spinnerCableType.getSelectedItem().toString();

        // ✅ Ensure required fields are filled
        if (borrowerName.isEmpty() || contactInfo.isEmpty()) {
            Toast.makeText(this, "Borrower's name and contact info are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // ✅ Store loan in the database
        dbHelper.addLoan(tabletBrand, cableType.equals("None") ? null : cableType, borrowerName, contactInfo, currentDate);

        // ✅ Show confirmation screen
        Intent intent = new Intent(MainActivity.this, LoanConfirmationActivity.class);
        intent.putExtra("loan_summary", "📋 **Loan Confirmation**\n\n"
                + "📱 Tablet: " + tabletBrand + "\n"
                + "🔌 Cable: " + cableType + "\n"
                + "👤 Borrower: " + borrowerName + "\n"
                + "📧 Contact: " + contactInfo + "\n"
                + "📅 Date: " + currentDate);
        startActivity(intent);
    }
}
