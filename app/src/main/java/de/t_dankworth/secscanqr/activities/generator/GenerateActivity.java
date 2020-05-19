package de.t_dankworth.secscanqr.activities.generator;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.View;


import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.GeneralHandler;


/**
 * Created by Thore Dankworth
 * Last Update: 10.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is just a forwarding to the specific generators
 */

public class GenerateActivity extends AppCompatActivity implements View.OnClickListener {

    final Activity activity = this;
    private GeneralHandler generalHandler;
    private CardView barcodeCard, textCard, geoCard, contactCard, wifiCard;



    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_generate);
        barcodeCard = (CardView) findViewById(R.id.barcode_card);
        textCard = (CardView) findViewById(R.id.text_card);
        geoCard = (CardView) findViewById(R.id.geo_card);
        contactCard = (CardView) findViewById(R.id.contact_card);
        wifiCard = (CardView) findViewById(R.id.wifi_card);

        barcodeCard.setOnClickListener(this);
        textCard.setOnClickListener(this);
        geoCard.setOnClickListener(this);
        contactCard.setOnClickListener(this);
        wifiCard.setOnClickListener(this);
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.barcode_card:
                startActivity(new Intent(GenerateActivity.this, BarcodeGenerateActivity.class));
                break;
            case R.id.text_card:
                startActivity(new Intent(GenerateActivity.this, TextGeneratorActivity.class));
                break;
            case R.id.geo_card:
                startActivity(new Intent(GenerateActivity.this, GeoGeneratorActivity.class));
                break;
            case R.id.contact_card:
                startActivity(new Intent(GenerateActivity.this, VCardGeneratorActivity.class));
                break;
            case R.id.wifi_card:
                startActivity(new Intent(GenerateActivity.this, WifiGeneratorActivity.class));
                break;
            default:
                break;
        }
    }
}
