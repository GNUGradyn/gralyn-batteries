package com.gradyn.gralynbatteries;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private Configuration config;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hotdog, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        config = ConfigurationHelper.LoadConfiguration(preferences);
        WorkManager workManager = WorkManager.getInstance(this);

        final Switch batteryReportingSwitch = (Switch) findViewById(R.id.BatteryReportingSwitch);
        final EditText gralynApiRootTextView = (EditText) findViewById(R.id.GralynApiRootTextbox);
        final EditText accessCodeRootTextView = (EditText) findViewById(R.id.AccessCodeTextbox);
        final Button savebutton = (Button) findViewById(R.id.SaveButton);

        PeriodicWorkRequest pwr = new PeriodicWorkRequest.Builder(BatteryReportWorker.class,
                15, TimeUnit.MINUTES).setConstraints(
                        new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build()).setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
                .build();
        workManager.enqueueUniquePeriodicWork("BatteryReportWorker", ExistingPeriodicWorkPolicy.REPLACE, pwr);

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

                workManager.enqueueUniquePeriodicWork("BatteryReportWorker", ExistingPeriodicWorkPolicy.REPLACE, pwr);

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logbutton:
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(UploadLogsWorker.class).setConstraints(
                                new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build())
                        .build();
                WorkManager.getInstance(this).enqueue(otwr);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}