package com.muzk.addon;

import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import androidx.media3.session.SessionToken;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes13.dex */
public final class MediaBridge {

    public interface Ready {
        void onFailed(String str);

        void onReady(MediaController mediaController);
    }

    public static void connect(Context context, final Player.Listener listener, final Ready ready) {
        final Handler handler = new Handler(Looper.getMainLooper());
        try {
            final Object objInvoke = MediaController.Builder.class.getMethod("buildAsync", null).invoke(new MediaController.Builder(context, new SessionToken(context, new ComponentName(context.getPackageName(), context.getPackageName() + ".playback.PlaybackService"))), null);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Object obj = null;
                    Throwable th = null;
                    try {
                        Future future = (Future) objInvoke;
                        long deadline = System.currentTimeMillis() + 6000;
                        while (System.currentTimeMillis() < deadline) {
                            if (future.isDone()) {
                                obj = future.get(1L, TimeUnit.MILLISECONDS);
                                th = null;
                                break;
                            }
                            try {
                                Thread.sleep(100L);
                            } catch (InterruptedException unused) {
                            }
                        }
                        if (obj == null && th == null) {
                            th = new Exception("tempo esgotado ao abrir a sessao de midia");
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                    final Object resultObj = obj;
                    final Throwable resultTh = th;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            String str;
                            Object obj3 = resultObj;
                            if (obj3 instanceof MediaController) {
                                MediaController mediaController = (MediaController) obj3;
                                try {
                                    if (listener != null) {
                                        mediaController.addListener(listener);
                                    }
                                } catch (Throwable unused2) {
                                }
                                ready.onReady(mediaController);
                                return;
                            }
                            Ready ready2 = ready;
                            if (resultTh != null) {
                                str = resultTh.getClass().getSimpleName() + ": " + resultTh.getMessage();
                            } else {
                                str = "sessao indisponivel";
                            }
                            ready2.onFailed(str);
                        }
                    });
                }
            }).start();
        } catch (Throwable th) {
            ready.onFailed(th.getClass().getSimpleName() + ": " + th.getMessage());
        }
    }

    public static void release(MediaController mediaController) {
        if (mediaController != null) {
            try {
                mediaController.release();
            } catch (Throwable unused) {
            }
        }
    }

    private MediaBridge() {
    }
}
