package com.muzk.addon;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

/* JADX INFO: loaded from: classes13.dex */
public class CloudBackupPreference extends Preference {
    public CloudBackupPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.muzk.addon.CloudBackupPreference.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                CloudBackupPreference.this.run();
                return true;
            }
        });
    }

    public CloudBackupPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.muzk.addon.CloudBackupPreference.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                CloudBackupPreference.this.run();
                return true;
            }
        });
    }

    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        setSummary(Cloud.lastBackupText(getContext()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void run() {
        final Context context = getContext();
        if (!new TokenStore(context).hasRefresh()) {
            Ui.toast(context, "Entre com o Google primeiro.");
            context.startActivity(new Intent(context, (Class<?>) CloudAuthActivity.class));
        } else {
            setSummary("Enviando para o Drive...");
            Ui.toast(context, "Enviando backup...");
            final Handler handler = new Handler(Looper.getMainLooper());
            new Thread(new Runnable() { // from class: com.muzk.addon.CloudBackupPreference.3
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        final long jBackupNow = Cloud.backupNow(context);
                        handler.post(new Runnable() { // from class: com.muzk.addon.CloudBackupPreference.3.1
                            @Override // java.lang.Runnable
                            public void run() {
                                CloudBackupPreference.this.setSummary(Cloud.lastBackupText(context));
                                Ui.toast(context, "Backup enviado (" + jBackupNow + " KB).");
                            }
                        });
                    } catch (Exception e) {
                        handler.post(new Runnable() { // from class: com.muzk.addon.CloudBackupPreference.3.2
                            @Override // java.lang.Runnable
                            public void run() {
                                CloudBackupPreference.this.setSummary(Cloud.lastBackupText(context));
                                Ui.toast(context, "Falha no backup: " + e.getMessage());
                            }
                        });
                    }
                }
            }).start();
        }
    }
}
