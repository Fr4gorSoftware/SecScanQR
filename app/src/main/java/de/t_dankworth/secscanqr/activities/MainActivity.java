package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.generator.GenerateActivity;
import de.t_dankworth.secscanqr.util.BottomNavigationViewHelper;
import de.t_dankworth.secscanqr.util.DatabaseHelper;

import static de.t_dankworth.secscanqr.util.ButtonHandler.copyToClipboard;
import static de.t_dankworth.secscanqr.util.ButtonHandler.openInWeb;
import static de.t_dankworth.secscanqr.util.ButtonHandler.resetScreenInformation;
import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;

/**
 * Created by Thore Dankworth
 * Last Update: 29.03.2019
 * Last Update by Thore Dankworth
 *
 * This class is the MainActivity and is the starting point of the App
 * From here the User can start a QR-Code scan and can go to the Generate Activity
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTvInformation, mTvFormat, mLabelInformation, mLabelFormat;
    private BottomNavigationView action_navigation;
    final Activity activity = this;
    private String qrcode = "", qrcodeFormat = "";
    private int camera = 0;
    private DatabaseHelper mDatabaeHelper;
    private static final String STATE_QRCODE = MainActivity.class.getName();
    private static final String STATE_QRCODEFORMAT = "format";

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
                    copyToClipboard(mTvInformation, qrcode, activity);
                    return true;
                case R.id.main_action_navigation_reset:
                    resetScreenInformation(mTvInformation, mTvFormat, mLabelInformation, mLabelFormat, qrcode, qrcodeFormat, action_navigation);
                    return true;
                case R.id.main_action_navigation_openInWeb:
                    openInWeb(qrcode, qrcodeFormat, activity);
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
        savedInstanceState.putString(STATE_QRCODEFORMAT, qrcodeFormat);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTheme();
    }

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        loadTheme();
        setContentView(R.layout.activity_main);
        mTvInformation = (TextView) findViewById(R.id.tvTxtqrcode);
        mTvFormat = (TextView) findViewById(R.id.tvFormat);
        mLabelInformation = (TextView) findViewById(R.id.labelInformation);
        mLabelFormat = (TextView) findViewById(R.id.labelFormat);
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
            qrcode = savedInstanceState.getString(STATE_QRCODE);
            qrcodeFormat = savedInstanceState.getString(STATE_QRCODEFORMAT);

            if(qrcode.equals("")){

            } else {
                mTvFormat.setVisibility(View.VISIBLE);
                mLabelInformation.setVisibility(View.VISIBLE);
                mLabelFormat.setVisibility(View.VISIBLE);
                mTvFormat.setText(qrcodeFormat);
                mTvInformation.setText(qrcode);
                action_navigation.setVisibility(View.VISIBLE);

            }

        } else {
            //Autostart Scanner if activated
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String auto_scan = prefs.getString("pref_auto_scan", "");
            if(auto_scan.equals("true")){
                zxingScan();
            }
        }

        // Get intent, action and MINE type and check if the intent was started by a share to module from an other app
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null){
            if("image/*".equals(type)){
                handleSendPicture();
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
            case R.id.settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
                qrcodeFormat = result.getFormatName();
                qrcode = result.getContents();
                if(!qrcode.equals("")){
                    mTvFormat.setVisibility(View.VISIBLE);
                    mLabelInformation.setVisibility(View.VISIBLE);
                    mLabelFormat.setVisibility(View.VISIBLE);
                    mTvFormat.setText(qrcodeFormat);
                    mTvInformation.setText(qrcode);
                    action_navigation.setVisibility(View.VISIBLE);

                    //Check if history is activated
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String history_setting = prefs.getString("pref_history", "");
                    if(history_setting.equals("false")){
                        //Don't save QR-Code in history
                    } else {
                        addToDatabase(mTvInformation.getText().toString(), mTvFormat.getText().toString());
                    }
                    //Automatic Clipboard if activated
                    String auto_scan = prefs.getString("pref_auto_clipboard", "");
                    if(auto_scan.equals("true")){
                        copyToClipboard(mTvInformation, qrcode, activity);
                    }
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

    /**
     * Takes the scanned code hands over the code to the method addData in the DatabaseHelper
     * @param newCode = scanned qr-code/barcode
     */
    public void addToDatabase(String newCode, String format){
        boolean insertData = mDatabaeHelper.addData(newCode);
        if(!insertData){
            Toast.makeText(this, getResources().getText(R.string.error_add_to_database), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Depending on the saved settings. The day or night mode will be loaded
     */
    private void loadTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String history_setting = prefs.getString("pref_day_night_mode", "");
        if(history_setting.equals("1")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void handleSendPicture(){
        Uri imageUri = (Uri) getIntent().getExtras().get(Intent.EXTRA_STREAM);
        InputStream imageStream = null;
        try{
            imageStream = getContentResolver().openInputStream(imageUri);
        } catch (FileNotFoundException e){
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_file_not_found), Toast.LENGTH_LONG);
        }
        //decoding bitmap
        Bitmap bMap = BitmapFactory.decodeStream(imageStream);
        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        // copy pixel data from the Bitmap into the 'intArray' array
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(),
                bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(),
                bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        try{
            Hashtable<DecodeHintType, Object> decodeHints = new Hashtable<DecodeHintType, Object>();
            decodeHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            decodeHints.put(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

            Result result = reader.decode(bitmap, decodeHints);
            qrcode =  result.getText();

            if(qrcode != null){
                mLabelInformation.setVisibility(View.VISIBLE);
                mTvInformation.setText(qrcode);
                action_navigation.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG);
            }
        } catch (FormatException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG);
        } catch (ChecksumException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG);
        } catch (NotFoundException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG);
        }
    }
}
