package com.muzk.addon;

import android.content.Context;
import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes13.dex */
public final class Cloud {
    public static final String AUTO_KEY = "addon_auto_backup";
    private static volatile boolean running;

    public static String token(Context context) {
        TokenStore tokenStore = new TokenStore(context);
        String strAccess = tokenStore.access();
        if (strAccess != null && !tokenStore.expired()) {
            return strAccess;
        }
        String strRefresh = tokenStore.refresh();
        if (strRefresh == null) {
            return null;
        }
        try {
            JSONObject jSONObjectRefresh = OAuth.refresh(strRefresh, Config.CLIENT_ID);
            String string = jSONObjectRefresh.getString("access_token");
            tokenStore.save(string, jSONObjectRefresh.optString("refresh_token", null), jSONObjectRefresh.optLong("expires_in", 3600L), tokenStore.email());
            return string;
        } catch (Exception unused) {
            return null;
        }
    }

    public static void logout(Context context) throws Throwable {
        TokenStore tokenStore = new TokenStore(context);
        OAuth.revoke(tokenStore.refresh());
        OAuth.revoke(tokenStore.access());
        tokenStore.clear();
    }

    public static boolean autoEnabled(Context context) {
        return context.getSharedPreferences(context.getPackageName() + "_preferences", 0).getBoolean(AUTO_KEY, false);
    }

    public static long lastBackupMs(Context context) {
        return context.getSharedPreferences(Config.PREFS, 0).getLong("lastBackup", 0L);
    }

    public static String lastBackupText(Context context) {
        long jLastBackupMs = lastBackupMs(context);
        if (jLastBackupMs <= 0) {
            return "Nunca feito";
        }
        return "Último: " + DateFormat.getDateTimeInstance(3, 3).format(new Date(jLastBackupMs));
    }

    public static long backupNow(Context context) throws Exception {
        String str = token(context);
        if (str == null) {
            throw new Exception("entre com o Google primeiro");
        }
        File fileCreate = Backup.create(context);
        long length = fileCreate.length() / 1024;
        DriveClient.upload(context, str, fileCreate, DriveClient.find(context, str, Config.BACKUP_NAME));
        context.getSharedPreferences(Config.PREFS, 0).edit().putLong("lastBackup", System.currentTimeMillis()).apply();
        return length;
    }

    public static void autoIfDue(final Context context) {
        if (!running && autoEnabled(context) && new TokenStore(context).hasRefresh()) {
            long jLastBackupMs = lastBackupMs(context);
            if (jLastBackupMs <= 0 || System.currentTimeMillis() - jLastBackupMs >= 86400000) {
                running = true;
                new Thread(new Runnable() { // from class: com.muzk.addon.Cloud.1
                    @Override // java.lang.Runnable
                    public void run() {
                        try {
                            Cloud.backupNow(context);
                        } catch (Throwable unused) {
                        }
                        boolean unused2 = Cloud.running = false;
                    }
                }).start();
            }
        }
    }

    private Cloud() {
    }
}
