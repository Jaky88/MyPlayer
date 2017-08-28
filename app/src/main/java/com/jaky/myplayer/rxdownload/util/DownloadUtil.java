package com.jaky.myplayer.rxdownload.util;

import com.jaky.myplayer.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyPlayer
 * @Author: Jack
 * @Date: 2017/8/29 0029,1:21
 * @Version: V1.0
 * @Description: TODO
 */

public class DownloadUtil {

    public static String getHost(String strUrl){
        try {
            URL url = new URL(strUrl);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveFile(InputStream in, OutputStream out) {
        try {
            try {
                byte[] buff = new byte[1024 * 1024];
                long total = in.available();
                long progress = 0;
                int read = 0;

                if((read = in.read(buff)) > 0){
                    out.write(buff, 0, read);
                    progress += read;
                }
                out.flush();
                LogUtil.d("file download: " + progress + " of " + total);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


    public static String getDataSize(long fileSize) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "K";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
