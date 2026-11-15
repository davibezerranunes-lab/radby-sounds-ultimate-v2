package com.muzk.addon;

import android.util.Base64;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.SecureRandom;
import org.apache.commons.compress.utils.CharsetNames;

/* JADX INFO: loaded from: classes13.dex */
public final class Pkce {
    public static String verifier() {
        byte[] bArr = new byte[32];
        new SecureRandom().nextBytes(bArr);
        return b64(bArr);
    }

    public static String challenge(String str) {
        try {
            return b64(MessageDigest.getInstance("SHA-256").digest(str.getBytes(CharsetNames.US_ASCII)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String state() {
        byte[] bArr = new byte[16];
        new SecureRandom().nextBytes(bArr);
        return b64(bArr);
    }

    static String b64(byte[] bArr) {
        return Base64.encodeToString(bArr, 11);
    }

    public static String url(String str) {
        try {
            return URLEncoder.encode(str, CharsetNames.UTF_8);
        } catch (Exception unused) {
            return str;
        }
    }

    private Pkce() {
    }
}
