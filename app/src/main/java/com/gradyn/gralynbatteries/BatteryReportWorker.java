package com.gradyn.gralynbatteries;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.BATTERY_SERVICE;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

public class BatteryReportWorker extends Worker {
    private Context context;

    public BatteryReportWorker(@NonNull Context _context, @NonNull WorkerParameters workerParams) {
        super(_context, workerParams);
        _context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Configuration config = ConfigurationHelper.LoadConfiguration(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        Log.println(Log.INFO, "BatteryReportWorker", "Running battery report worker");
        if (!config.getBatteryReporting()) return Result.success();
        URL url = null;
        try {
            url = new URL(config.getApiRoot() + "/battery");
        } catch (MalformedURLException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to parse API url");
            e.printStackTrace();
            return Result.failure();
        }
        try {

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("AccessCode", config.getAccessCode());
            BatteryManager bm = (BatteryManager) getApplicationContext().getSystemService(BATTERY_SERVICE);
            json.put("Level", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
            json.put("Charging", bm.isCharging());
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println(response.toString());
            }
        } catch (IOException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to open connection to API, retrying");
            e.printStackTrace();
            return Result.retry();
        } catch (JSONException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Malformed JSON in battery report");
            e.printStackTrace();
            return Result.failure();
        }

        return Result.success();
    }
}
