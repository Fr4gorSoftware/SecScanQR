package de.t_dankworth.secscanqr.activities.generator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
 * This class is all about the VCard to QR-Code Generate Activity. In this Class the functionality of generating a QR-Code Picture is covered.
 */

public class VCardGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfFirstName, tfName, tfOrg, tfTeleWork, tfTelePrivate, tfMobil, tfEmail, tfWeb, tfStreet, tfPLZ, tfCity, tfState, tfCountry;
    BarcodeFormat format;
    String[] text2Qr = new String[13];;
    String vcardCode;
    MultiFormatWriter multiFormatWriter;
    Bitmap bitmap;
    final Activity activity = this;
    private static final String STATE_TEXT = MainActivity.class.getName();
    final  int REQ_EXTERNAL_STORAGE_PERMISSION = 97;

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vcard_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initializeInputFields();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_formats_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        if(savedInstanceState != null){
            text2Qr = (String[]) savedInstanceState.get(STATE_TEXT);
            recoverOldValues();
        }

        //OnClickListener for the "+" Button and functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(text2Qr[0].equals("") && text2Qr[1].equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fn_or_name_first), Toast.LENGTH_SHORT).show();
                } else {
                    multiFormatWriter = new MultiFormatWriter();
                    try{
                        buildVCardCode();
                        BitMatrix bitMatrix = multiFormatWriter.encode(vcardCode, format, 500,500);
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
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putStringArray(STATE_TEXT, text2Qr);
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        if(compare.equals("AZTEC")){
            format = BarcodeFormat.AZTEC;
        }
        else if(compare.equals("QR_CODE")){
            format = BarcodeFormat.QR_CODE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = BarcodeFormat.QR_CODE;
    }

    /**
     * Initializes all input fields
     */
    private void initializeInputFields(){
        tfFirstName = (EditText) findViewById(R.id.txtFN);
        tfName = (EditText) findViewById(R.id.txtName);
        tfOrg = (EditText) findViewById(R.id.txtOrg);
        tfTeleWork = (EditText) findViewById(R.id.txtTeleWork);
        tfTelePrivate = (EditText) findViewById(R.id.txtTelePrivat);
        tfMobil = (EditText) findViewById(R.id.txtMobil);
        tfEmail = (EditText) findViewById(R.id.txtEmail);
        tfWeb = (EditText) findViewById(R.id.txtWeb);
        tfStreet = (EditText) findViewById(R.id.txtStreet);
        tfPLZ = (EditText) findViewById(R.id.txtPLZ);
        tfCity = (EditText) findViewById(R.id.txtCity);
        tfState = (EditText) findViewById(R.id.txtState);
        tfCountry = (EditText) findViewById(R.id.txtCountry);
    }

    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr[0] = tfName.getText().toString().trim();
        text2Qr[1] = tfFirstName.getText().toString().trim();
        text2Qr[2] = tfOrg.getText().toString().trim();
        text2Qr[3] = tfWeb.getText().toString().trim();
        text2Qr[4] = tfEmail.getText().toString().trim();
        text2Qr[5] = tfMobil.getText().toString().trim();
        text2Qr[6] = tfTeleWork.getText().toString().trim();
        text2Qr[7] = tfTelePrivate.getText().toString().trim();
        text2Qr[8] = tfStreet.getText().toString().trim();
        text2Qr[9] = tfCity.getText().toString().trim();
        text2Qr[10] = tfState.getText().toString().trim();
        text2Qr[11] = tfPLZ.getText().toString().trim();
        text2Qr[12] = tfCountry.getText().toString().trim();
    }

    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        tfName.setText(text2Qr[0]);
        tfFirstName.setText(text2Qr[1]);
        tfOrg.setText(text2Qr[2]);
        tfWeb.setText(text2Qr[3]);
        tfEmail.setText(text2Qr[4]);
        tfMobil.setText(text2Qr[5]);
        tfTeleWork.setText(text2Qr[6]);
        tfTelePrivate.setText(text2Qr[7]);
        tfStreet.setText(text2Qr[8]);
        tfCity.setText(text2Qr[9]);
        tfState.setText(text2Qr[10]);
        tfPLZ.setText(text2Qr[11]);
        tfCountry.setText(text2Qr[12]);
    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildVCardCode(){
        vcardCode = "BEGIN:VCARD\nVERSION:2.1\nN:" + text2Qr[0] + ";" +text2Qr[1] + "\n";
        if(!text2Qr[2].equals("")){
            vcardCode += "ORG:" + text2Qr[2] + "\n";
        }
        if(!text2Qr[3].equals("")){
            vcardCode += "URL:" + text2Qr[3] + "\n";
        }
        if(!text2Qr[4].equals("")){
            vcardCode += "EMAIL;TYPE=INTERNET:" + text2Qr[4] + "\n";
        }
        if(!text2Qr[5].equals("")){
            vcardCode += "TEL;CELL:" + text2Qr[5] + "\n";
        }
        if(!text2Qr[6].equals("")){
            vcardCode += "TEL;WORK;VOICE:" + text2Qr[6] + "\n";
        }
        if(!text2Qr[7].equals("")){
            vcardCode += "TEL;HOME;VOICE:" + text2Qr[7] + "\n";
        }
        if((!text2Qr[8].equals("")) || (!text2Qr[9].equals("")) || (!text2Qr[10].equals("")) || (!text2Qr[11].equals("")) || (!text2Qr[12].equals(""))){
            vcardCode += "ADR:;;" + text2Qr[8] + ";" + text2Qr[9] + ";" + text2Qr[10] + ";" + text2Qr[11] + ";" + text2Qr[12] + "\n";
        }
        vcardCode += "END:VCARD";
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
        if(ActivityCompat.checkSelfPermission(VCardGeneratorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showQrPopup();
        } else {
            ActivityCompat.requestPermissions(VCardGeneratorActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    /**
     *  This method will launch a popup were the generated QR-Code will be displayed.
     */
    private void showQrPopup(){
        QrPopup qrPopup = new QrPopup(VCardGeneratorActivity.this, vcardCode, format);
        qrPopup.show();
    }
}
