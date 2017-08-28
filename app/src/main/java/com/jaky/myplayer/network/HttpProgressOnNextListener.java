package com.jaky.myplayer.network;

/**
 * Created by jaky on 2017/8/28 0028.
 */

public abstract class HttpProgressOnNextListener<T> {

    public abstract void onNext(T t);

    public abstract void onStart();

    public abstract void onComplete();


    public abstract void updateProgress(long readLength, long countLength);

    public void onError(Throwable e) {

    }

    public void onPuase() {

    }

    public void onStop() {

    }
}
