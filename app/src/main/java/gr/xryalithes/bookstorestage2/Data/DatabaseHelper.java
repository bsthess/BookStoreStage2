package gr.xryalithes.bookstorestage2.Data;

/**
 * Created by Λάμπης on 17/7/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;

/**
 * Database helper for Pets app. Manages database creation and version management.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelter.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link DatabaseHelper}.
     *
     * @param context of the app
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + BookData.TABLE_NAME + " ("
                + BookData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookData.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + BookData.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookData.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookData.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL DEFAULT NAME, "
                + BookData.COLUMN_SUPPLIER_PHONE + " LONG NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
