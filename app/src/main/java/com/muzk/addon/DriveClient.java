package com.muzk.addon;

import android.content.Context;
import androidx.media3.extractor.text.ttml.TtmlNode;
import com.google.common.net.HttpHeaders;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import org.apache.commons.compress.utils.CharsetNames;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes13.dex */
public final class DriveClient {
    private static final String FILES = "https://www.googleapis.com/drive/v3/files";
    private static final String UPLOAD = "https://www.googleapis.com/upload/drive/v3/files";

    public static String find(Context context, String str, String str2) {
        JSONArray jSONArrayOptJSONArray;
        try {
            Http.Resp resp = get("https://www.googleapis.com/drive/v3/files?spaces=appDataFolder&fields=files(id,name,modifiedTime,size)&q=" + Pkce.url("name='" + str2 + "' and trashed=false"), str);
            if (resp.code == 200 && resp.body != null && (jSONArrayOptJSONArray = new JSONObject(resp.body).optJSONArray("files")) != null && jSONArrayOptJSONArray.length() > 0) {
                return jSONArrayOptJSONArray.getJSONObject(0).optString(TtmlNode.ATTR_ID, null);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    public static String upload(Context context, String str, File file, String str2) throws Exception {
        byte[] all = readAll(file);
        if (str2 != null) {
            Http.Resp respRequest = request("PATCH", "https://www.googleapis.com/upload/drive/v3/files/" + str2 + "?uploadType=media", str, all, "application/octet-stream");
            if (respRequest.code >= 200 && respRequest.code < 300) {
                return str2;
            }
            throw new Exception("upload http " + respRequest.code);
        }
        String str3 = "muzk" + System.currentTimeMillis();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(("--" + str3 + "\r\nContent-Type: application/json; charset=UTF-8\r\n\r\n{\"name\":\"muzk-backup.zip\",\"parents\":[\"appDataFolder\"]}\r\n").getBytes(CharsetNames.UTF_8));
        byteArrayOutputStream.write(("--" + str3 + "\r\nContent-Type: application/octet-stream\r\n\r\n").getBytes(CharsetNames.UTF_8));
        byteArrayOutputStream.write(all);
        byteArrayOutputStream.write(("\r\n--" + str3 + "--\r\n").getBytes(CharsetNames.UTF_8));
        Http.Resp respRequest2 = request("POST", "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart", str, byteArrayOutputStream.toByteArray(), "multipart/related; boundary=" + str3);
        if (respRequest2.code < 200 || respRequest2.code >= 300) {
            throw new Exception("upload http " + respRequest2.code + " " + respRequest2.body);
        }
        try {
            return new JSONObject(respRequest2.body).optString(TtmlNode.ATTR_ID, null);
        } catch (Exception unused) {
            return null;
        }
    }

    public static byte[] download(String str, String str2) throws Exception {
        HashMap map = new HashMap();
        map.put(HttpHeaders.AUTHORIZATION, "Bearer " + str);
        Http.Bin bytes = Http.getBytes("https://www.googleapis.com/drive/v3/files/" + str2 + "?alt=media", map, 60000);
        if (bytes.code != 200) {
            throw new Exception("download http " + bytes.code);
        }
        return bytes.data;
    }

    public static void delete(String str, String str2) {
        if (str2 != null) {
            request("DELETE", "https://www.googleapis.com/drive/v3/files/" + str2, str, null, null);
        }
    }

    private static Http.Resp get(String str, String str2) {
        HashMap map = new HashMap();
        map.put(HttpHeaders.AUTHORIZATION, "Bearer " + str2);
        return Http.request("GET", str, map, null, null, 30000);
    }

    private static Http.Resp request(String str, String str2, String str3, byte[] bArr, String str4) {
        HashMap map = new HashMap();
        map.put(HttpHeaders.AUTHORIZATION, "Bearer " + str3);
        return Http.request(str, str2, map, bArr, str4, 60000);
    }

    private static byte[] readAll(File file) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bArr = new byte[16384];
        while (true) {
            int i = fileInputStream.read(bArr);
            if (i > 0) {
                byteArrayOutputStream.write(bArr, 0, i);
            } else {
                fileInputStream.close();
                return byteArrayOutputStream.toByteArray();
            }
        }
    }

    private DriveClient() {
    }
}
