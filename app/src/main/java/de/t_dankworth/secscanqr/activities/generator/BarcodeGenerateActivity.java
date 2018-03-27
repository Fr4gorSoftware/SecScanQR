package de.t_dankworth.secscanqr.activities.generator;

import android.Manifest;
import android.app.Activity;
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
 *
 * This class is all about the value to BARCODE Generate Activity. In this Class the functionality of generating a BARCODE Picture is covered.
 */

public class BarcodeGenerateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText text;
    BarcodeFormat format;
    String text2Barcode;
    MultiFormatWriter multiFormatWriter;
    Bitmap bitmap;
    final Activity activity = this;
    private static final String STATE_TEXT = MainActivity.class.getName();
    final  int REQ_EXTERNAL_STORAGE_PERMISSION = 97;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_generate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text = (EditText) findViewById(R.id.tfBarcode);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.barcode_formats_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //If the device were rotated then restore information
        if(savedInstanceState != null){
            text2Barcode = (String) savedInstanceState.get(STATE_TEXT);
            text.setText(text2Barcode);
        }

        // Get intent, action and MINE type and check if the intent was started by a share to modul from an other app
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null){
            if("text/plain".equals(type)){
                handleSendText(intent); //call method to handle sended text
            }
        }

        //OnClickListener for the "+" Button and functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2Barcode = text.getText().toString().trim();
                if(text2Barcode.equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_text_first), Toast.LENGTH_SHORT).show();
                } else {
                    multiFormatWriter = new MultiFormatWriter();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(text2Barcode, format, 500,500);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        //Hide Keyboard
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        //Check permissions before showing Popup
                        requestPermission();

                    } catch (Exception e){
                        Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
    }

    /**
     * This method handles Text that was shared by an other app to SecScanQR and generates a qr code
     * @param intent from Share to from other Apps
     */
    private void handleSendText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText != null){
            text.setText(sharedText);
            text2Barcode = sharedText;
        }
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(STATE_TEXT, text2Barcode);
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String compare = adapterView.getItemAtPosition(position).toString();
        if(compare.equals("BARCODE")){
            format = BarcodeFormat.CODABAR;
        }
        else if(compare.equals("CODE_128")){
            format = BarcodeFormat.CODE_128;
        }
        else if(compare.equals("CODE_39")){
            format = BarcodeFormat.CODE_39;
        }
        else if(compare.equals("EAN_13")){
            format = BarcodeFormat.EAN_13;
        }
        else if(compare.equals("EAN_8")){
            format = BarcodeFormat.EAN_8;
        }
        else if(compare.equals("ITF")){
            format = BarcodeFormat.ITF;
        }
        else if(compare.equals("PDF_417")){
            format = BarcodeFormat.PDF_417;
        }
        else if(compare.equals("UPC_A")){
            format = BarcodeFormat.UPC_A;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        format = BarcodeFormat.CODABAR;
    }

    /**
     * This method gets the results from the request of the requestPermission method.
     * @param requestCode something like a checksum. It validates that this activity asks for this permission
     * @param permissions stores the permissions that were asked for in the requestPermission method
     * @param grantResults stores the answers of the user regarding to the requestPermission method
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_EXTERNAL_STORAGE_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            showQrPopup();
        } else {
            Toast.makeText(activity, activity.getResources().getText(R.string.toast_permission_needed), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method asks for the write to external storage Permission to save or share the generated QR-Code
     */
    private void requestPermission(){
        if(ActivityCompat.checkSelfPermission(BarcodeGenerateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showQrPopup();
        } else {
            ActivityCompat.requestPermissions(BarcodeGenerateActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    /**
     *  This method will launch a popup were the generated QR-Code will be displayed.
     */
    private void showQrPopup(){
        QrPopup qrPopup = new QrPopup(BarcodeGenerateActivity.this, text2Barcode, format);
        qrPopup.show();
    }
}
