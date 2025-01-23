package com.example.tabletloansystem;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LoanDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "loan.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_LOANS = "loans";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TABLET_BRAND = "tablet_brand";
    public static final String COLUMN_CABLE_TYPE = "cable_type";
    public static final String COLUMN_BORROWER_NAME = "borrower_name";
    public static final String COLUMN_CONTACT_INFO = "contact_info";
    public static final String COLUMN_LOAN_DATE = "loan_date";

    public LoanDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOANS_TABLE = "CREATE TABLE " + TABLE_LOANS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TABLET_BRAND + " TEXT,"
                + COLUMN_CABLE_TYPE + " TEXT,"
                + COLUMN_BORROWER_NAME + " TEXT,"
                + COLUMN_CONTACT_INFO + " TEXT,"
                + COLUMN_LOAN_DATE + " TEXT" + ")";
        db.execSQL(CREATE_LOANS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOANS);
        onCreate(db);
    }

    /**
     * ✅ Saves a loan to the database.
     */
    public void addLoan(String tabletBrand, String cableType, String borrowerName, String contactInfo, String loanDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLET_BRAND, tabletBrand);
        values.put(COLUMN_CABLE_TYPE, cableType); // Can be null
        values.put(COLUMN_BORROWER_NAME, borrowerName);
        values.put(COLUMN_CONTACT_INFO, contactInfo);
        values.put(COLUMN_LOAN_DATE, loanDate);

        db.insert(TABLE_LOANS, null, values);
        db.close();
    }

    /**
     * ✅ Deletes a loan from the database.
     */
    public void deleteLoan(int loanId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOANS, COLUMN_ID + " = ?", new String[]{String.valueOf(loanId)});
        db.close();
    }
}
