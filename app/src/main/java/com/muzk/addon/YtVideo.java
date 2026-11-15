package com.muzk.addon;

import android.content.Context;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes13.dex */
public final class YtVideo {

    public static final class Result {
        public String error;
        public String url;
    }

    public static Result resolve(Context context, String str) {
        String strLastLine;
        Result result = new Result();
        if (str == null || str.length() == 0) {
            result.error = "faixa sem URL de origem";
            return result;
        }
        try {
            Class<?> cls = Class.forName("com.yausername.youtubedl_android.YoutubeDL");
            Class<?> cls2 = Class.forName("com.yausername.youtubedl_android.YoutubeDLRequest");
            Object objInvoke = cls.getMethod("getInstance", null).invoke(null, null);
            Object objNewInstance = cls2.getConstructor(String.class).newInstance(str);
            cls2.getMethod("addOption", String.class, String.class).invoke(objNewInstance, "-f", "best[height<=720][ext=mp4]/best[height<=720]/best[ext=mp4]/best");
            cls2.getMethod("addOption", String.class, String.class).invoke(objNewInstance, "--extractor-args", "youtube:player_client=android,web");
            cls2.getMethod("addOption", String.class).invoke(objNewInstance, "--get-url");
            cls2.getMethod("addOption", String.class).invoke(objNewInstance, "--no-playlist");
            cls2.getMethod("addOption", String.class).invoke(objNewInstance, "--no-warnings");
            Object objInvoke2 = cls.getMethod("execute", cls2).invoke(objInvoke, objNewInstance);
            String str2 = (String) objInvoke2.getClass().getMethod("getOut", null).invoke(objInvoke2, null);
            String str3 = (String) objInvoke2.getClass().getMethod("getErr", null).invoke(objInvoke2, null);
            String strFirstUrl = firstUrl(str2);
            if (strFirstUrl != null) {
                result.url = strFirstUrl;
            } else {
                if (str3 != null && str3.trim().length() > 0) {
                    strLastLine = lastLine(str3);
                } else {
                    strLastLine = "yt-dlp não devolveu URL de vídeo";
                }
                result.error = strLastLine;
            }
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            InvocationTargetException cause2 = e;
            if (cause != null) {
                cause2 = e.getCause();
            }
            result.error = cause2.getClass().getSimpleName() + ": " + cause2.getMessage();
        } catch (Throwable th) {
            result.error = th.getClass().getSimpleName() + ": " + th.getMessage();
        }
        return result;
    }

    private static String firstUrl(String str) {
        if (str == null) {
            return null;
        }
        for (String str2 : str.split("\\r?\\n")) {
            String strTrim = str2.trim();
            if (strTrim.startsWith("http://") || strTrim.startsWith("https://")) {
                return strTrim;
            }
        }
        return null;
    }

    private static String lastLine(String str) {
        String[] strArrSplit = str.trim().split("\\r?\\n");
        if (strArrSplit.length > 0) {
            str = strArrSplit[strArrSplit.length - 1];
        }
        String strTrim = str.trim();
        if (strTrim.length() <= 180) {
            return strTrim;
        }
        return strTrim.substring(0, 180) + "...";
    }

    private YtVideo() {
    }
}
