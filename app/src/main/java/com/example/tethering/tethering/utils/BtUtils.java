package com.example.tethering.tethering.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class BtUtils {

    private static final Logger LOG = LoggerFactory.getLogger(WifiUtils.class);

    public static Set<BluetoothDevice> getBoundedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getBondedDevices();
    }

    public static void enableBtTethering(Context context, final boolean enable, final OnBluetoothPanConnected onBluetoothPanConnected) {
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
                        onBluetoothPanConnected.onBluetoothPanConnected(bluetoothProfile);
                    } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
                        LOG.error(e.toString());
                    }
                }

                @Override
                public void onServiceDisconnected(int i) {
                    LOG.debug("bt service disconnected");
                }
            });
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
    }

    public interface OnBluetoothPanConnected {

        void onBluetoothPanConnected(BluetoothProfile bluetoothPan);
    }

    public static boolean connectTetheringDevice(BluetoothProfile bluetoothPan, BluetoothDevice bluetoothDevice) {
        try {
            return (boolean) bluetoothPan.getClass().getMethod("connect", BluetoothDevice.class).invoke(bluetoothPan, bluetoothDevice);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
        return false;
    }

    public static boolean isConnectedTetheringDevice(BluetoothProfile bluetoothPan, BluetoothDevice bluetoothDevice) {
        try {
            int state = (int) bluetoothPan.getClass().getMethod("getConnectionState", BluetoothDevice.class).invoke(bluetoothPan, bluetoothDevice);
            return state == BluetoothProfile.STATE_CONNECTED;
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
        return false;
    }

    public static boolean disconnectTetheringDevice(BluetoothProfile bluetoothPan, BluetoothDevice bluetoothDevice) {
        try {
            return (boolean) bluetoothPan.getClass().getMethod("disconnect", BluetoothDevice.class).invoke(bluetoothPan, bluetoothDevice);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
        return false;
    }
}
