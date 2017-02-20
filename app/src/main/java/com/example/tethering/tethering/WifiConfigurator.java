package com.example.tethering.tethering;

import android.net.wifi.WifiConfiguration;

public class WifiConfigurator {

    enum Security {
        NONE(WifiConfiguration.KeyMgmt.NONE),
        WPA_PSK(WifiConfiguration.KeyMgmt.WPA_PSK);

        private int keyMgmt;

        Security(int keyMgmt) {
            this.keyMgmt = keyMgmt;
        }

        public int getKeyMgmt() {
            return keyMgmt;
        }

        public static String[] names() {
            String[] names = new String[Security.values().length];
            for (int i = 0; i < Security.values().length; ++i) {
                names[i] = Security.values()[i].name();
            }
            return names;
        }
    }
}
