package de.t_dankworth.secscanqr.activities;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import de.t_dankworth.secscanqr.R;

/**
 * Created by Thore Dankworth
 * Last Update: 11.03.2018
 * Last Update by Thore Dankworth
 *
 * This class is all about the Popup for the generated qr/barcodes
 */

public class QrPopup extends Dialog {

    Context mContext;
    ImageView codeImage;
    Button btnSave;
    MultiFormatWriter multiFormatWriter;
    private String text2Code;
    BarcodeFormat format;
    Bitmap bitmap;

    public QrPopup(Context context, String text2Code, BarcodeFormat format) {
        super(context);

        this.mContext = context;
        this.text2Code = text2Code;
        this.format = format;
    }

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.qr_popup);

        codeImage = (ImageView) findViewById(R.id.image);
        btnSave = (Button) findViewById(R.id.btnSave);

        multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text2Code, format, 1000,1000);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            codeImage.setImageBitmap(bitmap);
        } catch (Exception e){
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFolder();
            }
        });
    }

    /**
     * This method saves the generated QR-Code on the smartphone
     */
    private void saveQrCode() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        Bitmap image = bitmap;

        File f = new File(Environment.getExternalStorageDirectory().toString() + File.separator + "SecScanQR" + File.separator  + now + ".jpg");
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
                Toast.makeText(mContext, mContext.getResources().getText(R.string.toast_save), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void createFolder(){
        File storageDir = new File(Environment.getExternalStorageDirectory(), "SecScanQR");
        if(!storageDir.exists()){
            if(!storageDir.mkdir()){
                Toast.makeText(mContext, mContext.getResources().getText(R.string.toast_erorr), Toast.LENGTH_LONG).show();
            } else {
                saveQrCode();
            }
        } else {
            saveQrCode();
        }
    }
}
