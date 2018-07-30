package gr.xryalithes.bookstorestage2;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MAXIMUM_ALLOWED_QUANTITY = 100;
    private static final int MINIMUM_ALLOWED_QUANTITY = 0;
    private static final int EXISTING_BOOK_LOADER = 0;
    public ImageButton quantityPlusButton;
    public ImageButton quantityMinusButton;
    private Uri mCurrentBookUri;
    private TextView mTitleTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierNameTextView;
    private TextView mSupplierPhoneTextView;
    private Button callButton;
    private Button editButton;
    private Button deleteButton;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle(getString(R.string.details_activity_title));
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);

        mTitleTextView = findViewById(R.id.edit_book_title);
        mPriceTextView = findViewById(R.id.edit_book_price);
        mQuantityTextView = findViewById(R.id.edit_book_quantity);
        mSupplierNameTextView = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneTextView = findViewById(R.id.edit_supplier_phone);

        callButton = findViewById(R.id.call_button);
        editButton = findViewById(R.id.edit_button);
        deleteButton = findViewById(R.id.delete_button);

        quantityPlusButton = findViewById(R.id.quantity_plus_button);
        quantityPlusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });
        quantityMinusButton = findViewById(R.id.quantity_minus_button);
        quantityMinusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this,EditActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telnumber = mSupplierPhoneTextView.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + telnumber));
                if (ActivityCompat.checkSelfPermission(DetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(intent);

            }
        });
    }

    private void increaseQuantity() {
        int quantity = Integer.parseInt(mQuantityTextView.getText().toString());
        if (quantity <MAXIMUM_ALLOWED_QUANTITY) {
            quantity++;
            String quantityChanged = String.valueOf(quantity);
            if (quantityIsUpdated(quantityChanged)){
                Toast.makeText(DetailsActivity.this,R.string.quantity_increased, Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(DetailsActivity.this,R.string.quantity_maximum_message, Toast.LENGTH_SHORT).show();

        }
    }
    private void decreaseQuantity() {
        int quantity = Integer.parseInt(mQuantityTextView.getText().toString());
        if (quantity > MINIMUM_ALLOWED_QUANTITY) {
            quantity--;
            String quantityChanged = String.valueOf(quantity);
            if(quantityIsUpdated(quantityChanged)){
                Toast.makeText(DetailsActivity.this,R.string.quantity_decreased, Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(DetailsActivity.this,R.string.quantity_minimum_message, Toast.LENGTH_SHORT).show();
        }

    }
    private boolean quantityIsUpdated(String updatedQuantity) {
        //////////////////////////////////////////////////////////////////////////////////////////
        ContentValues values = new ContentValues();
        values.put(BookData.COLUMN_BOOK_QUANTITY, updatedQuantity);
        int rowsUpdated = getContentResolver().update(mCurrentBookUri, values, null, null);

        if (rowsUpdated == 0) {
            Toast.makeText(this, getString(R.string.quantity_not_updated),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    return true;
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteBook() {

        int rowsDeleted = 0;
        // Only perform the delete if this is an existing pet.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);
        }

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.delete_book_fail),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.delete_book_ok),
                    Toast.LENGTH_SHORT).show();

        }
        finish();
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
            mTitleTextView.setText(title);
            mPriceTextView.setText(String.valueOf(price));
            mQuantityTextView.setText(String.valueOf(quantity));
            mSupplierNameTextView.setText(supplierName);
            mSupplierPhoneTextView.setText(Long.toString(supplierPhone));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mTitleTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mSupplierNameTextView.setText("");
        mSupplierPhoneTextView.setText("");

    }

}
