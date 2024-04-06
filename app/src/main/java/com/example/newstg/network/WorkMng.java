package com.example.newstg.network;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.newstg.MainActivity;
import com.example.newstg.R;

public class WorkMng extends Worker {
    static String TG_NEWS_ID = "TG_NEWS_ID";
    static int TG_NTF_ID = 123456;
    Context ctx;
    public WorkMng(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @SuppressLint("MissingPermission")
    @NonNull
    @Override
    public Result doWork() {
        ctx = getApplicationContext();
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        String text = "Check News Now";
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        NotificationChannel channel = new NotificationChannel(TG_NEWS_ID, "Tg News", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Tg News Desc");
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, TG_NEWS_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("News TG : ")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(TG_NTF_ID, builder.build());
        return Result.success();
    }
}