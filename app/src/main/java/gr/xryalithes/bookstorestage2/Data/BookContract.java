package gr.xryalithes.bookstorestage2.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Labis on 17/7/2018.
 */
//the class with all the costants to use with database handling
public class BookContract {
//content authority
    public static final String CONTENT_AUTHORITY = "gr.xryalithes.bookstorestage2";

//the base uri uppon to build all uris
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

//the path to books table
    public static final String PATH_BOOKS = "books";

    public static final class BookData implements BaseColumns {

        /** The content URI to access the book data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;


        //the final variables for table,id and columns names
        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_BOOK_TITLE = "title";

        public final static String COLUMN_BOOK_PRICE = "price";

        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        public final static String COLUMN_SUPPLIER_NAME = "supplier";

        public final static String COLUMN_SUPPLIER_PHONE = "supplier_phone";
    }
}