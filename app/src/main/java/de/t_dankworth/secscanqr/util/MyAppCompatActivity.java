package de.t_dankworth.secscanqr.util;

import androidx.appcompat.app.AppCompatActivity;

/**
 * A {@link androidx.appcompat.app.AppCompatActivity} which overrides
 * the home navigation button to call onBackPressed.
 */
public abstract class MyAppCompatActivity extends AppCompatActivity {

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }
    
}
