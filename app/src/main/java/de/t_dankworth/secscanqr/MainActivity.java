package de.t_dankworth.secscanqr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import de.t_dankworth.secscanqr.util.BottomNavigationViewHelper;
import de.t_dankworth.secscanqr.util.ButtonHandler;
import de.t_dankworth.secscanqr.util.DatabaseHelper;

import static de.t_dankworth.secscanqr.util.ButtonHandler.copyToClipboard;
import static de.t_dankworth.secscanqr.util.ButtonHandler.resetScreenInformation;
import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;
import static de.t_dankworth.secscanqr.util.ButtonHandler.webSearch;

/**
 * Created by Thore Dankworth
 * Last Update: 09.09.2017
 * Last Update by Thore Dankworth
 *
 * This class is the MainActivity and is the starting point of the App
 * From here the User can start a QR-Code scan and can go to the Generate Activity
 *
 * Planed for upcoming Releases:
 * - Save generated QR-Code as a picture
 * - Settings
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private BottomNavigationView action_navigation;
    final Activity activity = this;
    private String qrcode = "";
    private DatabaseHelper mDatabaeHelper;
    private static final String STATE_QRCODE = MainActivity.class.getName();

    /**
     * This method handles the main navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_history:
                    startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                    return true;
                case R.id.navigation_scan:
                    zxingScan();
                    return true;
                case R.id.navigation_generate:
                    startActivity(new Intent(MainActivity.this, GenerateActivity.class));
                    return true;
                //Following cases using a method from ButtonHandler
                case R.id.main_action_navigation_copy:
                    copyToClipboard(mTextMessage, qrcode, activity);
                    return true;
                case R.id.main_action_navigation_reset:
                    resetScreenInformation(mTextMessage, qrcode, action_navigation);
                    return true;
                case R.id.main_action_navigation_search:
                    webSearch(qrcode, activity);
                    return true;
                case R.id.main_action_navigation_share:
                    shareTo(qrcode, activity);
                    return true;


            }
            return false;
        }

    };

    /**
     * This method saves all data before the Activity will be destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
            savedInstanceState.putString(STATE_QRCODE, qrcode);
    }

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.txtqrcode);
        mDatabaeHelper = new DatabaseHelper(this);

        BottomNavigationView main_navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(main_navigation);
        main_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        main_navigation.clearFocus();

        action_navigation = (BottomNavigationView) findViewById(R.id.main_action_navigation);
        BottomNavigationViewHelper.disableShiftMode(action_navigation);
        action_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            qrcode = (String) savedInstanceState.get(STATE_QRCODE);
            if(qrcode.equals("")){

            } else {
                mTextMessage.setText(qrcode);
                action_navigation.setVisibility(View.VISIBLE);

            }

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
                AboutDialog aboutDialog = new AboutDialog(this);
                aboutDialog.setTitle(R.string.about_dialog);
                aboutDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method handles the results of the scan
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, getResources().getText(R.string.error_canceled_scan), Toast.LENGTH_LONG).show();
            } else {
                qrcode = result.getContents();
                if(!qrcode.equals("")){
                    mTextMessage.setText(qrcode);
                    action_navigation.setVisibility(View.VISIBLE);
                    addToDatabase(mTextMessage.getText().toString());
                }

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
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt((String) getResources().getText(R.string.xzing_label));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    /**
     * Takes the scanned code hands over the code to the method addData in the DatabaseHelper
     * @param newCode = scanned qr-code/barcode
     */
    public void addToDatabase(String newCode){
        boolean insertData = mDatabaeHelper.addData(newCode);
        if(!insertData){
            Toast.makeText(this, getResources().getText(R.string.error_add_to_database), Toast.LENGTH_LONG).show();
        }
    }
}
