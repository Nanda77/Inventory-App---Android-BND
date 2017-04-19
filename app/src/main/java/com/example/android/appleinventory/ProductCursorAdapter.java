package com.example.android.appleinventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.appleinventory.Data.InventoryContract.InventoryEntry;

/**
 * Created by Nanda on 26/11/16.
 */

public class ProductCursorAdapter extends CursorAdapter{

    private Integer quantityChanged,columnId;


        public ProductCursorAdapter(Context context, Cursor c) {
            super(context, c, 0 /* flags */);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }


        @Override
        public void bindView(View view,final Context context, final Cursor cursor) {
            final TextView nameTextView = (TextView) view.findViewById(R.id.name);
            TextView priceTextView = (TextView) view.findViewById(R.id.price);
            final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);


            final int idColumnIndex = cursor.getColumnIndex(InventoryEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);
            columnId = cursor.getInt(idColumnIndex);

            String productName = cursor.getString(nameColumnIndex);
            String productPrice = cursor.getString(priceColumnIndex);
            String productQuantity = cursor.getString(quantityColumnIndex);
            String productImage = cursor.getString(imageColumnIndex);



            nameTextView.setText(productName);
            nameTextView.setTag(columnId);
            priceTextView.setText(productPrice);
            quantityTextView.setText(productQuantity);


            // Sale Button


            Button saleButton = (Button) view.findViewById(R.id.sale_button);

            saleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    quantityChanged = Integer.valueOf(quantityTextView.getText().toString());
                    quantityChanged = quantityChanged - 1;
                    quantityTextView.setText(String.valueOf(quantityChanged));

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityChanged);

                    columnId = (Integer) nameTextView.getTag();
                    if (quantityChanged >= 0) {

                        Uri mCurrentCaseUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, columnId);
                        context.getContentResolver().update(mCurrentCaseUri, values, null, null);
                    }

                    if (quantityChanged < 0) {
                        Toast.makeText(context, R.string.no_stock, Toast.LENGTH_LONG).show();
                        quantityTextView.setText("0");
                    }
                }

            });



        }
    }
