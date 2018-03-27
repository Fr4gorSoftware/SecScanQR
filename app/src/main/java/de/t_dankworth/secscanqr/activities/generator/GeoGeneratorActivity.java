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
import android.widget.CheckBox;
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
 * This class is all about the geo location to QR-Code Generate Activity. In this Class the functionality of generating a QR-Code Picture is covered.
 */
public class GeoGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfLatitude, tfLongtitude;
    CheckBox cbLatitude, cbLongtitude;
    Boolean north = true, east = true;
    BarcodeFormat format;
    String latitude, longtitude, geo;
    MultiFormatWriter multiFormatWriter;
    Bitmap bitmap;
    final Activity activity = this;
    private static final String STATE_LATITUDE = MainActivity.class.getName();
    private static final String STATE_LONGTITUDE = "";
    private static final String STATE_NORTH = "north";
    private static final String STATE_EAST = "east";
    final  int REQ_EXTERNAL_STORAGE_PERMISSION = 97;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tfLatitude = (EditText) findViewById(R.id.tfLatitude);
        tfLongtitude = (EditText) findViewById(R.id.tfLongtitude);
        cbLatitude = (CheckBox) findViewById(R.id.cbLatitude);
        cbLongtitude = (CheckBox) findViewById(R.id.cbLongtitude);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.formats_geo_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            latitude = (String) savedInstanceState.get(STATE_LATITUDE);
            tfLatitude.setText(latitude);
            longtitude = (String) savedInstanceState.get(STATE_LONGTITUDE);
            tfLongtitude.setText(longtitude);
            north = (Boolean) savedInstanceState.get(STATE_NORTH);
            if(!north){
                cbLatitude.setChecked(false);
            }
            east = (Boolean) savedInstanceState.get(STATE_EAST);
            if(!east){
                cbLongtitude.setChecked(false);
            }
        }

        //OnClickListener for the "+" Button and functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitude = tfLatitude.getText().toString().trim();
                longtitude = tfLongtitude.getText().toString().trim();
                if(latitude.equals("") || longtitude.equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_geo_first), Toast.LENGTH_SHORT).show();
                } else {
                    multiFormatWriter = new MultiFormatWriter();
                    try{
                        if(north && east) {
                            geo = "geo:" + latitude + "," + longtitude;
                        } else if (!east && north){
                            geo = "geo:" + latitude + ",-" + longtitude;
                        } else if (!north && east){
                            geo = "geo:-" + latitude + "," + longtitude;
                        } else {
                            geo = "geo:-" + latitude + ",-" + longtitude;
                        }
                        BitMatrix bitMatrix = multiFormatWriter.encode(geo, format, 500,500);
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

        savedInstanceState.putString(STATE_LATITUDE, latitude);
        savedInstanceState.putString(STATE_LONGTITUDE, longtitude);
        savedInstanceState.putBoolean(STATE_NORTH, north);
        savedInstanceState.putBoolean(STATE_EAST, east);
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
     * Handles functionality behind the Checkboxes
     */
    public void onClickCheckboxes(View v){
        if(cbLatitude.isChecked() && cbLongtitude.isChecked()){
            north = true;
            east = true;
        } else if(!cbLatitude.isChecked() && cbLongtitude.isChecked()){
            north = false;
            east = true;
        } else if(cbLatitude.isChecked() && !cbLongtitude.isChecked()){
            north = true;
            east = false;
        } else{
            north = false;
            east = false;
        }
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
        if(ActivityCompat.checkSelfPermission(GeoGeneratorActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            showQrPopup();
        } else {
            ActivityCompat.requestPermissions(GeoGeneratorActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    /**
     *  This method will launch a popup were the generated QR-Code will be displayed.
     */
    private void showQrPopup(){
        QrPopup qrPopup = new QrPopup(GeoGeneratorActivity.this, geo, format);
        qrPopup.show();
    }

}
