package com.jaky.myplayer.rxdownload;

import android.support.annotation.NonNull;

import com.jaky.myplayer.rxdownload.entity.DownloadProgressInterceptor;
import com.jaky.myplayer.rxdownload.interf.DownloadProgressListener;
import com.jaky.myplayer.rxdownload.interf.IDownloadService;
import com.jaky.myplayer.rxdownload.util.DownloadUtil;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyPlayer
 * @Author: Jack
 * @Date: 2017/8/28 0028,23:45
 * @Version: V1.0
 * @Description: TODO
 */

public class Downloader {
    private static final String TAG = "Downloader";
    private static final int DEFAULT_TIMEOUT = 15;
    private static Downloader sInstance;
    private Retrofit retrofit;

    public static Downloader getInstance(String baseUrl, DownloadProgressListener listener) {
        if (sInstance == null) {
            sInstance = new Downloader(baseUrl, listener);
        }
        return sInstance;
    }

    public void download(String url, final OutputStream out, Subscriber subscriber) {
        retrofit.create(IDownloadService.class)
                .download(url)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Func1<ResponseBody, InputStream>() {
                    @Override
                    public InputStream call(ResponseBody responseBody) {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Action1<InputStream>() {
                    @Override
                    public void call(InputStream inputStream) {
                        DownloadUtil.saveFile(inputStream, out);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    //================================private==================================

    private Downloader(String url, DownloadProgressListener listener) {
        retrofit = getRetrofit(url, listener);
    }

    @NonNull
    private Retrofit getRetrofit(String url, DownloadProgressListener listener) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(getOkHttpClient(new DownloadProgressInterceptor(listener)))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @NonNull
    private OkHttpClient getOkHttpClient(DownloadProgressInterceptor interceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }
}
