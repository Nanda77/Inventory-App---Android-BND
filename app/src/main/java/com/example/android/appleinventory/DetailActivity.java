package com.example.android.appleinventory;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.appleinventory.Data.InventoryContract.InventoryEntry;

import java.io.FileDescriptor;
import java.io.IOException;

import static com.example.android.appleinventory.R.id.quantity;
import static java.lang.Integer.parseInt;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int EXISTING_PRODUCT_LOADER = 0;


    private Uri mCurrentProductUri;


    private EditText mNameEditText;


    private EditText mPriceEditText;

    private TextView mQuantityText;


    private Button mSoldButton;

    private int quantityGlobal;
    private String nameGlobal;

    private Button mStockButton;


    // Image Code starts

    private static final String LOG_TAG = InventoryActivity.class.getSimpleName();

    private ImageView mImageView;
    private TextView mTextView;

    private Uri mUri;
    private Bitmap mBitmap;


    private boolean isGalleryPicture = false;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final int MY_PERMISSIONS_REQUEST = 2;


    public void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST);

            }
        }

    }

    public void openImageSelector(View view) {
        Intent intent;
        Log.e(LOG_TAG, "While is set and the ifs are worked through.");

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }


        Log.e(LOG_TAG, "Check write to external permissions");

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.i(LOG_TAG, "Received an \"Activity Result\"");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());
                mImageView = (ImageView) findViewById(R.id.image);
                mBitmap = getBitmapFromUri(mUri);
                mImageView.setImageBitmap(mBitmap);

                isGalleryPicture = true;
            }

        }
    }


    private Bitmap getBitmapFromUri(Uri uri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                if (parcelFileDescriptor != null) {
                    parcelFileDescriptor.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error closing ParcelFile Descriptor");
            }
        }
    }


    // Image Code Ends


    private boolean mProductHasChanged = false;


    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();


        if (mCurrentProductUri == null)

        {

            setTitle(getString(R.string.detail_activity_title_new_product));


            invalidateOptionsMenu();
        } else

        {

            setTitle(getString(R.string.detail_activity_title_edit_product));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText)

                findViewById(R.id.edit_product_name);

        mPriceEditText = (EditText)

                findViewById(R.id.edit_product_price);

        mQuantityText = (TextView)

                findViewById(quantity);


        mSoldButton = (Button)

                findViewById(R.id.sold_button);


        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSoldButton.setOnTouchListener(mTouchListener);

    }

    public void incrementSold(View view) {

        EditText noStock = (EditText) findViewById(R.id.quantity_change);

        int quantityChange = quantityChange();

        if (quantityChange == 0) {
            quantityGlobal = quantityGlobal + 1;
        }
        quantityGlobal = quantityGlobal + quantityChange;
        TextView quantityTextView = (TextView) findViewById(quantity);
        quantityTextView.setText("" + quantityGlobal);

    }

    public void decrementSold(View view) {

        int quantityChange = quantityChange();

        if (quantityChange == 0) {
            quantityGlobal = quantityGlobal - 1;
        }

        int validChange = quantityGlobal - quantityChange;
        if (validChange > 0) {
            TextView quantityTextView = (TextView) findViewById(quantity);
            quantityGlobal = validChange;
            quantityTextView.setText("" + quantityGlobal);
        } else {
            EditText changeText = (EditText) findViewById(R.id.quantity_change);
            changeText.setText("0");
            Toast.makeText(this, getString(R.string.no_stock),
                    Toast.LENGTH_SHORT).show();

            TextView quantityTextView = (TextView) findViewById(quantity);
            quantityGlobal = 0;
            quantityTextView.setText("" + quantityGlobal);

        }

    }


    public int quantityChange() {
        int validNumber = 0;
        EditText changeText = (EditText) findViewById(R.id.quantity_change);
        String changeString = changeText.getText().toString();
        if (changeString.equals("")) {

            changeString = "0";
        }
        try {
            validNumber = parseInt(changeString);

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_number),
                    Toast.LENGTH_SHORT).show();

        }
        return validNumber;

    }


    public void clearText(View view) {
        EditText changeText = (EditText) findViewById(R.id.quantity_change);
        changeText.setText("");
    }


    public void requestSupplies(View view) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.order_summary_email_subject));

        intent.putExtra(Intent.EXTRA_TEXT, nameGlobal);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    private int saveProduct() {

        int success = 0;

        try {

            // Read from input fields
            // Use trim to eliminate leading or trailing white space
            String nameString = mNameEditText.getText().toString().trim();
            String priceString = mPriceEditText.getText().toString().trim();
            String quantityString = mQuantityText.getText().toString().trim();
            if (mUri == null) {
                mUri = Uri.parse("0");
            }

            String imageUri = mUri.toString();


            if (mCurrentProductUri == null &&
                    TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                    TextUtils.isEmpty(quantityString)) {
                return 0;
            }

            if (nameString.isEmpty() || priceString.isEmpty()) {
                Toast.makeText(this, getString(R.string.mandatory),
                        Toast.LENGTH_SHORT).show();
                return 0;
            }


            Integer.parseInt(priceString);


            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityGlobal);
            values.put(InventoryEntry.COLUMN_PRODUCT_IMAGE, imageUri);


            if (mCurrentProductUri == null) {

                Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                    success = 1;
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.update_product_successful),
                            Toast.LENGTH_SHORT).show();
                    success = 1;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.invalid_number),
                    Toast.LENGTH_SHORT).show();

        }
        return success;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int success;
        switch (item.getItemId()) {
            case R.id.action_save:

                success = saveProduct();
                if (success == 1) {
                    finish();
                }
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();

                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        requestPermissions();

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_IMAGE};


        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            String image = cursor.getString(imageColumnIndex);
            quantityGlobal = cursor.getInt(quantityColumnIndex);
            nameGlobal = name;
            mNameEditText.setText(String.valueOf(name));
            mPriceEditText.setText(String.valueOf(price));
            mQuantityText.setText(String.valueOf(quantity));

            if (!image.equals("0")) {
                Bitmap imageBit = getBitmapFromUri(Uri.parse(image));

                mImageView = (ImageView) findViewById(R.id.image);
                mImageView.setImageBitmap(imageBit);
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantityText.setText("");

    }


    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteProduct() {

        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }
}