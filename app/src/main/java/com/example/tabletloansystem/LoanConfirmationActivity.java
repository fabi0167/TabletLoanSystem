package com.example.tabletloansystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoanConfirmationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_confirmation);

        TextView textViewSummary = findViewById(R.id.textViewSummary);
        Button buttonBack = findViewById(R.id.buttonBack);

        // ✅ Get loan details from the intent
        Intent intent = getIntent();
        String summary = intent.getStringExtra("loan_summary");
        textViewSummary.setText(summary);

        // ✅ Fix: When clicking "OK," close all activities and return to Start screen
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoanConfirmationActivity.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
