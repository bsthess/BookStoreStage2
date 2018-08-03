package gr.xryalithes.bookstorestage2.Data;

/**
 * Created by Λάμπης on 17/7/2018.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;

/**
 * Object for the database handling.Creates database and handles the version number
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = DatabaseHelper.class.getSimpleName();

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelter.db";

    /**
     * Database version number.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     Creating a new DatabaseHelper object
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String concatenation with all the necessary column names for creating the books table.Including the type of data that every column should contain
        String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + BookData.TABLE_NAME + " ("
                + BookData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookData.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
                + BookData.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookData.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookData.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL DEFAULT NAME, "
                + BookData.COLUMN_SUPPLIER_PHONE + " LONG NOT NULL);";

        // Execute the SQL statement that creates the table!
        db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    /**
     * When we need to update the database version,call this method
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // nothing to do yet....
    }
}
