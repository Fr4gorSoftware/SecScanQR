package de.t_dankworth.secscanqr.activities.generator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 10.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is all about showing the QR Code/Barcode and give the opportunity to save them
 */

public class GeneratorResultActivity extends AppCompatActivity {

    ImageView codeImage;
    Button btnSave;
    MultiFormatWriter multiFormatWriter;
    private String text2Code;
    private int formatInt;
    private BarcodeFormat format;
    Bitmap bitmap;
    final  int REQ_EXTERNAL_STORAGE_PERMISSION = 97;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_generator_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        codeImage = (ImageView) findViewById(R.id.resultImage);
        btnSave = (Button) findViewById(R.id.btnSave);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        text2Code = intent.getStringExtra("CODE");
        formatInt = bundle.getInt("FORMAT");
        format = generalHandler.idToBarcodeFormat(formatInt);
        multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Code, format, 1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            codeImage.setImageBitmap(bitmap);
        } catch (Exception e){
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_SHORT).show();
            finish();
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });

    }

    /**
     * This method saves the generated QR-Code on the smartphone
     */
    private void saveQrCode() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        Random rand = new Random();
        int n = rand.nextInt(50);
        int p = rand.nextInt(50);
        Bitmap image = bitmap;

        File f = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "SecScanQR" + File.separator + day + "." + month + "." + year + " " + n + "-" + p + ".png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            // Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                Toast.makeText(this, this.getResources().getText(R.string.toast_save), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void createFolder(){
        File storageDir = new File(Environment.getExternalStorageDirectory(), "SecScanQR");
        if(!storageDir.exists()){
            if(!storageDir.mkdir()){
                Toast.makeText(this, this.getResources().getText(R.string.toast_erorr), Toast.LENGTH_LONG).show();
            } else {
                saveQrCode();
            }
        } else {
            saveQrCode();
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
            createFolder();
        } else {
            Toast.makeText(this, this.getResources().getText(R.string.toast_permission_needed), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method asks for the write to external storage Permission to save or share the generated QR-Code
     */
    private void requestPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            createFolder();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
