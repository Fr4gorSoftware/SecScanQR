package de.t_dankworth.secscanqr.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.BottomNavigationView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import de.t_dankworth.secscanqr.R;


/**
 * Created by Thore Dankworth
 * Last Update: 04.09.2017
 * Last Update by Thore Dankworth
 *
 * This class handles the functionality of the buttons like share, reset, copy etc.
 */

public class ButtonHandler {

    /**
     * This method resets all the information that were shown on the MainActivty
     * @param tv = TextView were the qrcode is shown
     * @param qrcode = the qrcode as a String - Needs to be reset for orientation switches
     * @param buttonContainer = The Container as a LinearLayout with all the Buttons
     */
    public static void resetScreenInformation(TextView tv, String qrcode, BottomNavigationView buttonContainer){
        tv.setText(R.string.default_text_main_activity);
        qrcode = "";
        buttonContainer.setVisibility(View.INVISIBLE);
    }

    /**
     * This method copies the information of the QR-Code to the clipboard
     * @param tv = TextView were the qrcode is shown
     * @param qrcode = the qrcode as a String
     * @param activity = Activty were the method was called. Needed for Toats and Clipboard
     */
    public static void copyToClipboard(TextView tv, String qrcode, Activity activity){
        ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(tv.getText(), qrcode);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(activity, activity.getResources().getText(R.string.notice_clipoard), Toast.LENGTH_LONG).show();
    }

    /**
     * This method handles the sharing functionality
     * @param qrcode = the qrcode as a String
     * @param activity = Activty were the method was called. Needed for sharing intent
     */
    public static void shareTo(String qrcode, Activity activity){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, qrcode);
        sendIntent.setType("text/plain");
        activity.startActivity(Intent.createChooser(sendIntent, activity.getResources().getText(R.string.send_to)));
    }

    /**
     * This method searches for the qr/barcode on the internet
     * @param qrcode = the qrcode as a String
     * @param activity = Activty were the method was called.Needed for Toast and web intent
     */
    public static void webSearch(String qrcode, Activity activity){
        if(qrcode.equals("")){
            Toast.makeText(activity.getApplicationContext(), activity.getResources().getText(R.string.error_scan_first), Toast.LENGTH_SHORT).show();
        } else {
            Uri uri = Uri.parse("http://www.google.com/#q=" + qrcode);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        }
    }

}
