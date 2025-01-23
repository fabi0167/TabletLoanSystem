package com.example.tabletloansystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import java.util.List;

public class LoanAdapter extends ArrayAdapter<String> {

    public LoanAdapter(Context context, List<String> loans) {
        super(context, 0, loans);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.loan_list_item, parent, false);
        }

        String loanInfo = getItem(position);
        if (loanInfo != null) {
            String[] details = loanInfo.split(", ");
            ((TextView) convertView.findViewById(R.id.textTabletBrand)).setText(details[1]); // Brand
            ((TextView) convertView.findViewById(R.id.textCableType)).setText(details[2]); // Cable Type
            ((TextView) convertView.findViewById(R.id.textBorrowerName)).setText(details[3]); // Borrower
            ((TextView) convertView.findViewById(R.id.textLoanDate)).setText(details[4]); // Date
        }

        return convertView;
    }
}
