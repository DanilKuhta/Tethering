package com.example.tethering.tethering;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;

public class TetheringActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tethering);
        getFragmentManager().beginTransaction().replace(R.id.wifi_tethering_fragment, Fragment.instantiate(this, WifiTetheringFragment.class.getName())).commit();
        getFragmentManager().beginTransaction().replace(R.id.bt_tethering_fragment, Fragment.instantiate(this, BtTetheringFragment.class.getName())).commit();
    }
}
