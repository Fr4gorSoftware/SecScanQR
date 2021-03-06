package de.t_dankworth.secscanqr.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import de.t_dankworth.secscanqr.BuildConfig;
import de.t_dankworth.secscanqr.R;
import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    private boolean drakMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadTheme();
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .enableDarkMode(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getResources().getString(R.string.license) + "\n" + getResources().getString(R.string.zxing_license))
                .addItem(new Element().setTitle("Version " + versionName + " (Build " + versionCode + ")"))
                .addWebsite("https://github.com/Fr4gorSoftware/SecScanQR")
                .addGitHub("Fr4gorSoftware")
                .addItem(new Element().setTitle(getResources().getString(R.string.logo_designer)))
                .addItem(new Element().setTitle(getResources().getString(R.string.translator)))
                .addItem(getCopyRightsElement())
                .create();
        setContentView(aboutPage);
    }

    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = getResources().getString(R.string.copyright_holder);
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AboutActivity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }

    /**
     * Depending on the saved settings. The day or night mode will be loaded
     */
    private void loadTheme(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String history_setting = prefs.getString("pref_day_night_mode", "");
        if(history_setting.equals("1")){
            drakMode = true;
        } else {
            drakMode = false;
        }
    }
}