package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.t_dankworth.secscanqr.R;

/**
 * Created by Thore Dankworth
 * Last Update: 25.10.2020
 * Last Update by Thore Dankworth
 *
 * This class is used from third party apps to scan QR Codes for their needs.
 * If your desired app does not support SecScanQR for scanning QR-Codes please ask them to implement it.
 */

public class ThirdPartyScannerActivity extends AppCompatActivity {

    final Activity activity = this;
    private String qrcode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_party_scanner);

        zxingScan();
    }

    /**
     * This method handles the results of the scan
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();

        if(result != null){
            if(result.getContents()==null){
                setResult(RESULT_CANCELED, intent);
                finish();
                return;
            } else {
                qrcode = result.getContents();
                intent.putExtra("SCAN_RESULT", qrcode);
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * This method handles the communication to the ZXING API -> Apache License 2.0
     * For more information please check out the link below.
     *
     * http://www.apache.org/licenses/LICENSE-2.0
     */
    public void zxingScan(){
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.addExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt((String) getResources().getText(R.string.xzing_label));

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String camera_setting = prefs.getString("pref_camera", "");
        if(camera_setting.equals("1")){
            integrator.setCameraId(1);
        } else {
            integrator.setCameraId(0);
        }

        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        try {
            integrator.initiateScan();
        } catch (ArithmeticException e){

        }
    }
}
