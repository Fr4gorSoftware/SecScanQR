package de.t_dankworth.secscanqr.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.EnumMap;
import java.util.Map;

import de.t_dankworth.secscanqr.R;
import de.t_dankworth.secscanqr.activities.generator.GeneratorResultActivity;
import de.t_dankworth.secscanqr.util.DatabaseHelper;
import de.t_dankworth.secscanqr.util.GeneralHandler;

import static de.t_dankworth.secscanqr.util.ButtonHandler.copyToClipboard;
import static de.t_dankworth.secscanqr.util.ButtonHandler.createContact;
import static de.t_dankworth.secscanqr.util.ButtonHandler.openInWeb;
import static de.t_dankworth.secscanqr.util.ButtonHandler.shareTo;

/**
* Created by Thore Dankworth
* Last Update: 20.05.2020
* Last Update by Thore Dankworth
*
* This class is the HistoryDetailsActivity shows details and further functionality for the chosen item
*/

public class HistoryDetailsActivity extends AppCompatActivity {

    private GeneralHandler generalHandler;

    public static final String EXTRA_FORMAT =
            "de.t-dankworth.secscanqr.EXTRA_FORMAT";
    public static final String EXTRA_INFORMATION =
            "de.t-dankworth.secscanqr.EXTRA_INFORMATION";

    private TextView tvCode, tvFormat;
    private ImageView codeImage;
    private BottomNavigationView action_navigation;
    Bitmap bitmap;
    MultiFormatWriter multiFormatWriter;

    DatabaseHelper historyDatabaseHelper;
    final Activity activity = this;

    private String selectedCode, selectedFormat;

    /**
     * This method handles the main navigation
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //Following cases using a method from ButtonHandler
                case R.id.history_action_navigation_copy:
                    copyToClipboard(tvCode, selectedCode, activity);
                    return true;
                case R.id.history_action_navigation_openInWeb:
                    openInWeb(selectedCode, activity);
                    return true;
                case R.id.history_action_navigation_createContact:
                    createContact(selectedCode, activity);
                    return true;
                case R.id.history_action_navigation_share:
                    shareTo(selectedCode, activity);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generalHandler = new GeneralHandler(this);
        generalHandler.loadTheme();
        setContentView(R.layout.activity_history_details);
        tvCode = (TextView) findViewById(R.id.tvTxtqrcodeHistory);
        tvFormat = (TextView) findViewById(R.id.tvFormatHistory);
        codeImage = (ImageView) findViewById(R.id.resultImageHistory);
        codeImage.setClickable(true);

        action_navigation = (BottomNavigationView) findViewById(R.id.history_action_navigation);
        action_navigation.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
        action_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        historyDatabaseHelper = new DatabaseHelper(this);

        //Get the extra information from the history listview. and set the text in the textview eqaul to code
        Intent receivedIntent = getIntent();
        selectedCode = receivedIntent.getStringExtra(EXTRA_INFORMATION);
        tvCode.setText(selectedCode);
        selectedFormat = receivedIntent.getStringExtra(EXTRA_FORMAT);
        tvFormat.setText(selectedFormat);
        showQrImage();

        if(selectedCode.contains("BEGIN:VCARD") & selectedCode.contains("END:VCARD")){
            action_navigation.getMenu().removeItem(R.id.history_action_navigation_openInWeb);
        } else {
            action_navigation.getMenu().removeItem(R.id.history_action_navigation_createContact);
        }

        codeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryDetailsActivity.this , GeneratorResultActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("CODE", selectedCode);
                int formatID = generalHandler.StringToBarcodeId(selectedFormat);
                bundle.putInt("FORMAT", formatID);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
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
            BarcodeFormat format = generalHandler.StringToBarcodeFormat(selectedFormat);
            BitMatrix bitMatrix = multiFormatWriter.encode(selectedCode, format, 250,250, hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);
            codeImage.setImageBitmap(bitmap);
        } catch (Exception e){
            codeImage.setVisibility(View.GONE);
        }
    }
}
