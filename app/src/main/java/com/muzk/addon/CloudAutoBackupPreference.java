package com.muzk.addon;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

/* JADX INFO: loaded from: classes13.dex */
public class CloudAutoBackupPreference extends SwitchPreferenceCompat {
    public CloudAutoBackupPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public CloudAutoBackupPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        setSummary("Uma vez por dia, ao abrir o app");
        setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.muzk.addon.CloudAutoBackupPreference.1
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public boolean onPreferenceChange(Preference preference, Object obj) {
                if (!Boolean.TRUE.equals(obj) || new TokenStore(CloudAutoBackupPreference.this.getContext()).hasRefresh()) {
                    return true;
                }
                Ui.toast(CloudAutoBackupPreference.this.getContext(), "Entre com o Google primeiro.");
                return false;
            }
        });
    }

    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
    }
}
