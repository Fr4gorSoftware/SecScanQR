package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.generator.GenerateActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 17.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is the MainActivity and is the starting point of the App
 * From here the user can start a QR-Code scan, go to the history and can generate Qr-Codes
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    final Activity activity = this;
    private GeneralHandler generalHandler;
    private CardView scanCard, generateCard, historyCard, settingsCard;

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_main);

        scanCard = (CardView) findViewById(R.id.scan_card);
        generateCard = (CardView) findViewById(R.id.generate_card);
        historyCard = (CardView) findViewById(R.id.history_card);
        settingsCard = (CardView) findViewById(R.id.settings_card);

        scanCard.setOnClickListener(this);
        generateCard.setOnClickListener(this);
        historyCard.setOnClickListener((View.OnClickListener) this);
        settingsCard.setOnClickListener(this);

        //Autostart Scanner if activated
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean auto_scan = prefs.getBoolean("pref_start_auto_scan", false);
        if(auto_scan == true){
            startActivity(new Intent(MainActivity.this, ScannerActivity.class));
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan_card:
                startActivity(new Intent(MainActivity.this, ScannerActivity.class));
                break;
            case R.id.generate_card:
                startActivity(new Intent(MainActivity.this, GenerateActivity.class));
                break;
            case R.id.history_card:
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                break;
            case R.id.settings_card:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * This method inflate the menu; this adds items to the action bar if it is present.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
