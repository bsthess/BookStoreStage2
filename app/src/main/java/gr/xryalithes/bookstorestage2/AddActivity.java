package gr.xryalithes.bookstorestage2;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

public class AddActivity extends AppCompatActivity {
    //initialization of variables and views
    private static final int MAXIMUM_ALLOWED_PRICE = 100;
    private static final int MINIMUM_ALLOWED_PRICE = 1;
    private EditText mTitleEditText;
    private EditText mPriceEditText;
    private TextView mQuantityTextView;
    private EditText mSupplierNameEditText;
    private EditText mSupplierPhoneEditText;
    private boolean mBookHasChanged = false;
    //listener for touched views.If touched any,mBookHasChanged is true
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent Event) {
            mBookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    //creating the activity.Declare the views and set touchlistener on them.Set the title of the activity.
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_activity);
        setTitle(getString(R.string.add_activity_title));

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
    //create menu
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add_, menu);
        return true;
    }

    @Override
    //what happens if menu items selected
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            //if we select save then call dataValidation method
            case R.id.action_save:
                dataValidation();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity

                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(AddActivity.this);
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
        if (TextUtils.isEmpty(titleString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) && TextUtils.isEmpty(supplierPhone)) {
            Toast.makeText(this, getString(R.string.no_data_to_save),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        //Check every field with its own validation method, if all the fields have valid data inserted,
        // then procceed to add book to database  with (addBook() method )
        if (titleIsValid(titleString) && priceIsValid(priceString) && quantityIsValid(quantityString) &&
                supplierNameIsValid(supplierName) && supplierPhoneIsValid(supplierPhone)) {
            addBook();
        }
    }
    ////////////////DATA VALIDATION METHODS////////////////////////////////////////////////////////
    // check title
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
            Toast.makeText(AddActivity.this, R.string.price_not_allowed_msg, Toast.LENGTH_SHORT).show();
            Log.v("Price =", price);
            return false;
        } else {
            int priceInteger = Integer.parseInt(price);
            // price is valid between 0 and 100
            if (priceInteger <= 0) {
                Toast.makeText(AddActivity.this, R.string.price_minimum_msg, Toast.LENGTH_SHORT).show();
                mPriceEditText.setText(String.valueOf(MINIMUM_ALLOWED_PRICE));
                return false;
            }
            if (priceInteger > 100) {
                mPriceEditText.setText(String.valueOf(MAXIMUM_ALLOWED_PRICE));
                Toast.makeText(AddActivity.this, R.string.price_maximum_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    //check quantity
    private boolean quantityIsValid(String quantity) {

        if (quantity.isEmpty()) {
            Toast.makeText(AddActivity.this, R.string.quantity_limits_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int quantityInteger = Integer.parseInt(mQuantityTextView.getText().toString());
            // quantity must be between 0 and 100
            if (quantityInteger > 100 || quantityInteger < 0) {
                Toast.makeText(AddActivity.this, R.string.quantity_limits_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    //check supplier name
    private boolean supplierNameIsValid(String supplierName) {
        if (supplierName.isEmpty()) {
            Toast.makeText(AddActivity.this, R.string.supplier_name_empty_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //supplier name must have 25 characters maximum
            if (supplierName.length() > 25) {
                Toast.makeText(AddActivity.this, R.string.supplier_name_max_25_char_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    //check supplier phone
    private boolean supplierPhoneIsValid(String supplierPhone) {
        if (supplierPhone.isEmpty()) {
            Toast.makeText(AddActivity.this, R.string.supplier_phone_empty_msg, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            //telephone number must have 10 digits
            if (supplierPhone.length() < 10) {
                Toast.makeText(AddActivity.this, R.string.supplier_phone_10_digits_msg, Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        }
    }

    /**
     * Get user input from editor and save book into database.
     */
    private void addBook() {
//get the strings from editText views
        String titleString = mTitleEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityTextView.getText().toString().trim();
        String supplierName = mSupplierNameEditText.getText().toString().trim();
        String supplierPhone = mSupplierPhoneEditText.getText().toString().trim();
        //create the ContentValues object to insert
        ContentValues values = new ContentValues();
        values.put(BookData.COLUMN_BOOK_TITLE, titleString);
        values.put(BookData.COLUMN_BOOK_PRICE, priceString);
        values.put(BookData.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookData.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(BookData.COLUMN_SUPPLIER_PHONE, supplierPhone);
//execute the sql command for inserting data to database using values object
        Uri savedUri = getContentResolver().insert(BookData.CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (savedUri == null) {
            Toast.makeText(this, getString(R.string.insert_book_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.insert_book_success),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showUnsavedBookDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
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

}
