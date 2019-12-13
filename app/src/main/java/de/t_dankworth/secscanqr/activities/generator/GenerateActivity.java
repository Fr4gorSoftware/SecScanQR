package de.t_dankworth.secscanqr.activities.generator;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;


import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.GeneratorListAdapter;
import de.t_dankworth.secscanqr.util.GeneralHandler;


/**
 * Created by Thore Dankworth
 * Last Update: 12.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is just a forwarding to the specific generators
 */

public class GenerateActivity extends AppCompatActivity {

    final Activity activity = this;
    private GeneralHandler generalHandler;
    ListView lvGenerators;



    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_generate);
        lvGenerators = (ListView) findViewById(R.id.lvGenerators);


        GeneratorListAdapter listAdapter = new GeneratorListAdapter(GenerateActivity.this, R.layout.listview_row, getResources().getStringArray(R.array.generators));

        lvGenerators.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = lvGenerators.getItemAtPosition(i).toString();
                switch (choice) {
                    case "BARCODE":
                        startActivity(new Intent(GenerateActivity.this, BarcodeGenerateActivity.class));
                        break;
                    case "TEXT":
                        startActivity(new Intent(GenerateActivity.this, TextGeneratorActivity.class));
                        break;
                    case "GEO":
                        startActivity(new Intent(GenerateActivity.this, GeoGeneratorActivity.class));
                        break;
                    case "VCARD":
                        startActivity(new Intent(GenerateActivity.this, VCardGeneratorActivity.class));
                        break;
                    default:
                        startActivity(new Intent(GenerateActivity.this, BarcodeGenerateActivity.class));
                        break;
                }
            }
        });

        lvGenerators.setAdapter(listAdapter);
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
    }



}
