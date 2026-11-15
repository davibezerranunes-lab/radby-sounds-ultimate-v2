package com.muzk.addon;

import android.R;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.session.MediaController;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes13.dex */
public class LyricsActivity extends Activity {
    private MediaController controller;
    private TextView header;
    private LinearLayout lines;
    private ScrollView scroll;
    private TextView status;
    private boolean synced;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<Lrc.Line> parsed = new ArrayList();
    private String lastKey = "";
    private int active = -1;
    private int activeView = -1;
    private final Runnable tick = new Runnable() { // from class: com.muzk.addon.LyricsActivity.1
        @Override // java.lang.Runnable
        public void run() {
            LyricsActivity.this.update();
            LyricsActivity.this.handler.postDelayed(this, 250L);
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(build());
        MediaBridge.connect(this, new Player.Listener() { // from class: com.muzk.addon.LyricsActivity.2
            @Override // androidx.media3.common.Player.Listener
            public void onEvents(Player player, Player.Events events) {
                LyricsActivity.this.loadIfChanged();
            }
        }, new MediaBridge.Ready() { // from class: com.muzk.addon.LyricsActivity.3
            @Override // com.muzk.addon.MediaBridge.Ready
            public void onReady(MediaController mediaController) {
                LyricsActivity.this.controller = mediaController;
                LyricsActivity.this.status.setText("");
                LyricsActivity.this.loadIfChanged();
            }

            @Override // com.muzk.addon.MediaBridge.Ready
            public void onFailed(String str) {
                LyricsActivity.this.status.setText("Sem sessão de reprodução.\n" + str);
            }
        });
        this.handler.post(this.tick);
    }

    private View build() {
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setBackgroundColor(Ui.color(this, R.attr.colorBackground));
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setGravity(16);
        linearLayout2.setPadding(Ui.dp(this, 8.0f), Ui.dp(this, 10.0f), Ui.dp(this, 12.0f), Ui.dp(this, 6.0f));
        Button button = Ui.button(this, "<");
        button.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.LyricsActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                LyricsActivity.this.finish();
            }
        });
        linearLayout2.addView(button, new LinearLayout.LayoutParams(Ui.dp(this, 52.0f), Ui.dp(this, 44.0f)));
        TextView textViewText = Ui.text(this, "Letras", 18.0f, true);
        this.header = textViewText;
        textViewText.setSingleLine(true);
        this.header.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout2.addView(this.header, new LinearLayout.LayoutParams(0, -2, 1.0f));
        linearLayout.addView(linearLayout2);
        TextView textViewText2 = Ui.text(this, "Buscando letra...", 13.0f, false);
        this.status = textViewText2;
        textViewText2.setPadding(Ui.dp(this, 20.0f), 0, Ui.dp(this, 20.0f), Ui.dp(this, 8.0f));
        linearLayout.addView(this.status);
        this.scroll = new ScrollView(this);
        LinearLayout linearLayout3 = new LinearLayout(this);
        this.lines = linearLayout3;
        linearLayout3.setOrientation(1);
        this.lines.setPadding(Ui.dp(this, 20.0f), Ui.dp(this, 8.0f), Ui.dp(this, 20.0f), Ui.dp(this, 80.0f));
        this.scroll.addView(this.lines);
        linearLayout.addView(this.scroll, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        frameLayout.addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
        Ui.applyInsets(frameLayout);
        return frameLayout;
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
    public void loadIfChanged() {
        String strKey = key();
        MediaController mediaController = this.controller;
        MediaItem currentMediaItem = mediaController == null ? null : mediaController.getCurrentMediaItem();
        if (currentMediaItem == null || currentMediaItem.mediaMetadata == null) {
            this.status.setText("Aguardando uma faixa tocar...");
            this.lastKey = "";
            return;
        }
        if (strKey.equals(this.lastKey)) {
            return;
        }
        this.lastKey = strKey;
        final String string = currentMediaItem.mediaMetadata.title == null ? "" : currentMediaItem.mediaMetadata.title.toString();
        final String string2 = currentMediaItem.mediaMetadata.artist != null ? currentMediaItem.mediaMetadata.artist.toString() : "";
        long duration = this.controller.getDuration();
        final long j = (duration <= 0 || duration == C.TIME_UNSET) ? 0L : duration / 1000;
        this.header.setText(string.length() > 0 ? string : "Letras");
        this.status.setText("Buscando letra...");
        this.lines.removeAllViews();
        this.parsed.clear();
        this.active = -1;
        this.activeView = -1;
        new Thread(new Runnable() { // from class: com.muzk.addon.LyricsActivity.5
            @Override // java.lang.Runnable
            public void run() throws Throwable {
                final String strFetch = LyricsRepo.fetch(LyricsActivity.this, string, string2, j);
                final List<Lrc.Line> list = Lrc.parse(strFetch);
                LyricsActivity.this.runOnUiThread(new Runnable() { // from class: com.muzk.addon.LyricsActivity.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LyricsActivity.this.show(strFetch, list);
                    }
                });
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(String str, List<Lrc.Line> list) {
        this.lines.removeAllViews();
        this.parsed.clear();
        this.active = -1;
        this.activeView = -1;
        if (str == null) {
            this.status.setText("Letra não encontrada.");
            return;
        }
        this.synced = Lrc.isSynced(str);
        if (list.isEmpty()) {
            TextView textViewText = Ui.text(this, str, 16.0f, false);
            textViewText.setLineSpacing(Ui.dp(this, 4.0f), 1.0f);
            this.lines.addView(textViewText);
            this.status.setText("");
            return;
        }
        this.parsed.addAll(list);
        this.status.setText(this.synced ? "Sincronizada" : "Letra simples (sem sincronia)");
        for (int i = 0; i < this.parsed.size(); i++) {
            final Lrc.Line line = this.parsed.get(i);
            TextView textView = new TextView(this);
            textView.setText(line.text.length() == 0 ? " " : line.text);
            textView.setTextSize(this.synced ? 16.0f : 15.0f);
            textView.setPadding(0, Ui.dp(this, 6.0f), 0, Ui.dp(this, 6.0f));
            textView.setTextColor(Ui.color(this, R.attr.textColorSecondary));
            if (this.synced) {
                textView.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.LyricsActivity.6
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        if (LyricsActivity.this.controller != null) {
                            LyricsActivity.this.controller.seekTo(line.timeMs);
                        }
                    }
                });
            }
            this.lines.addView(textView);
        }
        update();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        if (!this.synced || this.controller == null || this.parsed.isEmpty()) {
            return;
        }
        long currentPosition = this.controller.getCurrentPosition();
        int i = -1;
        for (int i2 = 0; i2 < this.parsed.size() && this.parsed.get(i2).timeMs <= 150 + currentPosition; i2++) {
            i = i2;
        }
        if (i == this.active) {
            return;
        }
        int i3 = this.activeView;
        if (i3 >= 0 && i3 < this.lines.getChildCount()) {
            TextView textView = (TextView) this.lines.getChildAt(this.activeView);
            textView.setTextColor(Ui.color(this, R.attr.textColorSecondary));
            textView.setTypeface(Typeface.DEFAULT, 0);
        }
        this.active = i;
        this.activeView = i;
        if (i < 0 || i >= this.lines.getChildCount()) {
            return;
        }
        TextView textView2 = (TextView) this.lines.getChildAt(i);
        textView2.setTextColor(Ui.color(this, R.attr.textColorPrimary));
        textView2.setTypeface(Typeface.DEFAULT, 1);
        final int top = textView2.getTop();
        this.scroll.post(new Runnable() { // from class: com.muzk.addon.LyricsActivity.7
            @Override // java.lang.Runnable
            public void run() {
                LyricsActivity.this.scroll.smoothScrollTo(0, Math.max(0, top - (LyricsActivity.this.scroll.getHeight() / 3)));
            }
        });
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        this.handler.removeCallbacks(this.tick);
        MediaBridge.release(this.controller);
        this.controller = null;
        super.onDestroy();
    }
}
