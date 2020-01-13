package de.t_dankworth.secscanqr.activities.generator;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.MultiFormatWriter;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.util.GeneralHandler;

/**
 * Created by Thore Dankworth
 * Last Update: 13.01.2020
 * Last Update by Thore Dankworth
 *
 * This class is all about the wifi information to QR-Code Generate Activity.
 */

public class WifiGeneratorActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    EditText tfSSID, tfPassword;
    CheckBox cbHidden;
    String hidden = "false";
    int format;
    Button btnGenerate;
    String ssid, password, encrypt ,result;
    MultiFormatWriter multiFormatWriter;

    final Activity activity = this;
    private static final String STATE_SSID = "";
    private static final String STATE_PASSWORD = "";
    private static final String STATE_HIDDEN = "";
    private static final String STATE_ENCRYPT = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        GeneralHandler generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_wifi_generator);

        tfSSID = (EditText) findViewById(R.id.tfSSID);
        tfPassword = (EditText) findViewById(R.id.tfPassword);
        cbHidden = (CheckBox) findViewById(R.id.cbHidden);
        btnGenerate = (Button) findViewById(R.id.btnGenerateWifi);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssid = tfSSID.getText().toString().trim();
                password = tfPassword.getText().toString().trim();
                if(ssid.equals("") || (encrypt.equals("WEP") && password.equals("")) || (encrypt.equals("WPA") && password.equals(""))){
                    Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_geo_first), Toast.LENGTH_SHORT).show();
                } else {
                    multiFormatWriter = new MultiFormatWriter();
                    try{
                        if(hidden.equals("true") && encrypt.equals("nopass")) {
                            result = "WIFI:S:" + ssid + ";T:" + encrypt + ";P:;H:true;";
                        } else if (hidden.equals("false") && encrypt.equals("nopass")){
                            result = "WIFI:S:" + ssid + ";T:" + encrypt + ";P:;;";
                        } else if (hidden.matches("false")){
                            result = "WIFI:S:" + ssid + ";T:" + encrypt + ";P:" + password + ";;";
                        } else {
                            result = "WIFI:S:" + ssid + ";T:" + encrypt + ";P:" + password + ";H:true;";
                        }
                        openResultActivity();
                    } catch (Exception e){
                        Toast.makeText(activity.getApplicationContext(), getResources().getText(R.string.error_generate), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Setup the Spinner Menu for the different formats
        Spinner formatSpinner = (Spinner) findViewById(R.id.spinner);
        Spinner encrypSpinner = (Spinner) findViewById(R.id.spinnerWifi);
        formatSpinner.setOnItemSelectedListener(this);
        encrypSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.text_formats_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.text_formats_encryption, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        encrypSpinner.setAdapter(adapter);

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            ssid = (String) savedInstanceState.get(STATE_SSID);
            tfSSID.setText(ssid);
            password = (String) savedInstanceState.get(STATE_PASSWORD);
            tfPassword.setText(password);
            encrypt = (String) savedInstanceState.get(STATE_ENCRYPT);
            if(encrypt.equals("nopass")){
                tfPassword.setVisibility(View.GONE);
            }
            hidden = (String) savedInstanceState.get(STATE_HIDDEN);
            if(hidden.equals("false")) {
                cbHidden.setChecked(false);
            }
        }


    }

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString(STATE_SSID, ssid);
        savedInstanceState.putString(STATE_PASSWORD, password);
        savedInstanceState.putString(STATE_ENCRYPT, encrypt);
        savedInstanceState.putString(STATE_HIDDEN, hidden);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String compare = parent.getItemAtPosition(position).toString();
        if(compare.equals("AZTEC")){
            format = 10;
        } else if(compare.equals("QR_CODE")){
            format = 9;
        } else if(compare.equals("NONE")){
            encrypt = "nopass";
            tfPassword.setVisibility(View.GONE);
        } else if (compare.equals("WEP")){
            encrypt = "WEP";
            tfPassword.setVisibility(View.VISIBLE);
        } else if (compare.equals("WPA/WPA2")){
            encrypt = "WPA";
            tfPassword.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        format = 9;
        encrypt = "nopass";
    }

    /**
     * Handles functionality behind the Checkboxes
     */
    public void onClickCheckboxes(View v){
        if(cbHidden.isChecked()){
            hidden = "true";
        } else{
            hidden = "false";
        }
    }

    /**
     *  This method will launch a new Activity were the generated QR-Code will be displayed.
     */
    private void openResultActivity(){
        Intent intent = new Intent(this, GeneratorResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("CODE", result);
        bundle.putInt("FORMAT", format);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
