package com.example.tethering.tethering;

import android.app.Application;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TetheringApplication extends Application {

    private static Logger LOG;

    @Override
    public void onCreate() {
        super.onCreate();
        LOG = LoggerFactory.getLogger(TetheringApplication.class);
        LOG.debug("application created");
    }
}
