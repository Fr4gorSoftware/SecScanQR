package de.t_dankworth.secscanqr.util;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.zxing.BarcodeFormat;

import de.t_dankworth.secscanqr.R;

/**
 * Created by Thore Dankworth
 * Last Update: 10.12.2019
 * Last Update by Thore Dankworth
 *
 * This class handles the functionality general functionality like load dark theme.
 */

public class GeneralHandler {

    private Activity activity;

    public GeneralHandler(Activity activity){
        this.activity = activity;
    }

    /**
     * Depending on the saved settings. The day or night mode will be loaded
     */
    public void loadTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        String history_setting = prefs.getString("pref_day_night_mode", "");
        if(history_setting.equals("1")){
            activity.setTheme(R.style.darktheme);
        }
    }

    /**
     * Method is used to convert parsed id to BarcodeFormat to create an image of the qr code
     * @param id as int
     * @return format as BarcodeFormat
     */
    public BarcodeFormat idToBarcodeFormat(int id){
        BarcodeFormat format;
        switch(id){
            case 1:
                format = BarcodeFormat.CODABAR;
                break;
            case 2:
                format= BarcodeFormat.CODE_128;
                break;
            case 3:
                format = BarcodeFormat.CODE_39;
                break;
            case 4:
                format = BarcodeFormat.EAN_13;
                break;
            case 5:
                format = BarcodeFormat.EAN_8;
                break;
            case 6:
                format = BarcodeFormat.ITF;
                break;
            case 7:
                format = BarcodeFormat.PDF_417;
                break;
            case 8:
                format = BarcodeFormat.UPC_A;
                break;
            case 9:
                format = BarcodeFormat.QR_CODE;
                break;
            case 10:
                format = BarcodeFormat.AZTEC;
                break;
            default:
                format = BarcodeFormat.CODABAR;
                break;
        }
        return format;
    }

    /**
     * Method is used to convert String to id which Activity GeneratorResultActivity can works with
     * @param format as String
     * @return id as int
     */
    public int StringToBarcodeId(String format){
        int id;
        switch(format) {
            case "CODBAR":
                id = 1;
                break;
            case "CODE_128":
                id = 2;
                break;
            case "CODE_39":
                id = 3;
                break;
            case "EAN_13":
                id = 4;
                break;
            case "EAN_8":
                id = 5;
                break;
            case "ITF":
                id = 6;
                break;
            case "PDF_417":
                id = 7;
                break;
            case "UPC_A":
                id = 8;
                break;
            case "QR_CODE":
                id = 9;
                break;
            case "AZTEC":
                id = 10;
                break;
            default:
                id = 9;
                break;
        }
        return id;
    }

    /**
     * Method is used to convert parsed String to BarcodeFormat to create an image of the qr code
     * @param stringFormat as String
     * @return format as BarcodeFormat
     */
    public BarcodeFormat StringToBarcodeFormat(String stringFormat){
        BarcodeFormat format;
        switch(stringFormat){
            case "CODBAR":
                format = BarcodeFormat.CODABAR;
                break;
            case "CODE_128":
                format= BarcodeFormat.CODE_128;
                break;
            case "CODE_39":
                format = BarcodeFormat.CODE_39;
                break;
            case "EAN_13":
                format = BarcodeFormat.EAN_13;
                break;
            case "EAN_8":
                format = BarcodeFormat.EAN_8;
                break;
            case "ITF":
                format = BarcodeFormat.ITF;
                break;
            case "PDF_417":
                format = BarcodeFormat.PDF_417;
                break;
            case "UPC_A":
                format = BarcodeFormat.UPC_A;
                break;
            case "QR_CODE":
                format = BarcodeFormat.QR_CODE;
                break;
            case "AZTEC":
                format = BarcodeFormat.AZTEC;
                break;
            default:
                format = BarcodeFormat.CODABAR;
                break;
        }
        return format;
    }
}
