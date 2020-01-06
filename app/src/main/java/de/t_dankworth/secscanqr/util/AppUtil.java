package de.t_dankworth.secscanqr.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class AppUtil {
    private static final String TAG = "AppUtil";

    public static boolean isPackageAvailable(Context context, String packageName) {
        try {
            if (!context.getPackageManager().getPackageInfo(packageName, 0).packageName.equalsIgnoreCase(packageName)) {
                return false;
            }
            Log.w(TAG, "isPackageAvailable - getPackageName.equalsIgnoreCase(packageName) - true");
            return true;
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Unable to query for Package - " + packageName + " with error " + e.getMessage());
            return false;
        }
    }


    public static boolean checkPackage(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}
