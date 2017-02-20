package com.example.tethering.tethering;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class EnableWriteSettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_write_settings);
        initUI();
    }

    private void initUI() {
        findViewById(R.id.enable_write_settings_button).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS));
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }
}
