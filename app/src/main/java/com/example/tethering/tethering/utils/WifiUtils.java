package com.example.tethering.tethering.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WifiUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WifiUtils.class);

    public static boolean isWriteSettingsAllowed(Context context) {
        return !(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !android.provider.Settings.System.canWrite(context));
    }

    public static boolean setWifiTetheringEnabled(Context context, boolean enable, WifiConfiguration wifiConfiguration) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        wifiManager.isDeviceToApRttSupported();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                try {
                    return (boolean) method.invoke(wifiManager, wifiConfiguration, enable);
                } catch (Exception ex) {
                    LOG.error(ex.toString());
                }
                break;
            }
        }
        return false;
    }

    public static boolean isWifiTetheringEnabled(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        wifiManager.isDeviceToApRttSupported();
        for (Method method : methods) {
            if (method.getName().equals("isWifiApEnabled")) {
                try {
                    return (boolean) method.invoke(wifiManager);
                } catch (Exception ex) {
                    LOG.error(ex.toString());
                }
                break;
            }
        }
        return false;
    }

    public static WifiConfiguration createWifiConfiguration(String SSID, int keyMgmt, String preSharedKey, boolean hiddenSSID) {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = SSID;
        wifiConfiguration.allowedKeyManagement.set(keyMgmt);
        wifiConfiguration.hiddenSSID = hiddenSSID;
        wifiConfiguration.preSharedKey = preSharedKey;
        return wifiConfiguration;
    }

    public static boolean isWifiConfigurationReady(WifiConfiguration wifiConfiguration) {
        boolean hasName = !wifiConfiguration.SSID.isEmpty();
        boolean hasPassword = wifiConfiguration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK);
        boolean isPasswordValid = wifiConfiguration.preSharedKey != null && wifiConfiguration.preSharedKey.length() >= 8;
        return hasName && (!hasPassword || isPasswordValid);
    }

    public static void setBtTetheringEnabled(Context context, final boolean enable) {
        try {
            Class classBluetoothPan = Class.forName("android.bluetooth.BluetoothPan");
            Constructor btPanConstructor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
            btPanConstructor.setAccessible(true);
            btPanConstructor.newInstance(context, new  BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                    LOG.debug("bt service connected");

                    try {
                        boolean nowVal = ((Boolean) bluetoothProfile.getClass().getMethod("isTetheringOn", new Class[0]).invoke(bluetoothProfile));
                        if (nowVal != enable) {
                            bluetoothProfile.getClass().getMethod("setBluetoothTethering", new Class[]{Boolean.TYPE}).invoke(bluetoothProfile, enable);
                        }
                        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                            if (device.getName().contains("Nexus")) {
                                if (enable) {
                                    bluetoothProfile.getClass().getMethod("connect", BluetoothDevice.class).invoke(bluetoothProfile, device);
                                } else {
                                    bluetoothProfile.getClass().getMethod("disconnect", BluetoothDevice.class).invoke(bluetoothProfile, device);
                                }
                            }
                        }
                    } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
                        LOG.error(e.toString());
                    }


                }

                @Override
                public void onServiceDisconnected(int i) {
                    LOG.debug("bt service disconnected");
                }
            });
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }
}
