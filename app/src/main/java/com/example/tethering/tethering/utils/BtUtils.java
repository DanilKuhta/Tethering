package com.example.tethering.tethering.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BtUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BtUtils.class);

    public static Set<BluetoothDevice> getBoundedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter.getBondedDevices();
    }

    public static void openBtPan(Context context, final OnBluetoothPanConnected onBluetoothPanConnected) {
        try {
            Class classBluetoothPan = Class.forName("android.bluetooth.BluetoothPan");
            Constructor btPanConstructor = classBluetoothPan.getDeclaredConstructor(Context.class, BluetoothProfile.ServiceListener.class);
            btPanConstructor.setAccessible(true);
            btPanConstructor.newInstance(context, new  BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, BluetoothProfile bluetoothProfile) {
                    LOG.debug("bt service connected, profile : " + profile);
                    onBluetoothPanConnected.onBluetoothPanConnected(bluetoothProfile);
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    LOG.debug("bt service disconnected, profile : " + profile);
                    onBluetoothPanConnected.onBluetoothPanDisconnected();
                }
            });
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
    }

    public static void closeBtPann(BluetoothProfile bluetoothPan) {
        try {
            Method method = bluetoothPan.getClass().getDeclaredMethod("close");
            method.setAccessible(true);
            method.invoke(bluetoothPan);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error(e.toString());
        }
    }

    public static void enableBtTethering(BluetoothProfile bluetoothProfile, final boolean enable) {
        try {
            boolean nowVal = ((Boolean) bluetoothProfile.getClass().getMethod("isTetheringOn", new Class[0]).invoke(bluetoothProfile));
            if (nowVal != enable) {
                bluetoothProfile.getClass().getMethod("setBluetoothTethering", new Class[]{Boolean.TYPE}).invoke(bluetoothProfile, enable);
            }
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
    }

    public interface OnBluetoothPanConnected {

        void onBluetoothPanConnected(BluetoothProfile bluetoothPan);
        void onBluetoothPanDisconnected();
    }

    public static boolean connectTetheringDevice(BluetoothProfile bluetoothPan, BluetoothDevice bluetoothDevice) {
        try {
            return (boolean) bluetoothPan.getClass().getMethod("connect", BluetoothDevice.class).invoke(bluetoothPan, bluetoothDevice);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
        }
        return false;
    }

    public static int getTetheringDeviceConnectionState(BluetoothProfile bluetoothPan, BluetoothDevice bluetoothDevice) {
        try {
            return (int) bluetoothPan.getClass().getMethod("getConnectionState", BluetoothDevice.class).invoke(bluetoothPan, bluetoothDevice);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.toString());
            throw new RuntimeException(e);
        }
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
