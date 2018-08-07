package gr.xryalithes.bookstorestage2;

import android.app.LoaderManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    //initialize views and variabels
    private static final int EXISTING_BOOK_LOADER = 0;
    private static final int MAXIMUM_ALLOWED_PRICE = 100;
    private static final int MINIMUM_ALLOWED_PRICE = 1;
    private EditText mTitleEditText;
    private EditText mPriceEditText;
    private TextView mQuantityTextView;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private boolean mBookHasChanged = false;
    private Uri mCurrentBookUri;

//listener for touching the views
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent Event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_activity);
        setTitle(getString(R.string.edit_activity_title));
//get the current book uri from main activity
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
//start the loader
       getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
//set the views and touchlisteners
        mTitleEditText = findViewById(R.id.edit_book_title);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityTextView = findViewById(R.id.edit_book_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        mTitleEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantityTextView.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_done:
                dataValidation();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mBookHasChanged) {
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // if discard selected, finish activity
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedBookDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //this method validates the data inserted by the user.
    public void dataValidation() {
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        //if all the fields are empty, no need to go further.
        if (
                TextUtils.isEmpty(titleString) && TextUtils.isEmpty(priceString) &&
                        TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)) {
            return;
        }
        //Check every field with its own validation method, if all the fields have valid data inserted,
        // then procceed to add book to database  with (addBook() method )
        if (titleIsValid(titleString) && priceIsValid(priceString) && quantityIsValid(quantityString) &&
                supplierNameIsValid(supplierName) && supplierPhoneIsValid(supplierPhone)) {
            editBook();
        }
    }

 //check title
    private boolean titleIsValid(String title) {
        if (title.isEmpty()) {
            Toast.makeText(this, getString(R.string.edit_text_title_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // maximum title length is 25 characters
        if (title.length() > 25) {
            mTitleEditText.setText("");
            Toast.makeText(this, getString(R.string.edit_text_title_maximum_characters),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    //check price
    private boolean priceIsValid(String price) {
        if (price.isEmpty()) {
            Toast.makeText(EditActivity.this, R.string.price_not_allowed_msg, Toast.LENGTH_SHORT).show();
            Log.v("Price =", price);
            return false;
        } else {
            int priceInteger = Integer.parseInt(price);
            //price must be bewtween 0-100
            if (priceInteger <= 0) {
                Toast.makeText(EditActivity.this, R.string.price_minimum_msg, Toast.LENGTH_SHORT).show();
                mPriceEditText.setText(String.valueOf(MINIMUM_ALLOWED_PRICE));
                return false;
            }
            if (priceInteger > 100) {
                mPriceEditText.setText(String.valueOf(MAXIMUM_ALLOWED_PRICE));
                Toast.makeText(EditActivity.this, R.string.price_maximum_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

   //check quantity
    private boolean quantityIsValid(String quantity) {

        if (quantity.isEmpty()) {
            Toast.makeText(EditActivity.this, R.string.quantity_limits_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int quantityInteger = Integer.parseInt(mQuantityTextView.getText().toString());
            //quantity must be 0-100
            if (quantityInteger > 100 || quantityInteger < 0) {
                Toast.makeText(EditActivity.this, R.string.quantity_limits_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

   //check supplier name
    private boolean supplierNameIsValid(String supplierName) {
        if (supplierName.isEmpty()) {
            Toast.makeText(EditActivity.this, R.string.supplier_name_empty_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (supplierName.length() > 25) {
                Toast.makeText(EditActivity.this, R.string.supplier_name_max_25_char_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

   //check supplier phone
    private boolean supplierPhoneIsValid(String supplierPhone) {
        //null not accepted
        if (supplierPhone.isEmpty()) {
            Toast.makeText(EditActivity.this, R.string.supplier_phone_empty_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //telephone number must have 10 digits
            if (supplierPhone.length() < 10) {
                Toast.makeText(EditActivity.this, R.string.supplier_phone_10_digits_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }


    private void editBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        //////////////////////////////////////////////////////////////////////////////////////////
        ContentValues values = new ContentValues();
        values.put(BookData.COLUMN_BOOK_TITLE, titleString);
        values.put(BookData.COLUMN_BOOK_PRICE, priceString);
        values.put(BookData.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookData.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookData.COLUMN_SUPPLIER_PHONE, supplierPhone);

        int rowsInserted = getContentResolver().update(mCurrentBookUri, values,null,null);

        // Show a toast message depending on whether or not the insertion was successful.
        if (rowsInserted == 0) {
            Toast.makeText(this, getString(R.string.edited_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.edited_book_success),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showUnsavedBookDialog(
            //dialog for user warning
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.continue_insert, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedBookDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookData._ID,
                BookData.COLUMN_BOOK_TITLE,
                BookData.COLUMN_BOOK_PRICE,
                BookData.COLUMN_BOOK_QUANTITY,
                BookData.COLUMN_SUPPLIER_NAME,
                BookData.COLUMN_SUPPLIER_PHONE};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookData.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookData.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookData.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookData.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookData.COLUMN_SUPPLIER_PHONE);


            // Extract out the values from the Cursor for the given column index

            String title = cursor.getString(titleColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            Long supplierPhone = cursor.getLong(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            mTitleEditText.setText(title);
            mPriceEditText.setText(String.valueOf(price));
            mQuantityTextView.setText(String.valueOf(quantity));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(Long.toString(supplierPhone));


        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleEditText.setText("");
        mPriceEditText.setText("");
        mQuantityTextView.setText("");
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");

    }


}
