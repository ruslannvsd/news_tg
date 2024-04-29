package com.example.newstg.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import java.util.Calendar;

import com.example.newstg.network.NtcWorker;

import java.util.concurrent.TimeUnit;

public class Scheduler {
    public static final String HOURS_24 = "TWENTY_FOUR";
    public static final String HOURS_4 = "FOUR";
    Context ctx;
    public void setupWorkSchedules(Context ctx, int first, int second) {
        this.ctx = ctx;
        scheduleWork24(first);
        scheduleWorkFour(second);
    }

    private void scheduleWork24(int hours) {
        cancelPeriodicWork(HOURS_24);
        int targetHour = 22;
        long initialDelay = calculateInitialDelay(targetHour);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putInt("interval", hours)
                .build();

        PeriodicWorkRequest newRequest = new PeriodicWorkRequest.Builder(NtcWorker.class, hours, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInitialDelay(initialDelay, TimeUnit.HOURS)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(HOURS_24, ExistingPeriodicWorkPolicy.KEEP, newRequest);
        Toast.makeText(ctx, "At 10.00pm", Toast.LENGTH_SHORT).show();
    }

    private void scheduleWorkFour(int hours) {
        cancelPeriodicWork(HOURS_4);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data inputData = new Data.Builder()
                .putInt("interval", hours)
                .build();

        PeriodicWorkRequest newRequest = new PeriodicWorkRequest.Builder(NtcWorker.class, hours, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(HOURS_4, ExistingPeriodicWorkPolicy.KEEP, newRequest);
    }

    private long calculateInitialDelay(int targetHour) {
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);

        int delay = targetHour - currentHour;
        if (delay < 0) {
            delay += 24;
        }
        return delay;
    }

    public void cancelPeriodicWork(String workName) {
        WorkManager.getInstance(ctx).cancelUniqueWork(workName);
    }
}
