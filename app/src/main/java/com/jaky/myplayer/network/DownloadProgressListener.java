package com.jaky.myplayer.network;

/**
 * Created by jaky on 2017/8/28 0028.
 */

public interface DownloadProgressListener {
    void update(long read, long count, boolean done);
}
