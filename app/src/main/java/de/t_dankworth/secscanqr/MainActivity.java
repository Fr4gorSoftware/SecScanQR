package de.t_dankworth.secscanqr;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Thore Dankworth
 * Last Update: 15.08.2017
 * Last Update by Thore Dankworth
 *
 * This class is the MainActivity and is the starting point of the App
 * From here the User can start a QR-Code scan and can go to the Generate Activity
 *
 * Planed for upcoming Releases:
 * - Save generated QR-Code as a picture
 * - History
 * - Settings
 * - support for more languages
 * TODO: getResources().getText(R.string.send_to));
 */
public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private ConstraintLayout buttonContainer;
    private Button btnCopy, btnShare, btnDelete;
    final Activity activity = this;
    private String qrcode = "";
    private static final String STATE_QRCODE = MainActivity.class.getName();

    /**
     * This method handles the main navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    if(qrcode.equals("")){
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.error_scan_first), Toast.LENGTH_SHORT).show();
                    } else {
                        Uri uri = Uri.parse("http://www.google.com/#q=" + qrcode);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                    return true;
                case R.id.navigation_scan:
                    zxingScan();
                    return true;
                case R.id.navigation_generate:
                    startActivity(new Intent(MainActivity.this, GenerateActivity.class));
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
        btnCopy = (Button) findViewById(R.id.btn_copy);
        btnDelete = (Button) findViewById(R.id.btn_delete);
        buttonContainer = (ConstraintLayout) findViewById(R.id.container_btn);
        btnShare = (Button) findViewById(R.id.btn_share);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //All the OnClickListener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteScreenInformation();
            }
        });
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyToClipboard();
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTo();
            }
        });

        //If the device were rotated then restore information
        if(savedInstanceState != null){
            qrcode = (String) savedInstanceState.get(STATE_QRCODE);
            if(qrcode.equals("")){

            } else {
                mTextMessage.setText(qrcode);
                buttonContainer.setVisibility(View.VISIBLE);

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
                    buttonContainer.setVisibility(View.VISIBLE);
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
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt((String) getResources().getText(R.string.xzing_label));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    /**
     * This method resets all the information that were shown on screen
     */
    public void deleteScreenInformation(){
        mTextMessage.setText(R.string.default_text_main_activity);
        qrcode = "";
        buttonContainer.setVisibility(View.INVISIBLE);
    }

    /**
     * This method copies the information of the QR-Code to the clipboard
     */
    public void copyToClipboard(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(mTextMessage.getText(), qrcode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getResources().getText(R.string.notice_clipoard), Toast.LENGTH_LONG).show();
    }

    /**
     * This method handles the sharing functionality
     */
    public void shareTo(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, qrcode);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }
}
