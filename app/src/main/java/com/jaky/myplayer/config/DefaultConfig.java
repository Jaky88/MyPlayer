package com.jaky.myplayer.config;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jaky.myplayer.R;
import com.jaky.myplayer.entity.DefaultConfigBean;
import com.jaky.myplayer.util.FileUtils;
import com.jaky.myplayer.util.LogUtil;
import com.jaky.myplayer.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 12345 on 2017/5/10.
 */

public class DefaultConfig {

    private static DefaultConfigBean config;

    public static DefaultConfigBean getConfig(Context context) {
        if (config == null) {
            LogUtil.o("read config!");
            config = readConfig(context, R.raw.config);
        }
        return config;
    }


    public static boolean saveConfig(String path,Object obj) {
        String json = JSONObject.toJSONString(obj);
        FileOutputStream fos = null;
        try {
            File configFile = new File(path);
            if (!configFile.exists()) {
                if (!configFile.getParentFile().exists()) {
                    configFile.getParentFile().mkdirs();
                }
                configFile.createNewFile();
            }

            fos = new FileOutputStream(path);
            fos.write(json.getBytes());
            fos.flush();
            fos.close();
            return configFile.exists() && configFile.length() > 0;
        } catch (Exception e) {
            LogUtil.o("saveConfig: ERROR! " + e.toString());
            return false;
        } finally {
            FileUtils.closeQuietly(fos);
        }
    }

    private static DefaultConfigBean readConfig(String path) {
        String content = readContent(path);
        if (StringUtils.isNullOrEmpty(content)) {
            LogUtil.o("readConfig: content is null, use default config!");
            return new DefaultConfigBean();
        }
        return JSON.parseObject(content, DefaultConfigBean.class);
    }

    public static String readContent(String path) {
        BufferedReader breader = null;
        FileInputStream fis = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                LogUtil.o("readContent: " + file.getAbsolutePath() + " is not exist!");
                return null;
            }
            fis = new FileInputStream(path);
            breader = new BufferedReader(new InputStreamReader(fis));
            StringBuffer total = new StringBuffer();
            String line = null;
            while ((line = breader.readLine()) != null) {
                total.append(line);
            }
            LogUtil.o("readContent: " + total.toString());
            return total.toString();
        } catch (Exception e) {
            LogUtil.o("readContent: " + e.toString());
        } finally {
            FileUtils.closeQuietly(breader);
            FileUtils.closeQuietly(fis);
        }
        return null;
    }


    private static DefaultConfigBean readConfig(Context context, int rawResourceId) {
        String content = readContent(context, rawResourceId);
        if (StringUtils.isNullOrEmpty(content)) {
            LogUtil.o("readConfig: content is null, use default config!");
            return new DefaultConfigBean();
        }
        return JSON.parseObject(content, DefaultConfigBean.class);
    }

    public static String readContent(Context context, int rawResourceId) {
        BufferedReader breader = null;
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(rawResourceId);
            breader = new BufferedReader(new InputStreamReader(is));
            StringBuilder total = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        }
        catch (Exception e) {
            LogUtil.o(e.getMessage());
        }
        finally {
            FileUtils.closeQuietly(breader);
            FileUtils.closeQuietly(is);
        }
        return null;
    }
}
