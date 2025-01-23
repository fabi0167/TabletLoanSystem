package com.example.tabletloansystem;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private ListView listViewLoans;
    private Spinner spinnerTabletBrand, spinnerCableType;
    private EditText editTextStartDate, editTextEndDate;
    private Button buttonFilter, buttonClearFilters, buttonReturn;
    private LoanDatabaseHelper dbHelper;
    private List<String> loansList;
    private LoanAdapter adapter;
    private int selectedLoanId = -1; // Stores selected loan ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        dbHelper = new LoanDatabaseHelper(this);
        loansList = new ArrayList<>();

        listViewLoans = findViewById(R.id.listViewLoans);
        spinnerTabletBrand = findViewById(R.id.spinnerTabletBrand);
        spinnerCableType = findViewById(R.id.spinnerCableType);
        editTextStartDate = findViewById(R.id.editTextStartDate);
        editTextEndDate = findViewById(R.id.editTextEndDate);
        buttonFilter = findViewById(R.id.buttonFilter);
        buttonClearFilters = findViewById(R.id.buttonClearFilters);
        buttonReturn = findViewById(R.id.buttonReturn);

        // ✅ Update Brand Names (Acer & Samsung)
        String[] tabletBrands = {"All", "Acer", "Samsung"};
        String[] cableTypes = {"All", "None", "USB-C", "Micro-USB"};
        spinnerTabletBrand.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tabletBrands));
        spinnerCableType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cableTypes));

        adapter = new LoanAdapter(this, loansList);
        listViewLoans.setAdapter(adapter);

        // ✅ Load all loans when admin logs in
        loadLoans();

        // ✅ Handle loan selection
        listViewLoans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedLoanId = extractLoanId(loansList.get(position));

                // ✅ Highlight the selected loan
                for (int i = 0; i < parent.getChildCount(); i++) {
                    parent.getChildAt(i).setBackgroundResource(android.R.color.white); // Reset all
                }
                view.setBackgroundResource(android.R.color.holo_blue_light); // Highlight selected
            }
        });


        // ✅ Handle Date Selection
        editTextStartDate.setOnClickListener(v -> showDatePickerDialog(editTextStartDate));
        editTextEndDate.setOnClickListener(v -> showDatePickerDialog(editTextEndDate));

        // ✅ Handle Filter Button Click
        buttonFilter.setOnClickListener(v -> filterLoans());

        // ✅ Handle Clear Filters Button Click
        buttonClearFilters.setOnClickListener(v -> {
            spinnerTabletBrand.setSelection(0);
            spinnerCableType.setSelection(0);
            editTextStartDate.setText("");
            editTextEndDate.setText("");
            loadLoans();
        });

        // ✅ Handle Return Button Click with Confirmation Dialog
        buttonReturn.setOnClickListener(v -> {
            if (selectedLoanId == -1) {
                Toast.makeText(AdminActivity.this, "Please select a loan to return!", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(AdminActivity.this)
                    .setTitle("Confirm Return")
                    .setMessage("Are you sure you want to mark this loan as returned?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        dbHelper.deleteLoan(selectedLoanId);
                        Toast.makeText(AdminActivity.this, "Loan returned successfully!", Toast.LENGTH_SHORT).show();
                        selectedLoanId = -1;
                        loadLoans();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadLoans() {
        loansList.clear();
        Cursor cursor = dbHelper.getReadableDatabase().query(
                LoanDatabaseHelper.TABLE_LOANS,
                null, null, null, null, null,
                LoanDatabaseHelper.COLUMN_LOAN_DATE + " DESC" // ✅ Sorts by latest loan first
        );

        if (cursor.moveToFirst()) {
            do {
                String loanInfo = "ID: " + cursor.getInt(0) +
                        ", Brand: " + cursor.getString(1) +
                        ", Cable: " + (cursor.getString(2) == null ? "None" : cursor.getString(2)) +
                        ", Borrower: " + cursor.getString(3) +
                        ", Date: " + cursor.getString(5);
                loansList.add(loanInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private int extractLoanId(String loanInfo) {
        try {
            return Integer.parseInt(loanInfo.split(": ")[1].split(",")[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void showDatePickerDialog(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = selectedYear + "-" + String.format("%02d", (selectedMonth + 1)) + "-" + String.format("%02d", selectedDay);
            editText.setText(selectedDate);
        }, year, month, day).show();
    }

    private void filterLoans() {
        String tabletBrand = spinnerTabletBrand.getSelectedItem().toString();
        String cableType = spinnerCableType.getSelectedItem().toString();
        String startDate = editTextStartDate.getText().toString();
        String endDate = editTextEndDate.getText().toString();

        String whereClause = "";
        List<String> args = new ArrayList<>();

        if (!tabletBrand.equals("All")) {
            whereClause += LoanDatabaseHelper.COLUMN_TABLET_BRAND + " = ?";
            args.add(tabletBrand);
        }
        if (!cableType.equals("All")) {
            if (!whereClause.isEmpty()) whereClause += " AND ";
            whereClause += LoanDatabaseHelper.COLUMN_CABLE_TYPE + (cableType.equals("None") ? " IS NULL" : " = ?");
            if (!cableType.equals("None")) args.add(cableType);
        }
        if (!startDate.isEmpty()) {
            if (!whereClause.isEmpty()) whereClause += " AND ";
            whereClause += LoanDatabaseHelper.COLUMN_LOAN_DATE + " >= ?";
            args.add(startDate);
        }
        if (!endDate.isEmpty()) {
            if (!whereClause.isEmpty()) whereClause += " AND ";
            whereClause += LoanDatabaseHelper.COLUMN_LOAN_DATE + " <= ?";
            args.add(endDate);
        }

        Cursor cursor = dbHelper.getReadableDatabase().query(
                LoanDatabaseHelper.TABLE_LOANS,
                null, whereClause,
                args.isEmpty() ? null : args.toArray(new String[0]),
                null, null, LoanDatabaseHelper.COLUMN_LOAN_DATE + " DESC"
        );

        loansList.clear();
        if (cursor.moveToFirst()) {
            do {
                String loanInfo = "ID: " + cursor.getInt(0) +
                        ", Brand: " + cursor.getString(1) +
                        ", Cable: " + (cursor.getString(2) == null ? "None" : cursor.getString(2)) +
                        ", Borrower: " + cursor.getString(3) +
                        ", Date: " + cursor.getString(5);
                loansList.add(loanInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}
