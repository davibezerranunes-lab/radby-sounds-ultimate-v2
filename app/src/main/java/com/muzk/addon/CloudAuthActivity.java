package com.muzk.addon;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import com.radby.sounds.download.DownloadWorker;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes13.dex */
public class CloudAuthActivity extends Activity {
    private static final String KEY_STATE = "state";
    private static final String KEY_VERIFIER = "verifier";

    @Override // android.app.Activity
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(Ui.text(this, "Conectando ao Google...", 16.0f, false));
        if (handle(bundle)) {
            return;
        }
        start();
    }

    @Override // android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handle(null);
    }

    private boolean handle(Bundle bundle) {
        Intent intent = getIntent();
        Uri data = intent != null ? intent.getData() : null;
        if (data == null) {
            return false;
        }
        final String queryParameter = data.getQueryParameter("code");
        String queryParameter2 = data.getQueryParameter(DownloadWorker.KEY_ERROR);
        String queryParameter3 = data.getQueryParameter(KEY_STATE);
        String string = getSharedPreferences(Config.PREFS, 0).getString(KEY_STATE, null);
        final String string2 = getSharedPreferences(Config.PREFS, 0).getString(KEY_VERIFIER, null);
        if (queryParameter2 != null) {
            finishWith("Login cancelado: " + queryParameter2);
            return true;
        }
        if (queryParameter == null) {
            return false;
        }
        if (string != null && queryParameter3 != null && !string.equals(queryParameter3)) {
            finishWith("Falha de seguranca (state).");
            return true;
        }
        setText("Finalizando login...");
        new Thread(new Runnable() { // from class: com.muzk.addon.CloudAuthActivity.1
            @Override // java.lang.Runnable
            public void run() {
                String str;
                try {
                    JSONObject jSONObject = OAuth.token(queryParameter, string2, Config.CLIENT_ID, Config.REDIRECT_URI);
                    String string3 = jSONObject.getString("access_token");
                    String strOptString = jSONObject.optString("refresh_token", null);
                    long jOptLong = jSONObject.optLong("expires_in", 3600L);
                    String strEmail = OAuth.email(string3);
                    new TokenStore(CloudAuthActivity.this).save(string3, strOptString, jOptLong, strEmail);
                    CloudAuthActivity cloudAuthActivity = CloudAuthActivity.this;
                    StringBuilder sb = new StringBuilder("Conectado");
                    if (strEmail != null) {
                        str = " como " + strEmail;
                    } else {
                        str = "";
                    }
                    sb.append(str);
                    sb.append(".");
                    cloudAuthActivity.finishWith(sb.toString());
                } catch (Throwable e) {
                    CloudAuthActivity.this.finishWith("Falha no login: " + e.getMessage());
                }
            }
        }).start();
        return true;
    }

    private void start() {
        String strVerifier = Pkce.verifier();
        String strChallenge = Pkce.challenge(strVerifier);
        String strState = Pkce.state();
        getSharedPreferences(Config.PREFS, 0).edit().putString(KEY_VERIFIER, strVerifier).putString(KEY_STATE, strState).apply();
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(OAuth.authUrl(Config.CLIENT_ID, Config.REDIRECT_URI, strChallenge, strState))));
        } catch (Exception unused) {
            finishWith("Nenhum navegador disponivel.");
        }
    }

    private void setText(final String str) {
        runOnUiThread(new Runnable() { // from class: com.muzk.addon.CloudAuthActivity.2
            @Override // java.lang.Runnable
            public void run() {
                if (CloudAuthActivity.this.findViewById(R.id.content) instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) CloudAuthActivity.this.findViewById(R.id.content);
                    if (viewGroup.getChildCount() <= 0 || !(viewGroup.getChildAt(0) instanceof TextView)) {
                        return;
                    }
                    ((TextView) viewGroup.getChildAt(0)).setText(str);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishWith(final String str) {
        runOnUiThread(new Runnable() { // from class: com.muzk.addon.CloudAuthActivity.3
            @Override // java.lang.Runnable
            public void run() {
                Ui.toast(CloudAuthActivity.this, str);
                CloudAuthActivity.this.setResult(-1);
                CloudAuthActivity.this.finish();
            }
        });
    }
}
