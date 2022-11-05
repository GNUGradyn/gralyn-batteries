package com.gradyn.gralynbatteries;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkRequest;

public class MainActivity extends AppCompatActivity {
    public static MainActivity inst;
    public SharedPreferences preferences;
    public Configuration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inst = this;
        preferences = getPreferences(Context.MODE_PRIVATE);
        config = ConfigurationHelper.LoadConfiguration(preferences);

        final Switch batteryReportingSwitch = (Switch) findViewById(R.id.BatteryReportingSwitch);
        final EditText gralynApiRootTextView = (EditText) findViewById(R.id.GralynApiRootTextbox);
        final EditText accessCodeRootTextView = (EditText) findViewById(R.id.AccessCodeTextbox);
        final Button savebutton = (Button) findViewById(R.id.SaveButton);

        WorkRequest reporter =
                new PeriodicWorkRequest.Builder(BatteryReportWorker.class,
                        15, TimeUnit.MINUTES).setConstraints(
                                new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build())
                        .build();

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