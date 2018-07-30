package gr.xryalithes.bookstorestage2;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.Locale;


import gr.xryalithes.bookstorestage2.Data.BookContract;
import gr.xryalithes.bookstorestage2.Data.DatabaseHelper;

/**
 * Created by Λάμπης on 17/7/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView =  view.findViewById(R.id.title_text_view);
        TextView priceTextView =  view.findViewById(R.id.price_text_view);
        TextView quantityTextView =  view.findViewById(R.id.quantity_text_view);

        // Find the columns of book attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BookContract.BookData._ID);
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookData.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookData.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookData.COLUMN_BOOK_QUANTITY);

        // Read the book attributes from the Cursor for the current book
        final String bookTitle = cursor.getString(titleColumnIndex);
        final String bookPrice = cursor.getString(priceColumnIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);
        final long id = cursor.getLong(idColumnIndex);

        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);


        // Format currency for Euro
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.GERMANY);
        String currency = format.format(Integer.parseInt(bookPrice));
        priceTextView.setText(currency);
        quantityTextView.setText((bookQuantity));

        Button sellButton =  view.findViewById(R.id.sale_button);
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(bookQuantity);

                if (quantity == 0){
                    Toast.makeText(context,R.string.no_books_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    quantity = quantity-1;
                    String reducedBookQuantity = String.valueOf(quantity);
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookData.COLUMN_BOOK_QUANTITY, reducedBookQuantity);

                    Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookData.CONTENT_URI, id);

                    int updatedRows = context.getContentResolver().update(currentBookUri, values, null, null);

                    if (updatedRows != 0) {
                        /* update text view if database update is successful */
                        Toast.makeText(context,R.string.book_sold, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, R.string.book_did_not_sold, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
