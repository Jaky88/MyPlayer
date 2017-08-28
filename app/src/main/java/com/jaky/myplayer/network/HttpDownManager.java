package com.jaky.myplayer.network;


import org.apache.http.protocol.HttpService;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jaky on 2017/8/28 0028.
 */

public class HttpDownManager {


    private final HashSet<Object> downInfos;

    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }


    /*回调sub队列*/
    private HashMap<String,ProgressDownSubscriber> subMap;
    /*单利对象*/
    private volatile static HttpDownManager INSTANCE;

    private HttpDownManager(){
        downInfos=new HashSet<>();
        subMap=new HashMap<>();
    }

    /**
     * 开始下载
     */
    public void startDown(DownInfo info){
        if(info==null||subMap.get(info.getUrl())!=null){
            return;
        }
        ProgressDownSubscriber subscriber=new ProgressDownSubscriber(info);
        subMap.put(info.getUrl(),subscriber);
        HttpService httpService;
        if(downInfos.contains(info)){
            httpService=info.getService();
        }else{
            DownloadInterceptor interceptor = new DownloadInterceptor(subscriber);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(info.getConnectionTime(), TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(info.getBaseUrl())
                    .build();
            httpService= retrofit.create(HttpService.class);
            info.setService(httpService);
        }

        httpService.download("bytes=" + info.getReadLength() + "-",info.getUrl())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .retryWhen(new RetryWhenNetworkException())
                .map(new Func1<ResponseBody, DownInfo>() {
                    @Override
                    public DownInfo call(ResponseBody responseBody) {
                        try {
                            writeCache(responseBody,new File(info.getSavePath()),info);
                        } catch (IOException e) {
                            /*失败抛出异常*/
                            throw new HttpTimeException(e.getMessage());
                        }
                        return info;
                    }
                })
                /*回调线程*/
                .observeOn(AndroidSchedulers.mainThread())
                /*数据回调*/
                .subscribe(subscriber);

    }

    /**
     * 写入文件
     * @param file
     * @param info
     * @throws IOException
     */
    public void writeCache(ResponseBody responseBody,File file,DownInfo info) throws IOException{
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        long allLength;
        if (info.getCountLength()==0){
            allLength=responseBody.contentLength();
        }else{
            allLength=info.getCountLength();
        }
        FileChannel channelOut = null;
        RandomAccessFile randomAccessFile = null;
        randomAccessFile = new RandomAccessFile(file, "rwd");
        channelOut = randomAccessFile.getChannel();
        MappedByteBuffer mappedBuffer = channelOut.map(FileChannel.MapMode.READ_WRITE,
                info.getReadLength(),allLength-info.getReadLength());
        byte[] buffer = new byte[1024*8];
        int len;
        int record = 0;
        while ((len = responseBody.byteStream().read(buffer)) != -1) {
            mappedBuffer.put(buffer, 0, len);
            record += len;
        }
        responseBody.byteStream().close();
        if (channelOut != null) {
            channelOut.close();
        }
        if (randomAccessFile != null) {
            randomAccessFile.close();
        }
    }

    /**
     * 停止下载
     */
    public void stopDown(DownInfo info){
        if(info==null)return;
        info.setState(DownState.STOP);
        info.getListener().onStop();
        if(subMap.containsKey(info.getUrl())) {
            ProgressDownSubscriber subscriber=subMap.get(info.getUrl());
            subscriber.unsubscribe();
            subMap.remove(info.getUrl());
        }
        /*同步数据库*/
    }

    /**
     * 暂停下载
     * @param info
     */
    public void pause(DownInfo info){
        if(info==null)return;
        info.setState(DownState.PAUSE);
        info.getListener().onPuase();
        if(subMap.containsKey(info.getUrl())){
            ProgressDownSubscriber subscriber=subMap.get(info.getUrl());
            subscriber.unsubscribe();
            subMap.remove(info.getUrl());
        }
        /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
    }

    /**
     * 停止全部下载
     */
    public void stopAllDown(){
        for (DownInfo downInfo : downInfos) {
            stopDown(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }

    /**
     * 暂停全部下载
     */
    public void pauseAll(){
        for (DownInfo downInfo : downInfos) {
            pause(downInfo);
        }
        subMap.clear();
        downInfos.clear();
    }
}
