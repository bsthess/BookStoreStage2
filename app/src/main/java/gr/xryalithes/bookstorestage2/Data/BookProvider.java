package gr.xryalithes.bookstorestage2.Data;

/**
 * Created by Λάμπης on 17/7/2018.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;
import java.security.Provider;

public class BookProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 100;

    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Database helper object
     */
    private DatabaseHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        // This cursor  has the content of the query results
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookData.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.books/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookData._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the BOOKs table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookData.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that the title is not null
        String title = values.getAsString(BookData.COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Book title missing");
        }
        // Check that the price is valid
        Integer price = values.getAsInteger(BookData.COLUMN_BOOK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        //  check that quantity it's greater than 0 and not negative
        Integer quantity = values.getAsInteger(BookData.COLUMN_BOOK_PRICE);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Book quantity must be greater than zero");
        }
        //check that supplier name is not null
        String supplierName = values.getAsString(BookData.COLUMN_BOOK_TITLE);
        if (supplierName == null) {
            throw new IllegalArgumentException("Supplier name is  missing");
        }
        //check that supplier phone is not negative
        String supplierPhone = values.getAsString(BookData.COLUMN_BOOK_TITLE);
        if (supplierPhone == null) {
            throw new IllegalArgumentException("Supplier phone is  missing");
        }
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new book with the given values
        long id = database.insert(BookData.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the BOOK_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookData._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update the books with the values object according the selection and selection arguments.If rows updated successfuly,return the number of rows.
     */
    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(BookData.COLUMN_BOOK_TITLE)) {
            String name = values.getAsString(BookData.COLUMN_BOOK_TITLE);
            if (name == null) {
                throw new IllegalArgumentException("Book must have a title");
            }
        }

        // if the values object contains a valid key BOOK_PRICE,set an integer variable for price.
        if (values.containsKey(BookData.COLUMN_BOOK_PRICE)) {
            Integer price = values.getAsInteger(BookData.COLUMN_BOOK_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Book must have a valid price");
            }
        }

// if the values object contains a valid key BOOK_QUANTITY,set an integer variable for quantity.
        if (values.containsKey(BookData.COLUMN_BOOK_QUANTITY)) {

            Integer quantity = values.getAsInteger(BookData.COLUMN_BOOK_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Book quantity invalid value");
            }
        }
        // if the values object contains a valid key for supplier name ,set a string variable for it.
        if (values.containsKey(BookData.COLUMN_SUPPLIER_NAME)) {

            String supplierName = values.getAsString(BookData.COLUMN_SUPPLIER_NAME);
            if (supplierName == null) {
                throw new IllegalArgumentException("Supplier Name needed");
            }
        }
        // if the values object contains a valid key for supplier_phone,set a string variable for it.
        if (values.containsKey(BookData.COLUMN_SUPPLIER_PHONE)) {

            String supplierPhone = values.getAsString(BookData.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null) {
                throw new IllegalArgumentException("Supplier Phone needed");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        //  get writeable database object to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Update the rows in database and return the updated rows.
        int rowsUpdated = database.update(BookData.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database object
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookData.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = BookData._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookData.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookData.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
