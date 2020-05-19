package de.t_dankworth.secscanqr.activities.generator;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 13.12.2019
 * Last Update by Thore Dankworth
 *
 * This class is all about the Text to QR-Code Generate Activity.
 */

public class TextGeneratorActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    EditText text;
    int format;
    String text2Qr;
    Button btnGenerate;
    private static final String STATE_TEXT = MainActivity.class.getName();



    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_text_generator);
        text = (EditText) findViewById(R.id.txtQR);
        btnGenerate = (Button) findViewById(R.id.btnGenerateText);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
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

        //Setup the Spinner Menu for the different formats
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.text_formats_array, R.layout.spinner_item);
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
            else if ("text/x-vcard".equals(type)){
                handleSendContact(intent); //call method to handle sended contact
            }
        }

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
     * This method handles Text that was shared by an other app to SecScanQR to generate a qr code
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
     * This method handles VCF that was shared by the contacts app to SecScanQR to generate a qr code
     * @param intent from Share to from Contacts app
     */
    private void handleSendContact(Intent intent){
        Uri uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
        ContentResolver cr = getContentResolver();
        InputStream stream = null;
        try {
            stream = cr.openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        StringBuffer fileContent = new StringBuffer("");
        int ch;
        try {
            while( (ch = stream.read()) != -1)
                fileContent.append((char)ch);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = new String(fileContent);
        text.setText(data);
        text2Qr = data;
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
