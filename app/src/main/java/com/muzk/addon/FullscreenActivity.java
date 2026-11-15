package com.muzk.addon;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.ViewCompat;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.MediaController;

/* JADX INFO: loaded from: classes13.dex */
public class FullscreenActivity extends Activity {
    private MediaController controller;
    private int generation;
    private TextView info;
    private SurfaceView surface;
    private TextView titleView;
    private ExoPlayer video;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private String lastKey = "";
    private final Runnable tick = new Runnable() { // from class: com.muzk.addon.FullscreenActivity.1
        @Override // java.lang.Runnable
        public void run() {
            FullscreenActivity.this.sync();
            FullscreenActivity.this.handler.postDelayed(this, 400L);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().addFlags(128);
        setContentView(build());
        hideBars();
        VideoFinder.ensurePermission(this);
        ExoPlayer exoPlayerBuild = new ExoPlayer.Builder(this).build();
        this.video = exoPlayerBuild;
        exoPlayerBuild.setVolume(0.0f);
        MediaBridge.connect(this, new Player.Listener() { // from class: com.muzk.addon.FullscreenActivity.2
            @Override // androidx.media3.common.Player.Listener
            public void onEvents(Player player, Player.Events events) {
                FullscreenActivity.this.attachIfChanged();
            }
        }, new MediaBridge.Ready() { // from class: com.muzk.addon.FullscreenActivity.3
            @Override // com.muzk.addon.MediaBridge.Ready
            public void onReady(MediaController mediaController) {
                FullscreenActivity.this.controller = mediaController;
                FullscreenActivity.this.lastKey = "";
                FullscreenActivity.this.attachIfChanged();
            }

            @Override // com.muzk.addon.MediaBridge.Ready
            public void onFailed(String str) {
                FullscreenActivity.this.info.setText("Sem sessão de reprodução.\n" + str);
            }
        });
        attachIfChanged();
        this.handler.post(this.tick);
    }

    private View build() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        SurfaceView surfaceView = new SurfaceView(this);
        this.surface = surfaceView;
        frameLayout.addView(surfaceView, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(Ui.dp(this, 8.0f), Ui.dp(this, 12.0f), Ui.dp(this, 12.0f), Ui.dp(this, 8.0f));
        Button button = Ui.button(this, "X");
        button.setTextColor(-1);
        button.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.FullscreenActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                FullscreenActivity.this.finish();
            }
        });
        linearLayout.addView(button, new LinearLayout.LayoutParams(Ui.dp(this, 52.0f), Ui.dp(this, 40.0f)));
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(1);
        TextView textViewText = Ui.text(this, "", 16.0f, true);
        this.titleView = textViewText;
        textViewText.setTextColor(-1);
        this.titleView.setSingleLine(true);
        this.titleView.setEllipsize(TextUtils.TruncateAt.END);
        TextView textViewText2 = Ui.text(this, "", 12.0f, false);
        this.info = textViewText2;
        textViewText2.setTextColor(-5197648);
        linearLayout2.addView(this.titleView);
        linearLayout2.addView(this.info);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -2, 48));
        return frameLayout;
    }

    private void hideBars() {
        getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    private String key() {
        MediaController mediaController = this.controller;
        MediaItem currentMediaItem = mediaController == null ? null : mediaController.getCurrentMediaItem();
        if (currentMediaItem == null || currentMediaItem.mediaMetadata == null) {
            return "";
        }
        String string = currentMediaItem.mediaMetadata.title == null ? "" : currentMediaItem.mediaMetadata.title.toString();
        return (currentMediaItem.mediaMetadata.artist != null ? currentMediaItem.mediaMetadata.artist.toString() : "") + "|" + string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attachIfChanged() {
        if (this.controller == null) {
            return;
        }
        String strKey = key();
        MediaItem currentMediaItem = this.controller.getCurrentMediaItem();
        if (currentMediaItem == null || currentMediaItem.mediaMetadata == null) {
            this.info.setText("Aguardando uma faixa tocar...");
            this.lastKey = "";
            return;
        }
        if (strKey.equals(this.lastKey)) {
            return;
        }
        this.lastKey = strKey;
        final int i = this.generation + 1;
        this.generation = i;
        String string = currentMediaItem.mediaMetadata.title == null ? "" : currentMediaItem.mediaMetadata.title.toString();
        String string2 = currentMediaItem.mediaMetadata.artist != null ? currentMediaItem.mediaMetadata.artist.toString() : "";
        this.titleView.setText(string);
        Uri uriFind = VideoFinder.find(this, string, string2);
        if (uriFind != null) {
            this.info.setText("Vídeo local sincronizado (mudo)");
            play(uriFind);
            return;
        }
        final String str = currentMediaItem.mediaId;
        if (str == null || (!str.startsWith("http://") && !str.startsWith("https://"))) {
            this.info.setText("Vídeo local não encontrado.");
            stopVideo();
        } else {
            this.info.setText("Buscando vídeo no YouTube...");
            new Thread(new Runnable() { // from class: com.muzk.addon.FullscreenActivity.5
                @Override // java.lang.Runnable
                public void run() {
                    final YtVideo.Result resultResolve = YtVideo.resolve(FullscreenActivity.this, str);
                    FullscreenActivity.this.runOnUiThread(new Runnable() { // from class: com.muzk.addon.FullscreenActivity.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            if (i != FullscreenActivity.this.generation || FullscreenActivity.this.isFinishing()) {
                                return;
                            }
                            if (resultResolve.url != null) {
                                FullscreenActivity.this.info.setText("Vídeo do YouTube sincronizado (mudo)");
                                FullscreenActivity.this.play(Uri.parse(resultResolve.url));
                                return;
                            }
                            FullscreenActivity.this.info.setText("Vídeo indisponível: " + resultResolve.error);
                            FullscreenActivity.this.stopVideo();
                        }
                    });
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play(Uri uri) {
        ExoPlayer exoPlayer = this.video;
        if (exoPlayer == null) {
            return;
        }
        exoPlayer.setVideoSurfaceView(this.surface);
        this.video.setMediaItem(MediaItem.fromUri(uri));
        this.video.setRepeatMode(1);
        this.video.prepare();
        this.video.play();
        sync();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopVideo() {
        ExoPlayer exoPlayer = this.video;
        if (exoPlayer != null) {
            try {
                exoPlayer.stop();
                this.video.clearMediaItems();
            } catch (Throwable unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sync() {
        ExoPlayer exoPlayer;
        if (this.controller == null || (exoPlayer = this.video) == null || exoPlayer.getMediaItemCount() == 0) {
            return;
        }
        if (this.controller.isPlaying()) {
            this.video.play();
        } else {
            this.video.pause();
        }
        long duration = this.video.getDuration();
        if (duration <= 0 || duration == C.TIME_UNSET) {
            return;
        }
        long jMax = Math.max(0L, this.controller.getCurrentPosition()) % duration;
        if (Math.abs(this.video.getCurrentPosition() - jMax) > 900) {
            this.video.seekTo(jMax);
        }
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        this.lastKey = "";
        attachIfChanged();
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.handler.removeCallbacks(this.tick);
        this.generation++;
        MediaBridge.release(this.controller);
        this.controller = null;
        ExoPlayer exoPlayer = this.video;
        if (exoPlayer != null) {
            exoPlayer.release();
            this.video = null;
        }
        super.onDestroy();
    }
}
