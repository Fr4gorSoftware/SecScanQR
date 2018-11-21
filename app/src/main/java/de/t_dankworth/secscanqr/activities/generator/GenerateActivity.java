package de.t_dankworth.secscanqr.activities.generator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.t_dankworth.secscanqr.R;


/**
 * Created by Thore Dankworth
 * Last Update: 17.12.2017
 * Last Update by Thore Dankworth
 * <p>
 * This class is just a forwarding to the specific generators
 */

public class GenerateActivity extends AppCompatActivity {

    final AppCompatActivity activity = this;
    ListView lvGenerators;


    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_generate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lvGenerators = findViewById(R.id.lvGenerators);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(GenerateActivity.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.generators));

        lvGenerators.setOnItemClickListener((adapterView, view, i, l) -> {
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
                default:
                    startActivity(new Intent(GenerateActivity.this, VCardGeneratorActivity.class));
                    break;
            }
        });

        lvGenerators.setAdapter(listAdapter);
    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
