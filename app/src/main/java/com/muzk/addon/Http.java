package com.muzk.addon;

import com.google.common.net.HttpHeaders;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import org.apache.commons.compress.utils.CharsetNames;

/* JADX INFO: loaded from: classes13.dex */
public final class Http {

    public static final class Bin {
        public int code;
        public byte[] data;
    }

    public static final class Resp {
        public String body;
        public int code;
        public String error;
    }

    public static Resp request(String str, String str2, Map<String, String> map, byte[] bArr, String str3, int i) throws Throwable {
        Resp resp = new Resp();
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str2).openConnection();
                try {
                    httpURLConnection2.setRequestMethod(str);
                    httpURLConnection2.setConnectTimeout(i);
                    httpURLConnection2.setReadTimeout(i);
                    httpURLConnection2.setInstanceFollowRedirects(false);
                    if (map != null) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            httpURLConnection2.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    if (bArr != null) {
                        httpURLConnection2.setDoOutput(true);
                        if (str3 != null) {
                            httpURLConnection2.setRequestProperty(HttpHeaders.CONTENT_TYPE, str3);
                        }
                        OutputStream outputStream = httpURLConnection2.getOutputStream();
                        outputStream.write(bArr);
                        outputStream.close();
                    }
                    resp.code = httpURLConnection2.getResponseCode();
                    InputStream errorStream = resp.code >= 400 ? httpURLConnection2.getErrorStream() : httpURLConnection2.getInputStream();
                    if (errorStream != null) {
                        resp.body = readAll(errorStream);
                    }
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                } catch (Exception e) {
                    e = e;
                    httpURLConnection = httpURLConnection2;
                    resp.code = -1;
                    resp.error = e.getClass().getSimpleName() + ": " + e.getMessage();
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } catch (Throwable th) {
                    th = th;
                    httpURLConnection = httpURLConnection2;
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
        return resp;
    }

    public static Bin getBytes(String str, Map<String, String> map, int i) throws Throwable {
        Bin bin = new Bin();
        HttpURLConnection httpURLConnection = null;
        try {
            try {
                HttpURLConnection httpURLConnection2 = (HttpURLConnection) new URL(str).openConnection();
                try {
                    httpURLConnection2.setConnectTimeout(i);
                    httpURLConnection2.setReadTimeout(i);
                    httpURLConnection2.setInstanceFollowRedirects(false);
                    if (map != null) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            httpURLConnection2.setRequestProperty(entry.getKey(), entry.getValue());
                        }
                    }
                    bin.code = httpURLConnection2.getResponseCode();
                    InputStream errorStream = bin.code >= 400 ? httpURLConnection2.getErrorStream() : httpURLConnection2.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (errorStream != null) {
                        byte[] bArr = new byte[16384];
                        while (true) {
                            int i2 = errorStream.read(bArr);
                            if (i2 <= 0) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr, 0, i2);
                        }
                        errorStream.close();
                    }
                    bin.data = byteArrayOutputStream.toByteArray();
                    if (httpURLConnection2 != null) {
                        httpURLConnection2.disconnect();
                    }
                } catch (Exception unused) {
                    httpURLConnection = httpURLConnection2;
                    bin.code = -1;
                    bin.data = new byte[0];
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                } catch (Throwable th) {
                    th = th;
                    httpURLConnection = httpURLConnection2;
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                    throw th;
                }
            } catch (Exception unused2) {
            }
            return bin;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    static String readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[8192];
        while (true) {
            int i = inputStream.read(bArr);
            if (i > 0) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                inputStream.close();
                return new String(byteArrayOutputStream.toByteArray(), CharsetNames.UTF_8);
            }
        }
    }

    private Http() {
    }
}
