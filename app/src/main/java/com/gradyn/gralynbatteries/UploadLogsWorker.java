package com.gradyn.gralynbatteries;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.os.BatteryManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.gradyn.gralynbatteries.Configuration.Configuration;
import com.gradyn.gralynbatteries.Configuration.ConfigurationHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadLogsWorker extends Worker {
    public UploadLogsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
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
            url = new URL(config.getApiRoot() + "/log");
        } catch (MalformedURLException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to parse API url");
            e.printStackTrace();
            return Result.failure();
        }
        Process logcat;
        final StringBuilder log = new StringBuilder();
        try {
            logcat = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            BufferedReader br = new BufferedReader(new InputStreamReader(logcat.getInputStream()),4*1024);
            String line;
            String separator = System.getProperty("line.separator");
            while ((line = br.readLine()) != null) {
                log.append(line);
                log.append(separator);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("AccessCode",config.getAccessCode())
                .addFormDataPart("Log","log.txt",
                        RequestBody.create(MediaType.parse("application/octet-stream"), log.toString()))
                .build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url(url)
                .method("POST", body)
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
