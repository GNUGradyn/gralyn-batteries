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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BatteryReportWorker extends Worker {
    private Context context;

    public BatteryReportWorker(@NonNull Context _context, @NonNull WorkerParameters workerParams) {
        super(_context, workerParams);
        _context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.println(Log.INFO, "BatteryReportWorker", "Running battery report worker");
        Configuration config = ConfigurationHelper.LoadConfiguration(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        BatteryManager bm = (BatteryManager) getApplicationContext().getSystemService(BATTERY_SERVICE);
        if (!config.getBatteryReporting()) return Result.success();
        URL url = null;
        try {
            url = new URL(config.getApiRoot() + "/battery");
        } catch (MalformedURLException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to parse API url");
            e.printStackTrace();
            return Result.failure();
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("AccessCode", config.getAccessCode());
            jsonObject.put("Level", bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY));
            jsonObject.put("Charging", bm.isCharging());
        } catch (JSONException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to generate JSON object");
            return Result.failure();
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());
        Request request = new Request.Builder()
                .url("https://gralyn.app/api/battery")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
        } catch (IOException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to reach API. retrying");
            return Result.retry();
        }

        return Result.success();
    }
}
