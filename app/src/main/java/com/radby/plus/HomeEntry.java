package com.radby.plus;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/* JADX INFO: loaded from: classes14.dex */
public final class HomeEntry {
    private static final String TAG = "radby-discover-entry";

    private HomeEntry() {
    }

    public static void attach(View view) {
        try {
            if (!(view instanceof ViewGroup)) {
                return;
            }
            ViewGroup viewGroup = (ViewGroup) view;
            if (viewGroup.getChildCount() == 0) {
                return;
            }
            View childAt = viewGroup.getChildAt(0);
            if (!(childAt instanceof LinearLayout)) {
                return;
            }
            LinearLayout linearLayout = (LinearLayout) childAt;
            for (int i = 0; i < linearLayout.getChildCount(); i++) {
                if (TAG.equals(linearLayout.getChildAt(i).getTag())) {
                    return;
                }
            }
            Context context = view.getContext();
            if (context == null) {
                return;
            }
            TextView textView = new TextView(context);
            textView.setTag(TAG);
            textView.setText("Descobrir novas mÃºsicas");
            textView.setTextSize(2, 15.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(-1);
            textView.setGravity(16);
            textView.setPadding(dp(context, 18), dp(context, 15), dp(context, 18), dp(context, 15));
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(accent(context));
            gradientDrawable.setCornerRadius(dp(context, 14));
            textView.setBackground(gradientDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
            layoutParams.setMargins(dp(context, 16), dp(context, 4), dp(context, 16), dp(context, 12));
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(new View.OnClickListener() { // from class: com.radby.plus.HomeEntry.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    try {
                        Context context2 = view2.getContext();
                        context2.startActivity(new Intent(context2, (Class<?>) ChartsActivity.class));
                    } catch (Throwable th) {
                    }
                }
            });
            int childCount = 1;
            if (linearLayout.getChildCount() <= 1) {
                childCount = linearLayout.getChildCount();
            }
            linearLayout.addView(textView, childCount);
        } catch (Throwable th) {
        }
    }

    private static int accent(Context context) {
        try {
            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true)) {
                if (typedValue.type >= 28 && typedValue.type <= 31) {
                    return typedValue.data;
                }
                if (typedValue.resourceId != 0) {
                    return context.getResources().getColorStateList(typedValue.resourceId).getDefaultColor();
                }
                return -45683;
            }
            return -45683;
        } catch (Throwable th) {
            return -45683;
        }
    }

    private static int dp(Context context, int i) {
        return Math.round(TypedValue.applyDimension(1, i, context.getResources().getDisplayMetrics()));
    }
}
