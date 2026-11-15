package com.muzk.addon;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;

/* JADX INFO: loaded from: classes13.dex */
public class CloudLoginPreference extends Preference {
    public CloudLoginPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.muzk.addon.CloudLoginPreference.1
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                CloudLoginPreference.this.handle();
                return true;
            }
        });
    }

    public CloudLoginPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.muzk.addon.CloudLoginPreference.2
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                CloudLoginPreference.this.handle();
                return true;
            }
        });
    }

    @Override // androidx.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        super.onAttachedToHierarchy(preferenceManager);
        refreshSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshSummary() {
        String strEmail;
        TokenStore tokenStore = new TokenStore(getContext());
        if (tokenStore.hasRefresh()) {
            strEmail = tokenStore.email() != null ? tokenStore.email() : "Conectado";
        } else {
            strEmail = "Toque para entrar";
        }
        setSummary(strEmail);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handle() {
        final Context context = getContext();
        if (new TokenStore(context).hasRefresh()) {
            new AlertDialog.Builder(context).setTitle("Conta Google").setMessage("Conectado como " + String.valueOf(new TokenStore(context).email())).setNegativeButton("Fechar", (DialogInterface.OnClickListener) null).setPositiveButton("Desconectar", new DialogInterface.OnClickListener() { // from class: com.muzk.addon.CloudLoginPreference.3
                @Override // android.content.DialogInterface.OnClickListener
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        Cloud.logout(context);
                    } catch (Throwable unused) {
                    }
                    CloudLoginPreference.this.refreshSummary();
                    Ui.toast(context, "Conta desconectada.");
                }
            }).show();
            return;
        }
        context.startActivity(new Intent(context, (Class<?>) CloudAuthActivity.class));
    }
}
