package com.jaky.myplayer.rxdownload;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jaky.myplayer.R;
import com.jaky.myplayer.rxdownload.entity.DownloadInfo;
import com.jaky.myplayer.rxdownload.interf.DownloadProgressListener;
import com.jaky.myplayer.rxdownload.util.DownloadUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import rx.Subscriber;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyPlayer
 * @Author: Jack
 * @Date: 2017/8/29 0029,0:40
 * @Version: V1.0
 * @Description: TODO
 */

public class DownloadService extends IntentService {

    private static final String TAG = "DownloadService";
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String fileUrl = intent.getStringExtra("url");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("DownloadInfo")
                .setContentText("Downloading File")
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
        doDownload(fileUrl);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

    //=========================private=============================

    private void doDownload(String fileUrl) {
        try {
            OutputStream out = new FileOutputStream(new File(
                    Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS), "file.apk"));
            Downloader.getInstance(DownloadUtil.getHost(fileUrl), getDownloadProgressListener())
                    .download(fileUrl, out, new Subscriber() {
                        @Override
                        public void onNext(Object o) {

                        }

                        @Override
                        public void onCompleted() {
                            downloadCompleted();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            downloadCompleted();
                            Log.e(TAG, "onError: " + e.getMessage());
                        }


                    });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private DownloadProgressListener getDownloadProgressListener() {
        return new DownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                DownloadInfo download = new DownloadInfo();
                download.setTotalFileSize(contentLength);
                download.setCurrentFileSize(bytesRead);
                int progress = (int) ((bytesRead * 100) / contentLength);
                download.setProgress(progress);
                sendNotification(download);
            }
        };
    }

    private void downloadCompleted() {
        DownloadInfo download = new DownloadInfo();
        download.setProgress(100);
        sendIntent(download);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendNotification(DownloadInfo download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                DownloadUtil.getDataSize(download.getCurrentFileSize()) + "/" +
                        DownloadUtil.getDataSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(DownloadInfo download) {

        Intent intent = new Intent("MESSAGE_PROGRESS");
        intent.putExtra("doDownload", download);
        LocalBroadcastManager.getInstance(DownloadService.this).sendBroadcast(intent);
    }
}
