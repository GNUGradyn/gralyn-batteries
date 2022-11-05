package com.gradyn.gralynbatteries;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        final Configuration config = ConfigurationHelper.LoadConfiguration(preferences);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Switch batteryReportingSwitch = (Switch) findViewById(R.id.BatteryReportingSwitch);
        final EditText gralynApiRootTextView = (EditText) findViewById(R.id.GralynApiRootTextbox);
        final EditText accessCodeRootTextView = (EditText) findViewById(R.id.AccessCodeTextbox);
        final Button savebutton = (Button) findViewById(R.id.SaveButton);

        batteryReportingSwitch.setChecked(config.getBatteryReporting());
        gralynApiRootTextView.setText(config.getApiRoot());
        accessCodeRootTextView.setText(config.getAccessCode());
        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                config.setAccessCode(accessCodeRootTextView.getText().toString());
                config.setBatteryReporting(batteryReportingSwitch.isChecked());
                config.setApiRoot(gralynApiRootTextView.getText().toString());
                ConfigurationHelper.SaveConfiguration(config, preferences);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Changes saved")
                        .setMessage("Changes were saved successsfully").setPositiveButton("Very good", null)
                        .show();
            }
        });
    }
}