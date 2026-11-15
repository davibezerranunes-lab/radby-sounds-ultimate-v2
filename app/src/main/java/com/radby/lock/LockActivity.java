package com.radby.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.common.net.HttpHeaders;
import com.radby.plus.HomeInjector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes14.dex */
public final class LockActivity extends Activity {
    private static final String API_KEY = "AIzaSyCH3yb01MGEH3Eku2lUZIp6_eedA9R_D2Y";
    private static final String KEY_FAILS = "fails";
    private static final String KEY_UNLOCKED = "unlocked";
    private static final String KEY_UNTIL = "lock_until";
    private static final long LOCKOUT_MS = 30000;
    private static final int MAX_FAILS = 5;
    private static final String PREFS = "radby_lock";
    private static final String PROJECT = "radby-sounds";
    private static final String TAG = "RadbyLock";
    private static final String TARGET = "com.radby.sounds.ui.MainActivity";
    private static final Pattern UPDATE_TIME = Pattern.compile("\"updateTime\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern USED = Pattern.compile("\"used\"\\s*:\\s*\\{\\s*\"booleanValue\"\\s*:\\s*(true|false)");
    private Button button;
    private boolean checking;
    private boolean coolingDown;
    private TextView error;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private EditText input;

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            installCrashHandler();
            note("onCreate");
            try {
                HomeInjector.install(getApplication());
            } catch (Throwable th) {
            }
            getWindow().setFlags(8192, 8192);
            try {
                getWindow().setStatusBarColor(Color.parseColor("#0B0B14"));
                getWindow().setNavigationBarColor(Color.parseColor("#0B0B14"));
            } catch (Throwable th2) {
            }
            if (prefs().getBoolean(KEY_UNLOCKED, false)) {
                openApp();
                return;
            }
            buildUi();
            updateCooldown();
            note("ui-ready");
        } catch (Throwable th3) {
            showFatal(th3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SharedPreferences prefs() {
        return getSharedPreferences(PREFS, 0);
    }

    private void buildUi() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setGravity(17);
        linearLayout.setBackgroundColor(Color.parseColor("#0B0B14"));
        int iDp = dp(28);
        linearLayout.setPadding(iDp, iDp, iDp, iDp);
        TextView textView = new TextView(this);
        textView.setText("RADBY SONDS");
        textView.setTextColor(-1);
        textView.setTextSize(26.0f);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setGravity(17);
        linearLayout.addView(textView);
        TextView textView2 = new TextView(this);
        textView2.setText("Digite o codigo de 6 digitos para entrar");
        textView2.setTextColor(Color.parseColor("#94A3B8"));
        textView2.setTextSize(14.0f);
        textView2.setGravity(17);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.topMargin = dp(10);
        linearLayout.addView(textView2, layoutParams);
        this.input = new EditText(this);
        this.input.setInputType(2);
        this.input.setGravity(17);
        this.input.setTextSize(24.0f);
        this.input.setTextColor(-1);
        this.input.setHintTextColor(Color.parseColor("#475569"));
        this.input.setHint("000000");
        this.input.setSingleLine(true);
        this.input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        try {
            this.input.setLetterSpacing(0.35f);
        } catch (Throwable th) {
        }
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColor(Color.parseColor("#141424"));
        gradientDrawable.setCornerRadius(dp(16));
        gradientDrawable.setStroke(dp(1), Color.parseColor("#2A2A4A"));
        this.input.setBackground(gradientDrawable);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, dp(64));
        layoutParams2.topMargin = dp(28);
        linearLayout.addView(this.input, layoutParams2);
        this.error = new TextView(this);
        this.error.setTextColor(Color.parseColor("#F87171"));
        this.error.setTextSize(13.0f);
        this.error.setGravity(17);
        this.error.setVisibility(8);
        LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams3.topMargin = dp(14);
        linearLayout.addView(this.error, layoutParams3);
        this.button = new Button(this);
        this.button.setText("Entrar");
        this.button.setTextColor(-1);
        this.button.setTextSize(16.0f);
        this.button.setAllCaps(false);
        this.button.setTypeface(Typeface.DEFAULT_BOLD);
        GradientDrawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{Color.parseColor("#8B5CF6"), Color.parseColor("#22D3EE")});
        gradientDrawable2.setCornerRadius(dp(16));
        this.button.setBackground(gradientDrawable2);
        LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams(-1, dp(54));
        layoutParams4.topMargin = dp(22);
        linearLayout.addView(this.button, layoutParams4);
        TextView textView3 = new TextView(this);
        textView3.setText("ACESSO RESTRITO");
        textView3.setTextColor(Color.parseColor("#475569"));
        textView3.setTextSize(11.0f);
        try {
            textView3.setLetterSpacing(0.2f);
        } catch (Throwable th2) {
        }
        textView3.setGravity(17);
        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams(-2, -2);
        layoutParams5.topMargin = dp(34);
        linearLayout.addView(textView3, layoutParams5);
        setContentView(linearLayout);
        this.input.addTextChangedListener(new TextWatcher() { // from class: com.radby.lock.LockActivity.1
            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                if (LockActivity.this.checking || LockActivity.this.coolingDown) {
                    return;
                }
                String string = editable == null ? "" : editable.toString();
                LockActivity.this.button.setEnabled(string.length() == 6);
                if (string.length() == 6) {
                    LockActivity.this.attempt(string);
                }
            }
        });
        this.button.setOnClickListener(new View.OnClickListener() { // from class: com.radby.lock.LockActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                String string = LockActivity.this.input.getText() == null ? "" : LockActivity.this.input.getText().toString();
                if (string.length() == 6) {
                    LockActivity.this.attempt(string);
                }
            }
        });
        this.button.setEnabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attempt(final String str) {
        if (this.checking || this.coolingDown) {
            return;
        }
        setChecking(true);
        new Thread(new Runnable() { // from class: com.radby.lock.LockActivity.3
            @Override // java.lang.Runnable
            public void run() {
                final boolean zClaim = LockActivity.this.claim(str);
                LockActivity.this.runOnUiThread(new Runnable() { // from class: com.radby.lock.LockActivity.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        LockActivity.this.setChecking(false);
                        if (!zClaim) {
                            LockActivity.this.input.setText("");
                            LockActivity.this.registerFailure();
                            if (!LockActivity.this.coolingDown) {
                                LockActivity.this.error.setText("Codigo incorreto ou ja usado.");
                                LockActivity.this.error.setVisibility(0);
                                return;
                            }
                            return;
                        }
                        LockActivity.this.prefs().edit().putBoolean(LockActivity.KEY_UNLOCKED, true).remove(LockActivity.KEY_FAILS).remove(LockActivity.KEY_UNTIL).apply();
                        LockActivity.this.openApp();
                    }
                });
            }
        }, "radby-lock").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setChecking(boolean z) {
        this.checking = z;
        this.input.setEnabled(!z);
        this.button.setEnabled(!z);
        if (z) {
            this.error.setText("Verificando codigo...");
            this.error.setVisibility(0);
        } else if (!this.coolingDown) {
            this.error.setVisibility(8);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean claim(String str) {
        HttpURLConnection httpURLConnection;
        HttpURLConnection httpURLConnection2 = null;
        try {
            String str2 = "https://firestore.googleapis.com/v1/projects/radby-sounds/databases/(default)/documents/codes/" + str;
            String strEncode = URLEncoder.encode(API_KEY, CharsetNames.UTF_8);
            HttpURLConnection httpURLConnection3 = (HttpURLConnection) new URL(str2 + "?key=" + strEncode).openConnection();
            try {
                httpURLConnection3.setRequestMethod("GET");
                httpURLConnection3.setConnectTimeout(12000);
                httpURLConnection3.setReadTimeout(12000);
                if (httpURLConnection3.getResponseCode() != 200) {
                    if (httpURLConnection3 != null) {
                        try {
                            httpURLConnection3.disconnect();
                        } catch (Throwable th) {
                        }
                    }
                    return false;
                }
                String str3 = read(httpURLConnection3.getInputStream());
                Matcher matcher = USED.matcher(str3);
                if (matcher.find() && "true".equals(matcher.group(1))) {
                    if (httpURLConnection3 != null) {
                        try {
                            httpURLConnection3.disconnect();
                        } catch (Throwable th2) {
                        }
                    }
                    return false;
                }
                Matcher matcher2 = UPDATE_TIME.matcher(str3);
                if (!matcher2.find()) {
                    if (httpURLConnection3 != null) {
                        try {
                            httpURLConnection3.disconnect();
                        } catch (Throwable th3) {
                        }
                    }
                    return false;
                }
                String str4 = str2 + "?key=" + strEncode + "&currentDocument.updateTime=" + URLEncoder.encode(matcher2.group(1), CharsetNames.UTF_8) + "&updateMask.fieldPaths=used&updateMask.fieldPaths=usedAt&updateMask.fieldPaths=usedBy";
                String str5 = "{\"fields\":{\"used\":{\"booleanValue\":true},\"usedAt\":{\"timestampValue\":\"" + timestamp() + "\"},\"usedBy\":{\"stringValue\":\"android\"}}}";
                HttpURLConnection httpURLConnection4 = (HttpURLConnection) new URL(str4).openConnection();
                try {
                    httpURLConnection4.setRequestMethod("PATCH");
                    httpURLConnection4.setRequestProperty(HttpHeaders.CONTENT_TYPE, "application/json");
                    httpURLConnection4.setDoOutput(true);
                    httpURLConnection4.setConnectTimeout(12000);
                    httpURLConnection4.setReadTimeout(12000);
                    OutputStream outputStream = httpURLConnection4.getOutputStream();
                    try {
                        outputStream.write(str5.getBytes(StandardCharsets.UTF_8));
                        try {
                            outputStream.close();
                        } catch (Throwable th4) {
                        }
                        boolean z = httpURLConnection4.getResponseCode() == 200;
                        if (httpURLConnection3 != null) {
                            try {
                                httpURLConnection3.disconnect();
                            } catch (Throwable th5) {
                            }
                        }
                        if (httpURLConnection4 != null) {
                            try {
                                httpURLConnection4.disconnect();
                            } catch (Throwable th6) {
                            }
                        }
                        return z;
                    } catch (Throwable th7) {
                        try {
                            outputStream.close();
                        } catch (Throwable th8) {
                        }
                        throw th7;
                    }
                } catch (Throwable th9) {
                    httpURLConnection2 = httpURLConnection3;
                    httpURLConnection = httpURLConnection4;
                    th = th9;
                }
            } catch (Throwable th10) {
                th = th10;
                httpURLConnection = null;
                httpURLConnection2 = httpURLConnection3;
            }
        } catch (Throwable th11) {
            th = th11;
            httpURLConnection = null;
        }
        try {
            logError("claim-failed", th);
            return false;
        } finally {
            if (httpURLConnection2 != null) {
                try {
                    httpURLConnection2.disconnect();
                } catch (Throwable th12) {
                }
            }
            if (httpURLConnection != null) {
                try {
                    httpURLConnection.disconnect();
                } catch (Throwable th13) {
                }
            }
        }
    }

    private static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(new Date());
    }

    private static String read(InputStream inputStream) throws Exception {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        while (true) {
            try {
                String line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line).append('\n');
                } else {
                    try {
                        break;
                    } catch (Throwable th) {
                    }
                }
            } catch (Throwable th2) {
                try {
                    bufferedReader.close();
                } catch (Throwable th3) {
                }
                throw th2;
            }
        }
        bufferedReader.close();
        return sb.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void registerFailure() {
        int i = prefs().getInt(KEY_FAILS, 0) + 1;
        SharedPreferences.Editor editorEdit = prefs().edit();
        if (i >= 5) {
            editorEdit.putInt(KEY_FAILS, 0);
            editorEdit.putLong(KEY_UNTIL, SystemClock.elapsedRealtime() + 30000);
        } else {
            editorEdit.putInt(KEY_FAILS, i);
        }
        editorEdit.apply();
        updateCooldown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCooldown() {
        this.handler.removeCallbacksAndMessages(null);
        long j = prefs().getLong(KEY_UNTIL, 0L);
        long jElapsedRealtime = j - SystemClock.elapsedRealtime();
        if (jElapsedRealtime > 0) {
            this.coolingDown = true;
            this.input.setEnabled(false);
            this.button.setEnabled(false);
            this.error.setText("Aguarde " + ((int) Math.ceil(jElapsedRealtime / 1000.0d)) + " s");
            this.error.setVisibility(0);
            this.handler.postDelayed(new Runnable() { // from class: com.radby.lock.LockActivity.4
                @Override // java.lang.Runnable
                public void run() {
                    LockActivity.this.updateCooldown();
                }
            }, 1000L);
            return;
        }
        if (j != 0) {
            prefs().edit().putLong(KEY_UNTIL, 0L).apply();
        }
        this.coolingDown = false;
        if (!this.checking) {
            this.input.setEnabled(true);
            this.button.setEnabled((this.input.getText() == null ? "" : this.input.getText().toString()).length() == 6);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void openApp() {
        try {
            Intent intent = new Intent();
            intent.setClassName(getPackageName(), TARGET);
            startActivity(intent);
        } catch (Throwable th) {
            logError("open-main-failed", th);
        }
        finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        finishAffinity();
    }

    private int dp(int i) {
        return Math.round(i * getResources().getDisplayMetrics().density);
    }

    private void showFatal(Throwable th) {
        String strStack = stack(th);
        logError("fatal", th);
        try {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            linearLayout.setBackgroundColor(Color.parseColor("#1A0000"));
            int iDp = dp(16);
            linearLayout.setPadding(iDp, iDp, iDp, iDp);
            TextView textView = new TextView(this);
            textView.setText("Falha ao abrir a tela de codigo");
            textView.setTextColor(Color.parseColor("#FCA5A5"));
            textView.setTextSize(18.0f);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            linearLayout.addView(textView);
            TextView textView2 = new TextView(this);
            textView2.setText(strStack);
            textView2.setTextColor(-1);
            textView2.setTextSize(11.0f);
            textView2.setTextIsSelectable(true);
            textView2.setTypeface(Typeface.MONOSPACE);
            ScrollView scrollView = new ScrollView(this);
            scrollView.addView(textView2);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, 0, 1.0f);
            layoutParams.topMargin = dp(10);
            linearLayout.addView(scrollView, layoutParams);
            Button button = new Button(this);
            button.setText("Tentar novamente");
            button.setOnClickListener(new View.OnClickListener() { // from class: com.radby.lock.LockActivity.5
                @Override // android.view.View.OnClickListener
                public void onClick(View view) {
                    LockActivity.this.recreate();
                }
            });
            linearLayout.addView(button);
            setContentView(linearLayout);
        } catch (Throwable th2) {
        }
    }

    private void installCrashHandler() {
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() { // from class: com.radby.lock.LockActivity.6
                @Override // java.lang.Thread.UncaughtExceptionHandler
                public void uncaughtException(Thread thread, Throwable th) {
                    LockActivity.this.logError("uncaught-" + thread.getName(), th);
                    Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
                    if (defaultUncaughtExceptionHandler != null && defaultUncaughtExceptionHandler != this) {
                        defaultUncaughtExceptionHandler.uncaughtException(thread, th);
                    }
                }
            });
        } catch (Throwable th) {
        }
    }

    private void note(String str) {
        try {
            appendLine("[" + timestamp() + "] stage: " + str);
        } catch (Throwable th) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logError(String str, Throwable th) {
        String str2 = "[" + timestamp() + "] " + str + IOUtils.LINE_SEPARATOR_UNIX + (th == null ? "" : stack(th)) + IOUtils.LINE_SEPARATOR_UNIX;
        try {
            Log.e(TAG, str2);
        } catch (Throwable th2) {
        }
        try {
            appendLine(str2);
        } catch (Throwable th3) {
        }
    }

    private void appendLine(String str) {
        File filesDir;
        FileOutputStream fileOutputStream = null;
        try {
            filesDir = getExternalFilesDir(null);
        } catch (Throwable th) {
            filesDir = null;
        }
        if (filesDir == null) {
            try {
                filesDir = getFilesDir();
            } catch (Throwable th2) {
            }
        }
        if (filesDir == null) {
            return;
        }
        try {
            try {
                FileOutputStream fileOutputStream2 = new FileOutputStream(new File(filesDir, "radby-lock.log"), true);
                try {
                    fileOutputStream2.write(str.getBytes(StandardCharsets.UTF_8));
                    fileOutputStream2.write(10);
                    fileOutputStream2.flush();
                    fileOutputStream2.close();
                } catch (Throwable th3) {
                    fileOutputStream = fileOutputStream2;
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                }
            } catch (Throwable th4) {
            }
        } catch (Throwable th5) {
        }
    }

    private static String stack(Throwable th) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            th.printStackTrace(printWriter);
            printWriter.flush();
            return stringWriter.toString();
        } catch (Throwable th2) {
            return String.valueOf(th);
        }
    }
}
