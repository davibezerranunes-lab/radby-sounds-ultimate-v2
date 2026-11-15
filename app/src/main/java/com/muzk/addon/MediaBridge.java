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
            new Thread(new Runnable() { // from class: com.muzk.addon.MediaBridge.1
                @Override // java.lang.Runnable
                public void run() {
                    final Object obj = null;
                    try {
                        Future future = (Future) objInvoke;
                        long jCurrentTimeMillis = System.currentTimeMillis() + 6000;
                        while (true) {
                            if (System.currentTimeMillis() < jCurrentTimeMillis) {
                                if (future.isDone()) {
                                    try {
                                        Object obj2 = future.get(1L, TimeUnit.MILLISECONDS);
                                        th = null;
                                        obj = obj2;
                                        break;
                                    } catch (Throwable th) {
                                        th = th;
                                    }
                                } else {
                                    try {
                                        Thread.sleep(100L);
                                    } catch (InterruptedException unused) {
                                    }
                                }
                            }
                            th = null;
                            break;
                        }
                        if (obj == null && th == null) {
                            th = new Exception("tempo esgotado ao abrir a sessão de mídia");
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                    handler.post(new Runnable() { // from class: com.muzk.addon.MediaBridge.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            String str;
                            Object obj3 = obj;
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
                            if (th != null) {
                                str = th.getClass().getSimpleName() + ": " + th.getMessage();
                            } else {
                                str = "sessão indisponível";
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
