package com.muzk.addon;

import android.R;
import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/* JADX INFO: loaded from: classes13.dex */
public final class Ui {
    public static int dp(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int color(Context context, int i) {
        TypedValue typedValue = new TypedValue();
        if (!context.getTheme().resolveAttribute(i, typedValue, true)) {
            return -7829368;
        }
        if (typedValue.resourceId != 0) {
            return context.getResources().getColor(typedValue.resourceId);
        }
        return typedValue.data;
    }

    public static TextView text(Context context, String str, float f, boolean z) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        if (z) {
            textView.setTypeface(textView.getTypeface(), 1);
        }
        textView.setTextColor(color(context, R.attr.textColorPrimary));
        return textView;
    }

    public static Button button(Context context, String str) {
        Button button = new Button(context);
        button.setText(str);
        button.setAllCaps(false);
        return button;
    }

    public static LinearLayout column(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(dp(context, 16.0f), dp(context, 16.0f), dp(context, 16.0f), dp(context, 16.0f));
        return linearLayout;
    }

    public static void toast(Context context, String str) {
        Toast.makeText(context, str, 1).show();
    }

    public static void applyInsets(View view) {
        view.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.muzk.addon.Ui.1
            private boolean applied = false;

            @Override // android.view.View.OnApplyWindowInsetsListener
            public WindowInsets onApplyWindowInsets(View view2, WindowInsets windowInsets) {
                if (!this.applied) {
                    this.applied = true;
                    view2.setPadding(view2.getPaddingLeft(), windowInsets.getSystemWindowInsetTop(), view2.getPaddingRight(), windowInsets.getSystemWindowInsetBottom());
                }
                return windowInsets;
            }
        });
    }

    private Ui() {
    }
}
