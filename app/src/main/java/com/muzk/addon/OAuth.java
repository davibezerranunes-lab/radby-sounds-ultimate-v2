package com.muzk.addon;

import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import com.google.common.net.HttpHeaders;
import java.util.HashMap;
import org.apache.commons.compress.utils.CharsetNames;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes13.dex */
public final class OAuth {
    public static String authUrl(String str, String str2, String str3, String str4) {
        return Uri.parse(Config.AUTH_ENDPOINT).buildUpon().appendQueryParameter("client_id", str).appendQueryParameter("redirect_uri", str2).appendQueryParameter("response_type", "code").appendQueryParameter("scope", Config.SCOPE).appendQueryParameter("code_challenge", str3).appendQueryParameter("code_challenge_method", "S256").appendQueryParameter("access_type", "offline").appendQueryParameter("prompt", "consent").appendQueryParameter("state", str4).build().toString();
    }

    public static JSONObject token(String str, String str2, String str3, String str4) throws Exception {
        Http.Resp respRequest = Http.request("POST", Config.TOKEN_ENDPOINT, null, ("grant_type=authorization_code&code=" + Pkce.url(str) + "&code_verifier=" + Pkce.url(str2) + "&client_id=" + Pkce.url(str3) + "&redirect_uri=" + Pkce.url(str4)).getBytes(CharsetNames.UTF_8), "application/x-www-form-urlencoded", AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH);
        if (respRequest.code != 200) {
            StringBuilder sb = new StringBuilder("token http ");
            sb.append(respRequest.code);
            sb.append(" ");
            sb.append(respRequest.body != null ? respRequest.body : respRequest.error);
            throw new Exception(sb.toString());
        }
        return new JSONObject(respRequest.body);
    }

    public static JSONObject refresh(String str, String str2) throws Exception {
        Http.Resp respRequest = Http.request("POST", Config.TOKEN_ENDPOINT, null, ("grant_type=refresh_token&refresh_token=" + Pkce.url(str) + "&client_id=" + Pkce.url(str2)).getBytes(CharsetNames.UTF_8), "application/x-www-form-urlencoded", AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH);
        if (respRequest.code != 200) {
            StringBuilder sb = new StringBuilder("refresh http ");
            sb.append(respRequest.code);
            sb.append(" ");
            sb.append(respRequest.body != null ? respRequest.body : respRequest.error);
            throw new Exception(sb.toString());
        }
        return new JSONObject(respRequest.body);
    }

    public static String email(String str) throws Throwable {
        try {
            HashMap map = new HashMap();
            map.put(HttpHeaders.AUTHORIZATION, "Bearer " + str);
            Http.Resp respRequest = Http.request("GET", Config.USERINFO_ENDPOINT, map, null, null, 15000);
            if (respRequest.code == 200 && respRequest.body != null) {
                return new JSONObject(respRequest.body).optString(NotificationCompat.CATEGORY_EMAIL, null);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static void revoke(String str) throws Throwable {
        if (str == null) {
            return;
        }
        Http.request("POST", "https://oauth2.googleapis.com/revoke?token=" + Pkce.url(str), null, new byte[0], "application/x-www-form-urlencoded", 15000);
    }

    private OAuth() {
    }
}
