package de.t_dankworth.secscanqr;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Created by Thore Dankworth
 * Last Update: 15.08.2017
 * Last Update by Thore Dankworth
 *
 * This class is all about the Generate Activity. In this Class the functionality of generating a QR-Code Picture is covered.
 */

public class GenerateActivity extends AppCompatActivity {

    EditText text;
    ImageView image;
    String text2Qr;
    String sampleQr = "Example";
    private static final String STATE_TEXT = MainActivity.class.getName();



    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text = (EditText) findViewById(R.id.txtQR);
        image = (ImageView) findViewById(R.id.image);

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            text2Qr = (String) savedInstanceState.get(STATE_TEXT);
            if(text2Qr == null){
                //Example QR-Code will be shown
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode("Example", BarcodeFormat.QR_CODE, 200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e){
                    e.printStackTrace();
                }
            } else {
                //Generated QR-Code will be shown
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    image.setImageBitmap(bitmap);
                } catch (WriterException e){
                    e.printStackTrace();
                }
            }
        } else {
            //Example QR-Code will be shown
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try{
                BitMatrix bitMatrix = multiFormatWriter.encode(sampleQr, BarcodeFormat.QR_CODE, 200,200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                image.setImageBitmap(bitmap);
            } catch (WriterException e){
                e.printStackTrace();
            }
        }

        //OnClickListener for the "+" Button and functionality
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text2Qr = text.getText().toString().trim();
                if(text2Qr.equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_text_first), Toast.LENGTH_SHORT).show();
                } else {
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200,200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        image.setImageBitmap(bitmap);
                    } catch (WriterException e){
                        e.printStackTrace();
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

        savedInstanceState.putString(STATE_TEXT, text2Qr);
    }
}
