package com.gradyn.gralynbatteries;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class BatteryReportWorker extends Worker {
    public BatteryReportWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
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
        } catch (IOException e) {
            Log.println(Log.ERROR, "BatteryReportWorker", "Failed to open connection to API, retrying");
            e.printStackTrace();
            return Result.retry();
        }

        return Result.success();


    }
}
