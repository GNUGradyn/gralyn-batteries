package com.gradyn.gralynbatteries;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.BATTERY_SERVICE;

public class BatteryReportWorker extends Worker {
    public BatteryReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!MainActivity.inst.config.getBatteryReporting()) return Result.success();
        URL url = null;
        try {
            url = new URL(MainActivity.inst.config.getApiRoot() + "/battery");
        } catch (MalformedURLException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to parse API url");
            e.printStackTrace();
            return Result.failure();
        }
        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            JSONObject json = new JSONObject();
            json.put("AccessCode", MainActivity.inst.config.getAccessCode());
            BatteryManager bm = (BatteryManager) MainActivity.inst.getSystemService(BATTERY_SERVICE);
            json.put("Level", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            con.disconnect();
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
