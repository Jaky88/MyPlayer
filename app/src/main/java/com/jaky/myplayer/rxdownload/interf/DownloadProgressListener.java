package com.jaky.myplayer.rxdownload.interf;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyPlayer
 * @Author: Jack
 * @Date: 2017/8/28 0028,23:40
 * @Version: V1.0
 * @Description: TODO
 */

public interface  DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
