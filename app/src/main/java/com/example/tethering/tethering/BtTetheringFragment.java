package com.example.tethering.tethering;

import android.app.Fragment;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.example.tethering.tethering.utils.BtUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtTetheringFragment extends Fragment {

    private static Logger LOG = LoggerFactory.getLogger(BtTetheringFragment.class);
    private BluetoothProfile bluetoothPan;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bt_tethering, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        openBtPanConnection();
    }

    private void openBtPanConnection() {
        BtUtils.openBtPan(getActivity(), new BtUtils.OnBluetoothPanConnected() {
            @Override
            public void onBluetoothPanConnected(BluetoothProfile bluetoothPan) {
                synchronized (BtTetheringFragment.this) {
                    BtTetheringFragment.this.bluetoothPan = bluetoothPan;
                    BtUtils.enableBtTethering(bluetoothPan, true);
                }
            }

            @Override
            public void onBluetoothPanDisconnected() {
                synchronized (BtTetheringFragment.this) {
                    BtTetheringFragment.this.bluetoothPan = null;
                }
            }
        });
    }

    private synchronized void closeBtPanConnection() {
        if (bluetoothPan != null) {
            BtUtils.closeBtPann(bluetoothPan);
        }
    }

    private void reopenBtPanConnection() {
        closeBtPanConnection();
        openBtPanConnection();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI() {
        final AppCompatSpinner spinner = (AppCompatSpinner) getView().findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getDevicesNames(BtUtils.getBoundedDevices()));
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        getView().findViewById(R.id.process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronized (BtTetheringFragment.this) {
                    if (bluetoothPan == null) return;
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) BtUtils.getBoundedDevices().toArray()[spinner.getSelectedItemPosition()];
                    LOG.debug("current tethering state :" + BtUtils.getTetheringDeviceConnectionState(bluetoothPan, bluetoothDevice));
                    if (BtUtils.isConnectedTetheringDevice(bluetoothPan, bluetoothDevice)) {
                        boolean result = BtUtils.disconnectTetheringDevice(bluetoothPan, bluetoothDevice);
                        reopenBtPanConnection();
                        updateEnableState(!result);
                    } else {
                        boolean result = BtUtils.connectTetheringDevice(bluetoothPan, bluetoothDevice);
                        updateEnableState(result);
                    }
                }
            }
        });
        updateEnableState(false);
    }

    private void updateEnableState(boolean isDeviceTetheringEnabled) {
        if (getView() != null) {
            ((Button) getView().findViewById(R.id.process)).setText(isDeviceTetheringEnabled ? R.string.disable : R.string.enable);
        }
    }

    private static String[] getDevicesNames(Set<BluetoothDevice> bluetoothDevices) {
        List<String> names = new ArrayList<>();
        for (BluetoothDevice device : bluetoothDevices) {
            names.add(device.getName());
        }
        return names.toArray(new String[names.size()]);
    }
}
