package de.t_dankworth.secscanqr.activities.generator;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;

/**
 * Created by Thore Dankworth
 * Last Update: 16.01.2019
 * Last Update by Thore Dankworth
 *
 * This class is all about the Text to QR-Code Generate Activity. In this Class the functionality of generating a QR-Code Picture is covered.
 */

public class TextGeneratorActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    EditText text;
    int format;
    String text2Qr;
    private static final String STATE_TEXT = MainActivity.class.getName();



    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_generator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        text = (EditText) findViewById(R.id.txtQR);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.text_formats_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        //If the device were rotated then restore information
        if(savedInstanceState != null){
            text2Qr = (String) savedInstanceState.get(STATE_TEXT);
            text.setText(text2Qr);
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
                text2Qr = text.getText().toString().trim();
                if(text2Qr.equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_text_first), Toast.LENGTH_SHORT).show();
                } else {
                    openResultActivity();
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

    /**
     * This method handles Text that was shared by an other app to SecScanQR and generates a qr code
     * @param intent from Share to from other Apps
     */
    private void handleSendText(Intent intent){
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText != null){
            text.setText(sharedText);
            text2Qr = sharedText;
        }
    }

    /**
     * Generates the chosen format from the spinner menu
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        if(compare.equals("AZTEC")){
            format = 10;
        }
        else if(compare.equals("QR_CODE")){
            format = 9;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = 9;
    }


    /**
     *  This method will launch a new Activity were the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", text2Qr);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
