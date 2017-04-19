package com.example.android.appleinventory.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nanda on 26/11/16.
 */

public class InventoryContract {


        private InventoryContract() {}


        public static final String CONTENT_AUTHORITY = "com.example.android.appleinventory";

        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


        public static final String PATH_INVENTORY = "inventory";


        public static final class InventoryEntry implements BaseColumns {

            public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

            public static final String CONTENT_LIST_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;


            public static final String CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

            public final static String TABLE_NAME = "inventory";


            public final static String _ID = BaseColumns._ID;


            public final static String COLUMN_PRODUCT_NAME ="Name";


            public final static String COLUMN_PRODUCT_PRICE = "Price";


            public final static String COLUMN_PRODUCT_QUANTITY = "Quantity";


            public final static String COLUMN_PRODUCT_IMAGE = "Image";


        }

    }


