package com.example.newstg.network;

import static com.example.newstg.consts.Cons.TG_NEWS_ID;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.newstg.MainActivity;
import com.example.newstg.R;
import com.example.newstg.data.NewsDB;
import com.example.newstg.obj.Article;
import com.example.newstg.obj.Chn;
import com.example.newstg.obj.Word;
import com.example.newstg.utils.Count;

import java.util.List;

public class NtcWorker extends Worker {
    static int TG_NTF_ID = 12345;
    private final Context ctx;
    private final NewsDB db;
    public NtcWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
        this.ctx = ctx;
        this.db = NewsDB.getDatabase(ctx);
    }

    @NonNull
    @Override
    public Result doWork() {
        int interval = getInputData().getInt("interval", 0);
        try {
            Log.i("WorkManager", "Work Started");
            List<Word> keywords = db.wordDao().offlineKw();
            List<Chn> channels = db.chnDao().offChannels();
            db.artDao().deleteAll();
            List<Article> articles = new GetArtOffline().getArt(keywords, channels, interval);
            if (!articles.isEmpty()) {
                db.artDao().insertAll(articles);
                List<Word> words = FetchUtils.sortingNum(new Count().results(keywords, articles), ctx);
                String summary = summary(words);
                notification(summary, interval);
                Log.i("WorkManager", "Notification Made");
            }
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @NonNull
    private String summary(@NonNull List<Word> summary) {
        StringBuilder summaryString = new StringBuilder();
        for (Word keyword : summary) {
            Log.i("WorkManager KW", keyword.getWord());
            summaryString.append(keyword.getWord())
                    .append(" - ")
                    .append(keyword.getNum())
                    .append("; ");
        }
        if (summaryString.length() > 0) {
            summaryString.setLength(summaryString.length() - 2);
        }

        return summaryString.toString();
    }

    @SuppressLint("MissingPermission")
    private void notification(String text, int interval) {
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        NotificationChannel channel = new NotificationChannel(TG_NEWS_ID, "Tg News", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Tg News Desc");
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, TG_NEWS_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("News for " + interval + " hours :")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(TG_NTF_ID, builder.build());
    }
}
