package com.example.tethering.tethering;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class BtTetheringFragment extends Fragment {

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
                BtUtils.enableBtTethering(getActivity(), true, new BtUtils.OnBluetoothPanConnected() {
                    @Override
                    public void onBluetoothPanConnected(BluetoothProfile bluetoothPan) {
                        BluetoothDevice bluetoothDevice = (BluetoothDevice) BtUtils.getBoundedDevices().toArray()[spinner.getSelectedItemPosition()];
                        if (BtUtils.isConnectedTetheringDevice(bluetoothPan, bluetoothDevice)) {
                            boolean result = BtUtils.disconnectTetheringDevice(bluetoothPan, bluetoothDevice);
                            updateEnableState(!result);
                        } else {
                            boolean result = BtUtils.connectTetheringDevice(bluetoothPan, bluetoothDevice);
                            updateEnableState(result);
                        }
                    }
                });
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
