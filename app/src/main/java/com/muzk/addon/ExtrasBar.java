package com.muzk.addon;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes13.dex */
public class ExtrasBar extends LinearLayout {
    public ExtrasBar(Context context) {
        super(context);
        init();
    }

    public ExtrasBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public ExtrasBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setOrientation(0);
        setGravity(17);
        setPadding(Ui.dp(getContext(), 8.0f), Ui.dp(getContext(), 4.0f), Ui.dp(getContext(), 8.0f), Ui.dp(getContext(), 4.0f));
        addAction("Letras", LyricsActivity.class);
        addAction("Tela cheia", FullscreenActivity.class);
    }

    private void addAction(String str, final Class<?> cls) {
        TextView textView = new TextView(getContext());
        textView.setText(str);
        textView.setTextSize(13.0f);
        textView.setGravity(17);
        textView.setPadding(Ui.dp(getContext(), 6.0f), Ui.dp(getContext(), 10.0f), Ui.dp(getContext(), 6.0f), Ui.dp(getContext(), 10.0f));
        textView.setTextColor(Ui.color(getContext(), R.attr.textColorPrimary));
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.muzk.addon.ExtrasBar.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                try {
                    ExtrasBar.this.getContext().startActivity(new Intent(ExtrasBar.this.getContext(), (Class<?>) cls));
                } catch (Throwable unused) {
                    Ui.toast(ExtrasBar.this.getContext(), "Não foi possível abrir.");
                }
            }
        });
        addView(textView, new LinearLayout.LayoutParams(0, -2, 1.0f));
    }
}
