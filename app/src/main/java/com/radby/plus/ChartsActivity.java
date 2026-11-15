package com.radby.plus;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes14.dex */
public class ChartsActivity extends Activity {
    private static final String[] TABS = {"RegiÃ£o", "PaÃ\u00ads", "Mundo"};
    private TrackAdapter adapter;
    private String cc;
    private int colorAccent;
    private int colorBg;
    private int colorPrimary;
    private int colorSub;
    private int colorTitle;
    private ListView list;
    private int request;
    private ProgressBar spinner;
    private TextView status;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final ExecutorService io = Executors.newFixedThreadPool(2);
    private final List<TextView> tabLabels = new ArrayList();
    private final List<View> tabBars = new ArrayList();
    private final List<List<ChartsData.Track>> cache = new ArrayList();
    private int tab = -1;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.colorBg = themeColor(this, R.attr.colorBackground, -15724524);
        this.colorPrimary = themeColor(this, R.attr.colorPrimary, -45683);
        this.colorAccent = themeColor(this, R.attr.colorAccent, this.colorPrimary);
        this.colorTitle = themeColor(this, R.attr.textColorPrimary, -1);
        this.colorSub = themeColor(this, R.attr.textColorSecondary, -4934466);
        this.cc = ChartsData.deviceCountry(this);
        for (int i = 0; i < TABS.length; i++) {
            this.cache.add(null);
        }
        setContentView(buildUi());
        select(0);
    }

    private View buildUi() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setBackgroundColor(this.colorBg);
        int identifier = getResources().getIdentifier("status_bar_height", "dimen", "android");
        linearLayout.setPadding(0, identifier > 0 ? getResources().getDimensionPixelSize(identifier) : dp(24), 0, 0);
        TextView textView = new TextView(this);
        textView.setText("Descobrir");
        textView.setTextSize(2, 26.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setTextColor(this.colorTitle);
        textView.setPadding(dp(20), dp(14), dp(20), 0);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("As mais tocadas agora â€” " + this.cc.toUpperCase());
        textView2.setTextSize(2, 13.0f);
        textView2.setTextColor(this.colorSub);
        textView2.setPadding(dp(20), dp(4), dp(20), dp(10));
        linearLayout.addView(textView2);
        LinearLayout linearLayout2 = new LinearLayout(this);
        linearLayout2.setOrientation(0);
        linearLayout2.setPadding(dp(12), 0, dp(12), 0);
        for (final int i = 0; i < TABS.length; i++) {
            LinearLayout linearLayout3 = new LinearLayout(this);
            linearLayout3.setOrientation(1);
            linearLayout3.setGravity(17);
            linearLayout3.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
            TextView textView3 = new TextView(this);
            textView3.setText(TABS[i]);
            textView3.setTextSize(2, 15.0f);
            textView3.setTextColor(this.colorSub);
            textView3.setPadding(0, dp(10), 0, dp(8));
            linearLayout3.addView(textView3);
            View view = new View(this);
            view.setBackgroundColor(0);
            linearLayout3.addView(view, new LinearLayout.LayoutParams(-1, dp(3)));
            linearLayout3.setOnClickListener(new View.OnClickListener() { // from class: com.radby.plus.ChartsActivity.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    ChartsActivity.this.select(i);
                }
            });
            linearLayout2.addView(linearLayout3);
            this.tabLabels.add(textView3);
            this.tabBars.add(view);
        }
        linearLayout.addView(linearLayout2);
        FrameLayout frameLayout = new FrameLayout(this);
        linearLayout.addView(frameLayout, new LinearLayout.LayoutParams(-1, 0, 1.0f));
        this.list = new ListView(this);
        this.list.setDivider(null);
        this.list.setDividerHeight(0);
        this.list.setSelector(R.color.transparent);
        this.list.setCacheColorHint(0);
        this.adapter = new TrackAdapter();
        this.list.setAdapter((ListAdapter) this.adapter);
        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.radby.plus.ChartsActivity.2
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> adapterView, View view2, int i2, long j) {
                ChartsActivity.this.play(ChartsActivity.this.adapter.items.get(i2));
            }
        });
        frameLayout.addView(this.list, new FrameLayout.LayoutParams(-1, -1));
        this.spinner = new ProgressBar(this);
        frameLayout.addView(this.spinner, new FrameLayout.LayoutParams(dp(44), dp(44), 17));
        this.status = new TextView(this);
        this.status.setGravity(17);
        this.status.setTextColor(this.colorSub);
        this.status.setTextSize(2, 14.0f);
        this.status.setPadding(dp(24), dp(24), dp(24), dp(24));
        this.status.setVisibility(8);
        frameLayout.addView(this.status, new FrameLayout.LayoutParams(-1, -2, 17));
        return linearLayout;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void select(int i) {
        this.tab = i;
        int i2 = 0;
        while (i2 < this.tabLabels.size()) {
            this.tabLabels.get(i2).setTextColor(i2 == i ? this.colorAccent : this.colorSub);
            this.tabLabels.get(i2).setTypeface(i2 == i ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
            this.tabBars.get(i2).setBackgroundColor(i2 == i ? this.colorAccent : 0);
            i2++;
        }
        List<ChartsData.Track> list = this.cache.get(i);
        if (list != null) {
            show(list);
        } else {
            load(i);
        }
    }

    private void load(final int i) {
        final int i2 = this.request + 1;
        this.request = i2;
        this.adapter.items = new ArrayList();
        this.adapter.notifyDataSetChanged();
        this.spinner.setVisibility(0);
        this.status.setVisibility(8);
        this.io.submit(new Runnable() { // from class: com.radby.plus.ChartsActivity.3
            @Override // java.lang.Runnable
            public void run() {
                final String simpleName;
                List<ChartsData.Track> listCountry;
                final List<ChartsData.Track> list = null;
                try {
                    if (i == 0) {
                        listCountry = ChartsData.region(ChartsActivity.this, ChartsActivity.this.cc);
                    } else {
                        listCountry = i == 1 ? ChartsData.country(ChartsActivity.this, ChartsActivity.this.cc) : ChartsData.world(ChartsActivity.this);
                    }
                    List<ChartsData.Track> list2 = listCountry;
                    simpleName = null;
                    list = list2;
                } catch (Throwable th) {
                    simpleName = th.getMessage() == null ? th.getClass().getSimpleName() : th.getMessage();
                }
                ChartsActivity.this.ui.post(new Runnable() { // from class: com.radby.plus.ChartsActivity.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (i2 == ChartsActivity.this.request) {
                            ChartsActivity.this.spinner.setVisibility(8);
                            if (list == null || list.isEmpty()) {
                                ChartsActivity.this.adapter.items = new ArrayList();
                                ChartsActivity.this.adapter.notifyDataSetChanged();
                                ChartsActivity.this.status.setText("NÃ£o consegui carregar agora.\n" + (simpleName == null ? "sem dados" : simpleName) + "\nToque na aba de novo para tentar.");
                                ChartsActivity.this.status.setVisibility(0);
                                return;
                            }
                            ChartsActivity.this.cache.set(i, list);
                            ChartsActivity.this.show(list);
                        }
                    }
                });
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void show(List<ChartsData.Track> list) {
        this.adapter.items = list;
        this.adapter.notifyDataSetChanged();
        this.status.setVisibility(8);
        this.spinner.setVisibility(8);
        this.list.setSelection(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void play(final ChartsData.Track track) {
        Toast.makeText(this, "Buscando: " + track.title, 0).show();
        this.io.submit(new Runnable() { // from class: com.radby.plus.ChartsActivity.4
            @Override // java.lang.Runnable
            public void run() {
                final String simpleName;
                try {
                    PlayBridge.play(ChartsActivity.this, track);
                    simpleName = null;
                } catch (Throwable th) {
                    simpleName = th.getMessage() == null ? th.getClass().getSimpleName() : th.getMessage();
                }
                ChartsActivity.this.ui.post(new Runnable() { // from class: com.radby.plus.ChartsActivity.4.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (simpleName != null) {
                            Toast.makeText(ChartsActivity.this, "NÃ£o deu: " + simpleName, 1).show();
                        } else {
                            Toast.makeText(ChartsActivity.this, "Tocando: " + track.title, 0).show();
                        }
                    }
                });
            }
        });
    }

    private final class TrackAdapter extends BaseAdapter {
        List<ChartsData.Track> items;

        private TrackAdapter() {
            this.items = new ArrayList();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            return this.items.size();
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.items.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            LinearLayout linearLayout;
            if ((view instanceof LinearLayout) && view.getTag() != null) {
                linearLayout = (LinearLayout) view;
            } else {
                linearLayout = new LinearLayout(ChartsActivity.this);
                linearLayout.setOrientation(0);
                linearLayout.setGravity(16);
                linearLayout.setPadding(ChartsActivity.this.dp(18), ChartsActivity.this.dp(10), ChartsActivity.this.dp(18), ChartsActivity.this.dp(10));
                TypedValue typedValue = new TypedValue();
                if (ChartsActivity.this.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true) && typedValue.resourceId != 0) {
                    linearLayout.setBackgroundResource(typedValue.resourceId);
                }
                linearLayout.setTag(Boolean.TRUE);
                TextView textView = new TextView(ChartsActivity.this);
                textView.setTag("rank");
                textView.setTextSize(2, 14.0f);
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setTextColor(ChartsActivity.this.colorAccent);
                textView.setGravity(17);
                linearLayout.addView(textView, new LinearLayout.LayoutParams(ChartsActivity.this.dp(34), -2));
                LinearLayout linearLayout2 = new LinearLayout(ChartsActivity.this);
                linearLayout2.setOrientation(1);
                linearLayout2.setPadding(ChartsActivity.this.dp(6), 0, ChartsActivity.this.dp(6), 0);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(0, -2, 1.0f));
                TextView textView2 = new TextView(ChartsActivity.this);
                textView2.setTag("name");
                textView2.setTextSize(2, 15.0f);
                textView2.setTextColor(ChartsActivity.this.colorTitle);
                textView2.setSingleLine(true);
                textView2.setEllipsize(TextUtils.TruncateAt.END);
                linearLayout2.addView(textView2);
                TextView textView3 = new TextView(ChartsActivity.this);
                textView3.setTag("artist");
                textView3.setTextSize(2, 12.0f);
                textView3.setTextColor(ChartsActivity.this.colorSub);
                textView3.setSingleLine(true);
                textView3.setEllipsize(TextUtils.TruncateAt.END);
                linearLayout2.addView(textView3);
                TextView textView4 = new TextView(ChartsActivity.this);
                textView4.setTag("go");
                textView4.setText("â–¶");
                textView4.setTextSize(2, 18.0f);
                textView4.setTextColor(ChartsActivity.this.colorAccent);
                linearLayout.addView(textView4, new LinearLayout.LayoutParams(ChartsActivity.this.dp(28), -2));
            }
            ChartsData.Track track = this.items.get(i);
            ((TextView) linearLayout.findViewWithTag("rank")).setText(String.valueOf(i + 1));
            ((TextView) linearLayout.findViewWithTag("name")).setText(track.title);
            ((TextView) linearLayout.findViewWithTag("artist")).setText(track.artist);
            return linearLayout;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int dp(int i) {
        return Math.round(TypedValue.applyDimension(1, i, getResources().getDisplayMetrics()));
    }

    private static int themeColor(Context context, int i, int i2) {
        try {
            TypedValue typedValue = new TypedValue();
            if (context.getTheme().resolveAttribute(i, typedValue, true)) {
                if (typedValue.type >= 28 && typedValue.type <= 31) {
                    return typedValue.data;
                }
                if (typedValue.resourceId != 0) {
                    try {
                        return context.getResources().getColorStateList(typedValue.resourceId).getDefaultColor();
                    } catch (Throwable th) {
                        try {
                            return context.getResources().getColor(typedValue.resourceId);
                        } catch (Throwable th2) {
                        }
                    }
                }
            }
        } catch (Throwable th3) {
        }
        return i2;
    }
}
