package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.lifecycle.ViewModelProvider;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.android.Intents;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.generator.GeneratorResultActivity;
import de.t_dankworth.secscanqr.util.DatabaseHelper;
import de.t_dankworth.secscanqr.util.GeneralHandler;
import de.t_dankworth.secscanqr.util.HistoryEntity;
import de.t_dankworth.secscanqr.util.HistoryViewModel;

import de.t_dankworth.secscanqr.util.MyAppCompatActivity;

import static de.t_dankworth.secscanqr.util.ButtonHandler.copyToClipboard;
import static de.t_dankworth.secscanqr.util.ButtonHandler.createContact;
import static de.t_dankworth.secscanqr.util.ButtonHandler.openInWeb;
import static de.t_dankworth.secscanqr.util.ButtonHandler.resetScreenInformation;
import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;

/**
 * Created by Thore Dankworth
 * Last Update: 25.10.2020
 * Last Update by Thore Dankworth
 *
 * This class is the MainActivity and is the starting point of the App
 * From here the User can start a QR-Code scan and can go to the Generate Activity
 */
public class ScannerActivity extends MyAppCompatActivity {

    private TextView mTvInformation, mTvFormat, mLabelInformation, mLabelFormat;
    private BottomNavigationView action_navigation;
    private BottomNavigationItemView action_navigation_web_button, action_navigation_contact_button;
    private ImageView codeImage;
    private GeneralHandler generalHandler;
    Bitmap bitmap;
    MultiFormatWriter multiFormatWriter;
    final Activity activity = this;
    private String qrcode = "", qrcodeFormat = "";


    private static final SimpleDateFormat date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
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
                case R.id.navigation_scan:
                    zxingScan();
                    return true;
                //Following cases using a method from ButtonHandler
                case R.id.main_action_navigation_copy:
                    copyToClipboard(mTvInformation, qrcode, activity);
                    return true;
                case R.id.main_action_navigation_reset:
                    resetScreenInformation(mTvInformation, mTvFormat, mLabelInformation, mLabelFormat, action_navigation, codeImage);
                    qrcode = "";
                    qrcodeFormat = "";
                    return true;
                case R.id.main_action_navigation_openInWeb:
                    openInWeb(qrcode, qrcodeFormat, activity);
                    return true;
                case R.id.main_action_navigation_createContact:
                    createContact(qrcode, activity);
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
        generalHandler.loadTheme();
    }

    /**
     * Standard Android on create method that gets called when the activity
     * initialized.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_scanner);
        mTvInformation = (TextView) findViewById(R.id.tvTxtqrcode);
        mTvFormat = (TextView) findViewById(R.id.tvFormat);
        mLabelInformation = (TextView) findViewById(R.id.labelInformation);
        mLabelFormat = (TextView) findViewById(R.id.labelFormat);
        codeImage = (ImageView) findViewById(R.id.resultImageMain);
        codeImage.setClickable(true);

        BottomNavigationView main_navigation = (BottomNavigationView) findViewById(R.id.navigation);
        main_navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        main_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        main_navigation.clearFocus();

        action_navigation = (BottomNavigationView) findViewById(R.id.main_action_navigation);
        action_navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        action_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        action_navigation_web_button = (BottomNavigationItemView) findViewById(R.id.main_action_navigation_openInWeb);
        action_navigation_contact_button = (BottomNavigationItemView) findViewById(R.id.main_action_navigation_createContact);

        codeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerActivity.this , GeneratorResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CODE", qrcode);
                int formatID = generalHandler.StringToBarcodeId(qrcodeFormat);
                bundle.putInt("FORMAT", formatID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            qrcode = savedInstanceState.getString(STATE_QRCODE);
            qrcodeFormat = savedInstanceState.getString(STATE_QRCODEFORMAT);

            if(qrcode.equals("")){
                zxingScan();
            } else {
                codeImage.setVisibility(View.VISIBLE);
                showQrImage();
                mTvFormat.setVisibility(View.VISIBLE);
                mLabelInformation.setVisibility(View.VISIBLE);
                mLabelFormat.setVisibility(View.VISIBLE);
                mTvFormat.setText(qrcodeFormat);
                mTvInformation.setText(qrcode);
                action_navigation.setVisibility(View.VISIBLE);

                if(qrcode.contains("BEGIN:VCARD") & qrcode.contains("END:VCARD")){
                    action_navigation_web_button.setVisibility(View.GONE);
                    action_navigation_contact_button.setVisibility(View.VISIBLE);
                } else {
                    action_navigation_contact_button.setVisibility(View.GONE);
                    action_navigation_web_button.setVisibility(View.VISIBLE);
                }

            }

        } else {
            // Get intent, action and MINE type and check if the intent was started by a share to module from an other app
            Intent intent = getIntent();
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null){
                if(type.toLowerCase().startsWith("image")){
                    handleSendPicture();
                }
            } else {
                zxingScan();
            }
        }
    }

    /**
     * This method creates a picture of the scanned qr code
     */
    private void showQrImage() {
        multiFormatWriter = new MultiFormatWriter();
        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);

        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        try{
            BarcodeFormat format = generalHandler.StringToBarcodeFormat(qrcodeFormat);
            BitMatrix bitMatrix = multiFormatWriter.encode(qrcode, format, 250,250, hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            codeImage.setImageBitmap(bitmap);
            codeImage.setEnabled(true);
        } catch (Exception e){
            codeImage.setImageResource(R.drawable.ic_baseline_error_24);
            codeImage.setEnabled(false);
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
                    codeImage.setVisibility(View.VISIBLE);
                    showQrImage();
                    mTvFormat.setVisibility(View.VISIBLE);
                    mLabelInformation.setVisibility(View.VISIBLE);
                    mLabelFormat.setVisibility(View.VISIBLE);
                    mTvFormat.setText(qrcodeFormat);
                    mTvInformation.setText(qrcode);
                    action_navigation.setVisibility(View.VISIBLE);
                    if(qrcode.contains("BEGIN:VCARD") & qrcode.contains("END:VCARD")){
                        action_navigation_web_button.setVisibility(View.GONE);
                        action_navigation_contact_button.setVisibility(View.VISIBLE);
                    } else {
                        action_navigation_contact_button.setVisibility(View.GONE);
                        action_navigation_web_button.setVisibility(View.VISIBLE);
                    }


                    //Check if history is activated
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                    String history_setting = prefs.getString("pref_history", "");
                    if(history_setting.equals("false")){
                        //Don't save QR-Code in history
                    } else {
                        addToDatabase(mTvInformation.getText().toString(), mTvFormat.getText().toString());
                    }
                    //Automatic Clipboard if activated
                    Boolean auto_scan = prefs.getBoolean("pref_start_auto_clipboard", false);
                    if(auto_scan == true){
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

    /**
     * Takes the scanned code hands over the code to the method addData in the DatabaseHelper
     * @param newCode = scanned qr-code/barcode
     */
    public void addToDatabase(String newCode, String format){
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        HistoryEntity newEntry = new HistoryEntity(format, newCode,date.format(timestamp));
        HistoryViewModel historyViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(HistoryViewModel.class);
        historyViewModel.insert(newEntry);
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

            Result result = reader.decode(bitmap, decodeHints);
            qrcodeFormat =result.getBarcodeFormat().toString();
            qrcode =  result.getText();

            if(qrcode != null){
                codeImage.setVisibility(View.VISIBLE);
                mLabelFormat.setVisibility(View.VISIBLE);
                mTvFormat.setVisibility(View.VISIBLE);
                mTvFormat.setText(qrcodeFormat);
                mLabelInformation.setVisibility(View.VISIBLE);
                mTvInformation.setText(qrcode);
                action_navigation.setVisibility(View.VISIBLE);
                showQrImage();

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

                if(qrcode.contains("BEGIN:VCARD") & qrcode.contains("END:VCARD")){
                    action_navigation_web_button.setVisibility(View.GONE);
                    action_navigation_contact_button.setVisibility(View.VISIBLE);
                } else {
                    action_navigation_contact_button.setVisibility(View.GONE);
                    action_navigation_web_button.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG).show();
            }
        } catch (FormatException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG).show();
        } catch (ChecksumException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG).show();
        } catch (NotFoundException e) {
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG).show();
        } catch (ArrayIndexOutOfBoundsException e){
            Toast.makeText(activity, getResources().getText(R.string.error_code_not_found), Toast.LENGTH_LONG).show();
        }
    }
}
