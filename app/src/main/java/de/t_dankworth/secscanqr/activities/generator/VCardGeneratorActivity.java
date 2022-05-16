package de.t_dankworth.secscanqr.activities.generator;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.MainActivity;
import de.t_dankworth.secscanqr.util.GeneralHandler;

import de.t_dankworth.secscanqr.util.MyAppCompatActivity;

/**
 * Created by Thore Dankworth
 * Last Update: 01.05.2020
 * Last Update by Thore Dankworth
 *
 * This class is all about the VCard to QR-Code Generate Activity.
 */

public class VCardGeneratorActivity extends MyAppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfFirstName, tfName, tfOrg, tfTeleWork, tfTelePrivate, tfMobil, tfEmail, tfWeb, tfStreet, tfPLZ, tfCity, tfState, tfCountry, tfPhoto, tfBday, tfNote;
    int format;
    String[] text2Qr = new String[16];;
    String vcardCode;
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
        setContentView(R.layout.activity_vcard_generator);
        initializeInputFields();
        btnGenerate = (Button) findViewById(R.id.btnGenerateVCard);
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertValuesIntoArray();
                if(text2Qr[0].equals("") && text2Qr[1].equals("")){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_fn_or_name_first), Toast.LENGTH_SHORT).show();
                } else {
                    buildVCardCode();
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



        if(savedInstanceState != null){
            text2Qr = (String[]) savedInstanceState.get(STATE_TEXT);
            recoverOldValues();
        }

    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putStringArray(STATE_TEXT, text2Qr);
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
     * Initializes all input fields
     */
    private void initializeInputFields(){
        tfFirstName = (EditText) findViewById(R.id.txtFN);
        tfName = (EditText) findViewById(R.id.txtName);
        tfBday = (EditText) findViewById(R.id.txtBday);
        tfOrg = (EditText) findViewById(R.id.txtOrg);
        tfPhoto = (EditText) findViewById(R.id.txtPhotoUri);
        tfTeleWork = (EditText) findViewById(R.id.txtTeleWork);
        tfTelePrivate = (EditText) findViewById(R.id.txtTelePrivat);
        tfMobil = (EditText) findViewById(R.id.txtMobil);
        tfEmail = (EditText) findViewById(R.id.txtEmail);
        tfWeb = (EditText) findViewById(R.id.txtWeb);
        tfStreet = (EditText) findViewById(R.id.txtStreet);
        tfPLZ = (EditText) findViewById(R.id.txtPLZ);
        tfCity = (EditText) findViewById(R.id.txtCity);
        tfState = (EditText) findViewById(R.id.txtState);
        tfCountry = (EditText) findViewById(R.id.txtCountry);
        tfNote = (EditText) findViewById(R.id.txtNote);
    }

    /**
     * The values of the input fields will be placed into a string array
     */
    private void insertValuesIntoArray(){
        text2Qr[0] = tfName.getText().toString().trim();
        text2Qr[1] = tfFirstName.getText().toString().trim();
        text2Qr[2] = tfBday.getText().toString().trim();
        text2Qr[3] = tfOrg.getText().toString().trim();
        text2Qr[4] = tfPhoto.getText().toString().trim();
        text2Qr[5] = tfWeb.getText().toString().trim();
        text2Qr[6] = tfEmail.getText().toString().trim();
        text2Qr[7] = tfMobil.getText().toString().trim();
        text2Qr[8] = tfTeleWork.getText().toString().trim();
        text2Qr[9] = tfTelePrivate.getText().toString().trim();
        text2Qr[10] = tfStreet.getText().toString().trim();
        text2Qr[11] = tfCity.getText().toString().trim();
        text2Qr[12] = tfState.getText().toString().trim();
        text2Qr[13] = tfPLZ.getText().toString().trim();
        text2Qr[14] = tfCountry.getText().toString().trim();
        text2Qr[15] = tfNote.getText().toString().trim();
    }

    /**
     * This will write the values of the array into the input fields
     * Needed if screen gets rotated
     */
    private void recoverOldValues(){
        tfName.setText(text2Qr[0]);
        tfFirstName.setText(text2Qr[1]);
        tfBday.setText(text2Qr[2]);
        tfOrg.setText(text2Qr[3]);
        tfPhoto.setText(text2Qr[4]);
        tfWeb.setText(text2Qr[5]);
        tfEmail.setText(text2Qr[6]);
        tfMobil.setText(text2Qr[7]);
        tfTeleWork.setText(text2Qr[8]);
        tfTelePrivate.setText(text2Qr[9]);
        tfStreet.setText(text2Qr[10]);
        tfCity.setText(text2Qr[11]);
        tfState.setText(text2Qr[12]);
        tfPLZ.setText(text2Qr[13]);
        tfCountry.setText(text2Qr[14]);
        tfNote.setText(text2Qr[15]);
    }

    /**
     * This method builds the final string which gets transformed into a qr-code
     */
    private void buildVCardCode(){
        vcardCode = "BEGIN:VCARD\nVERSION:2.1\nN:" + text2Qr[0] + ";" +text2Qr[1] + "\n";
        if(!text2Qr[2].equals("")){
            vcardCode += "BDAY:" + text2Qr[2] + "\n";
        }
        if(!text2Qr[3].equals("")){
            vcardCode += "ORG:" + text2Qr[3] + "\n";
        }
        if(!text2Qr[4].equals("")){
            vcardCode += "PHOTO;VALUE=uri:" + text2Qr[4] + "\n";
        }
        if(!text2Qr[5].equals("")){
            vcardCode += "URL:" + text2Qr[5] + "\n";
        }
        if(!text2Qr[6].equals("")){
            vcardCode += "EMAIL;TYPE=INTERNET:" + text2Qr[6] + "\n";
        }
        if(!text2Qr[7].equals("")){
            vcardCode += "TEL;CELL:" + text2Qr[7] + "\n";
        }
        if(!text2Qr[8].equals("")){
            vcardCode += "TEL;WORK;VOICE:" + text2Qr[8] + "\n";
        }
        if(!text2Qr[9].equals("")){
            vcardCode += "TEL;HOME;VOICE:" + text2Qr[9] + "\n";
        }
        if((!text2Qr[10].equals("")) || (!text2Qr[11].equals("")) || (!text2Qr[12].equals("")) || (!text2Qr[13].equals("")) || (!text2Qr[14].equals(""))){
            vcardCode += "ADR:;;" + text2Qr[10] + ";" + text2Qr[11] + ";" + text2Qr[12] + ";" + text2Qr[13] + ";" + text2Qr[14] + "\n";
        }
        if(!text2Qr[15].equals("")){
            vcardCode += "NOTE:" + text2Qr[15] + "\n";
        }
        vcardCode += "END:VCARD";
    }

    /**
     *  This method will launch a new Activity were the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", vcardCode);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
