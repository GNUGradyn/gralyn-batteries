package com.gradyn.gralynbatteries;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Switch;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        Configuration config = ConfigurationHelper.LoadConfiguration(preferences);
        Switch batteryReportingSwitch = (Switch) findViewById(R.id.BatteryReportingSwitch);
        EditText gralynApiRootTextView = (EditText) findViewById(R.id.GralynApiRootTextbox);
        EditText accessCode = (EditText) findViewById(R.id.AccessCodeTextbox);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        batteryReportingSwitch.setChecked(config.getBatteryReporting());
        gralynApiRootTextView.setText(config.getApiRoot());
        accessCode.setText(config.getAccessCode());
    }
}