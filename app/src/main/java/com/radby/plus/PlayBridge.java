package com.radby.plus;

import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes14.dex */
public final class PlayBridge {
    private static final String SERVICE = "com.radby.sounds.playback.PlaybackService";
    private static Object controller;

    private PlayBridge() {
    }

    public static void play(Context context, ChartsData.Track track) throws Exception {
        String strResolve = resolve(track.query);
        if (strResolve == null) {
            throw new IllegalStateException("nao achei o audio dessa musica");
        }
        Object objMediaItem = mediaItem(track, strResolve);
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(objMediaItem);
        Object objController = controller(context);
        Class<?> cls = Class.forName("androidx.media3.session.MediaController");
        cls.getMethod("setMediaItems", List.class, Integer.TYPE, Long.TYPE).invoke(objController, arrayList, 0, 0L);
        cls.getMethod("prepare", new Class[0]).invoke(objController, new Object[0]);
        cls.getMethod("play", new Class[0]).invoke(objController, new Object[0]);
    }

    private static String resolve(String str) throws Exception {
        Class<?> cls = Class.forName("com.yausername.youtubedl_android.YoutubeDLRequest");
        Class<?> cls2 = Class.forName("com.yausername.youtubedl_android.YoutubeDL");
        Object objInvoke = cls2.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
        Object objNewInstance = cls.getConstructor(String.class).newInstance("ytsearch1:" + str);
        cls.getMethod("addOption", String.class).invoke(objNewInstance, "-g");
        cls.getMethod("addOption", String.class).invoke(objNewInstance, "-f");
        cls.getMethod("addOption", String.class).invoke(objNewInstance, "bestaudio/best");
        cls.getMethod("addOption", String.class).invoke(objNewInstance, "--no-warnings");
        cls.getMethod("addOption", String.class).invoke(objNewInstance, "--no-playlist");
        try {
            Object objInvoke2 = cls2.getMethod("execute", cls, String.class).invoke(objInvoke, objNewInstance, "radby-discover");
            Object objInvoke3 = objInvoke2.getClass().getMethod("getOut", new Class[0]).invoke(objInvoke2, new Object[0]);
            if (objInvoke3 instanceof String) {
                for (String str2 : ((String) objInvoke3).split("\\r?\\n")) {
                    String strTrim = str2.trim();
                    if (strTrim.startsWith("http")) {
                        return strTrim;
                    }
                }
            }
            return null;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new IllegalStateException("yt-dlp falhou: " + cause.getMessage());
        }
    }

    private static Object mediaItem(ChartsData.Track track, String str) throws Exception {
        Class<?> cls = Class.forName("androidx.media3.common.MediaMetadata");
        Class<?> cls2 = Class.forName("androidx.media3.common.MediaMetadata$Builder");
        Object objNewInstance = cls2.getConstructor(new Class[0]).newInstance(new Object[0]);
        cls2.getMethod("setTitle", CharSequence.class).invoke(objNewInstance, track.title);
        cls2.getMethod("setArtist", CharSequence.class).invoke(objNewInstance, track.artist);
        if (track.art != null && track.art.startsWith("http")) {
            try {
                cls2.getMethod("setArtworkUri", Uri.class).invoke(objNewInstance, Uri.parse(track.art));
            } catch (Throwable th) {
            }
        }
        Object objInvoke = cls2.getMethod(JsonPOJOBuilder.DEFAULT_BUILD_METHOD, new Class[0]).invoke(objNewInstance, new Object[0]);
        Class<?> cls3 = Class.forName("androidx.media3.common.MediaItem$Builder");
        Object objNewInstance2 = cls3.getConstructor(new Class[0]).newInstance(new Object[0]);
        cls3.getMethod("setMediaId", String.class).invoke(objNewInstance2, "radby:" + track.query);
        cls3.getMethod("setUri", String.class).invoke(objNewInstance2, str);
        cls3.getMethod("setMediaMetadata", cls).invoke(objNewInstance2, objInvoke);
        return cls3.getMethod(JsonPOJOBuilder.DEFAULT_BUILD_METHOD, new Class[0]).invoke(objNewInstance2, new Object[0]);
    }

    private static Object controller(Context context) throws Exception {
        if (controller != null) {
            return controller;
        }
        Class<?> cls = Class.forName("androidx.media3.session.SessionToken");
        Class<?> cls2 = Class.forName("androidx.media3.session.MediaController$Builder");
        Object objInvoke = cls2.getMethod("buildAsync", new Class[0]).invoke(cls2.getConstructor(Context.class, cls).newInstance(context, cls.getConstructor(Context.class, ComponentName.class).newInstance(context, new ComponentName(context.getPackageName(), SERVICE))), new Object[0]);
        try {
            controller = objInvoke.getClass().getMethod("get", Long.TYPE, TimeUnit.class).invoke(objInvoke, 12L, TimeUnit.SECONDS);
            if (controller == null) {
                throw new IllegalStateException("servico de audio nao conectou");
            }
            return controller;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            throw new IllegalStateException("servico de audio indisponivel: " + cause.getMessage());
        }
    }
}
