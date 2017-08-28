package com.jaky.myplayer.rxdownload.interf;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyPlayer
 * @Author: Jack
 * @Date: 2017/8/28 0028,23:47
 * @Version: V1.0
 * @Description: TODO
 */

public interface IDownloadService {
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}
