package com.muzk.addon;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.core.app.NotificationCompat;

/* JADX INFO: loaded from: classes13.dex */
public final class TokenStore {
    private final SharedPreferences p;

    public TokenStore(Context context) {
        this.p = context.getApplicationContext().getSharedPreferences(Config.PREFS, 0);
    }

    public void save(String str, String str2, long j, String str3) {
        SharedPreferences.Editor editorPutLong = this.p.edit().putString("access", str).putLong("expiresAt", System.currentTimeMillis() + (j * 1000));
        if (str2 != null) {
            editorPutLong.putString("refresh", str2);
        }
        if (str3 != null) {
            editorPutLong.putString(NotificationCompat.CATEGORY_EMAIL, str3);
        }
        editorPutLong.apply();
    }

    public String access() {
        return this.p.getString("access", null);
    }

    public String refresh() {
        return this.p.getString("refresh", null);
    }

    public String email() {
        return this.p.getString(NotificationCompat.CATEGORY_EMAIL, null);
    }

    public boolean hasRefresh() {
        return refresh() != null;
    }

    public boolean expired() {
        return System.currentTimeMillis() > this.p.getLong("expiresAt", 0L) - 60000;
    }

    public void clear() {
        this.p.edit().clear().apply();
    }
}
