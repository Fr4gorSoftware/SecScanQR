package de.t_dankworth.secscanqr.activities.generator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.activities.QrPopup;

/**
 * Created by Thore Dankworth
 * Last Update: 11.03.2018
 * Last Update by Thore Dankworth
 * <p>
 * This class is all about the Text to QR-Code Generate Activity. In this Class the functionality of generating a QR-Code Picture is covered.
 */

public class TextGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText text;
    BarcodeFormat format;
    String text2Qr;
    MultiFormatWriter multiFormatWriter;
    Bitmap bitmap;
    final AppCompatActivity activity = this;
    private static final String STATE_TEXT = MainActivity.class.getName();
    final int REQ_EXTERNAL_STORAGE_PERMISSION = 97;


    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_generator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text = findViewById(R.id.txtQR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the Spinner Menu for the different formats
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_formats_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //If the device were rotated then restore information
        if (savedInstanceState != null) {
            text2Qr = (String) savedInstanceState.get(STATE_TEXT);
            text.setText(text2Qr);
        }

        // Get intent, action and MINE type and check if the intent was started by a share to module from an other app
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); //call method to handle sent text
            }
        }

        //OnClickListener for the "+" Button and functionality
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            text2Qr = text.getText().toString().trim();
            if (text2Qr.equals("")) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_text_first), Toast.LENGTH_SHORT).show();
            } else {
                multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, format, 500, 500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    //Hide Keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    //Check permissions before showing Popup
                    requestPermission();
                } catch (Exception e) {
                    Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_LONG).show();
                }
            }

        });

    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(STATE_TEXT, text2Qr);
    }

    /**
     * This method handles Text that was shared by an other app to SecScanQR and generates a qr code
     *
     * @param intent from Share to from other Apps
     */
    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            text.setText(sharedText);
            text2Qr = sharedText;
        }
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        if (compare.equals("AZTEC")) {
            format = BarcodeFormat.AZTEC;
        } else if (compare.equals("QR_CODE")) {
            format = BarcodeFormat.QR_CODE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = BarcodeFormat.QR_CODE;
    }

    /**
     * This method gets the results from the request of the requestPermission method.
     *
     * @param requestCode  something like a checksum. It validates that this activity asks for this permission
     * @param permissions  stores the permissions that were asked for in the requestPermission method
     * @param grantResults stores the answers of the user regarding to the requestPermission method
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_EXTERNAL_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showQrPopup();
        } else {
            Toast.makeText(activity, activity.getResources().getText(R.string.toast_permission_needed), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method asks for the write to external storage Permission to save or share the generated QR-Code
     */
    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(TextGeneratorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showQrPopup();
        } else {
            ActivityCompat.requestPermissions(TextGeneratorActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    /**
     * This method will launch a popup were the generated QR-Code will be displayed.
     */
    private void showQrPopup() {
        QrPopup qrPopup = new QrPopup(TextGeneratorActivity.this, text2Qr, format);
        qrPopup.show();
    }
}
