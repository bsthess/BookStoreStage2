package gr.xryalithes.bookstorestage2;

/**
 * Created by Λάμπης on 17/7/2018.
 */


import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import gr.xryalithes.bookstorestage2.Data.BookContract.BookData;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

//variable for the loader id
    private static final int BOOK_LOADER = 0;

//create new object for cursor adapter
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//the floating button for adding new book
        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

//list view for showing items
        ListView bookListView =  findViewById(R.id.book_list);
//setting the empty view image
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
//creating the adapter and attach to list view
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        //set click listener to list view.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //clicking on a list item,triggers the details activity,passing the item's uri for use in editor and database updating
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookData.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

//start the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookData._ID,
                BookData.COLUMN_BOOK_TITLE,
                BookData.COLUMN_BOOK_PRICE,
                BookData.COLUMN_BOOK_QUANTITY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookData.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
