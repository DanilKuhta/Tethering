package com.example.tethering.tethering;

import android.app.Fragment;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.example.tethering.tethering.utils.WifiUtils;

public class WifiTetheringFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_wifi_tethering, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!WifiUtils.isWriteSettingsAllowed(getActivity())) {
            startActivity(new Intent(getActivity(), EnableWriteSettingsActivity.class));
        }
    }

    private void initUI() {
        AppCompatSpinner spinner = (AppCompatSpinner) getView().findViewById(R.id.spinner);
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, WifiConfigurator.Security.names());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean needPassword = i == WifiConfigurator.Security.WPA_PSK.ordinal();
                getView().findViewById(R.id.password_layout).setVisibility(needPassword ? View.VISIBLE : View.GONE);
                updateWifiConfigurationUI(WifiUtils.isWifiTetheringEnabled(getActivity()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateWifiConfigurationUI(WifiUtils.isWifiTetheringEnabled(getActivity()));
            }
        };

        ((EditText) getView().findViewById(R.id.name_edit_text)).addTextChangedListener(textWatcher);
        ((EditText) getView().findViewById(R.id.password_edit_text)).addTextChangedListener(textWatcher);

        getView().findViewById(R.id.process).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                process();
            }
        });
        updateWifiConfigurationUI(WifiUtils.isWifiTetheringEnabled(getActivity()));
    }

    private void updateWifiConfigurationUI(boolean isWifiTetheringEnabled) {
        if (getView() != null) {
            ((Button)getView().findViewById(R.id.process)).setText(isWifiTetheringEnabled ? getString(R.string.disable) : getString(R.string.enable));
            getView().findViewById(R.id.process).setEnabled(isWifiTetheringEnabled || WifiUtils.isWifiConfigurationReady(buildWifiConfiguration()));
        }
    }

    private WifiConfiguration buildWifiConfiguration() {
        AppCompatSpinner spinner = (AppCompatSpinner) getView().findViewById(R.id.spinner);
        EditText passwordEditText = (EditText) getView().findViewById(R.id.password_edit_text);
        EditText nameEditText = (EditText) getView().findViewById(R.id.name_edit_text);

        return WifiUtils.createWifiConfiguration(
                nameEditText.getText().toString(),
                WifiConfigurator.Security.values()[spinner.getSelectedItemPosition()].getKeyMgmt(),
                passwordEditText.getText().toString(),
                false);
    }

    private void process() {
        if (WifiUtils.isWifiTetheringEnabled(getActivity())) {
            boolean result = WifiUtils.setWifiTetheringEnabled(getActivity(), false, null);
            updateWifiConfigurationUI(!result);
        } else {
            boolean result = WifiUtils.setWifiTetheringEnabled(getActivity(), true, buildWifiConfiguration());
            updateWifiConfigurationUI(result);
        }
    }
}
