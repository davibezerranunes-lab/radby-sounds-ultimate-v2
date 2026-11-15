package com.muzk.addon;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.common.net.HttpHeaders;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes13.dex */
public final class LyricsRepo {
    private static final String BASE = "https://lrclib.net/api";

    public static String fetch(Context context, String str, String str2, long j) throws Throwable {
        String strPick = null;
        if (str != null && str.trim().length() != 0) {
            StringBuilder sb = new StringBuilder("lrc2_");
            sb.append(Math.abs((str2 + "|" + str).hashCode()));
            String string = sb.toString();
            SharedPreferences sharedPreferences = context.getSharedPreferences(Config.PREFS, 0);
            String string2 = sharedPreferences.getString(string, null);
            if (string2 != null && string2.length() > 0) {
                return string2;
            }
            String strCleanTitle = cleanTitle(str);
            String strCleanArtist = cleanArtist(str2);
            String[] strArrSplitArtistTitle = splitArtistTitle(strCleanTitle);
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            if (strCleanTitle.length() > 0) {
                linkedHashSet.add("get|" + strCleanTitle + "|" + strCleanArtist + "|" + j);
                StringBuilder sb2 = new StringBuilder("get|");
                sb2.append(strCleanTitle);
                sb2.append("|");
                sb2.append(strCleanArtist);
                sb2.append("|0");
                linkedHashSet.add(sb2.toString());
                linkedHashSet.add("search|" + strCleanTitle + " " + strCleanArtist);
                StringBuilder sb3 = new StringBuilder("search|");
                sb3.append(strCleanTitle);
                linkedHashSet.add(sb3.toString());
            }
            if (strArrSplitArtistTitle != null) {
                linkedHashSet.add("get|" + strArrSplitArtistTitle[1] + "|" + strArrSplitArtistTitle[0] + "|" + j);
                StringBuilder sb4 = new StringBuilder("search|");
                sb4.append(strArrSplitArtistTitle[1]);
                sb4.append(" ");
                sb4.append(strArrSplitArtistTitle[0]);
                linkedHashSet.add(sb4.toString());
                linkedHashSet.add("search|" + strArrSplitArtistTitle[1]);
            }
            if (strCleanArtist.length() > 0) {
                linkedHashSet.add("search|" + strCleanArtist);
            }
            Iterator it = linkedHashSet.iterator();
            while (it.hasNext()) {
                String strRequest = request((String) it.next());
                if (strRequest != null) {
                    String strPick2 = pick(strRequest, true);
                    if (strPick2 != null) {
                        sharedPreferences.edit().putString(string, strPick2).apply();
                        return strPick2;
                    }
                    if (strPick == null) {
                        strPick = pick(strRequest, false);
                    }
                }
            }
            if (strPick != null) {
                sharedPreferences.edit().putString(string, strPick).apply();
            }
        }
        return strPick;
    }

    private static String request(String str) throws Throwable {
        String string;
        String[] strArrSplit = str.split("\\|", -1);
        if (strArrSplit[0].equals("get")) {
            string = "https://lrclib.net/api/get?track_name=" + Pkce.url(strArrSplit[1]) + "&artist_name=" + Pkce.url(strArrSplit[2]);
            if (strArrSplit.length > 3 && !strArrSplit[3].equals("0")) {
                string = string + "&duration=" + strArrSplit[3];
            }
        } else {
            StringBuilder sb = new StringBuilder("https://lrclib.net/api/search?q=");
            sb.append(Pkce.url(strArrSplit.length > 1 ? strArrSplit[1] : ""));
            string = sb.toString();
        }
        String str2 = string;
        HashMap map = new HashMap();
        map.put(HttpHeaders.USER_AGENT, Config.UA);
        map.put(HttpHeaders.ACCEPT, "application/json");
        Http.Resp respRequest = Http.request("GET", str2, map, null, null, 15000);
        if (respRequest.code == 200) {
            return respRequest.body;
        }
        return null;
    }

    private static String pick(String str, boolean z) {
        try {
            String strTrim = str.trim();
            if (strTrim.startsWith("[")) {
                JSONArray jSONArray = new JSONArray(strTrim);
                for (int i = 0; i < jSONArray.length(); i++) {
                    String strPickOne = pickOne(jSONArray.getJSONObject(i), z);
                    if (strPickOne != null) {
                        return strPickOne;
                    }
                }
                return null;
            }
            if (strTrim.startsWith("{")) {
                return pickOne(new JSONObject(strTrim), z);
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    private static String pickOne(JSONObject jSONObject, boolean z) {
        String strOptString;
        String strOptString2 = jSONObject.optString("syncedLyrics", null);
        if (z) {
            if (strOptString2 == null || !Lrc.isSynced(strOptString2)) {
                return null;
            }
            return strOptString2;
        }
        if (Lrc.isSynced(strOptString2) || (strOptString = jSONObject.optString("plainLyrics", null)) == null || strOptString.trim().length() <= 0) {
            return null;
        }
        return strOptString;
    }

    public static String cleanTitle(String str) {
        if (str == null) {
            return "";
        }
        int iIndexOf = str.indexOf(" | ");
        if (iIndexOf > 0) {
            str = str.substring(0, iIndexOf);
        }
        return str.replaceAll("\\s*[\\(\\[][^\\)\\]]*[\\)\\]]", " ").replaceAll("(?i)\\b(official|video|videoclip|audio|lyric|lyrics|visualizer|hd|hq|4k|remaster(ed)?|ao vivo|live|explicit|clean|version|música|musica)\\b", " ").replaceAll("\\s+", " ").trim().replaceAll("[\\-–—:|]+$", "").trim();
    }

    public static String cleanArtist(String str) {
        if (str == null) {
            return "";
        }
        int iIndexOf = str.indexOf(" - Topic");
        if (iIndexOf > 0) {
            str = str.substring(0, iIndexOf);
        }
        int iIndexOf2 = str.indexOf(" - ");
        if (iIndexOf2 > 0) {
            str = str.substring(0, iIndexOf2);
        }
        return str.replaceAll("(?i)\\b(vevo|official|topic|records|music)\\b", " ").replaceAll("(?i)feat\\..*$", " ").replaceAll("\\s+", " ").trim();
    }

    private static String[] splitArtistTitle(String str) {
        if (str == null) {
            return null;
        }
        String[] strArr = {" - ", " – ", " — "};
        for (int i = 0; i < 3; i++) {
            String str2 = strArr[i];
            int iIndexOf = str.indexOf(str2);
            if (iIndexOf > 1 && iIndexOf < str.length() - 2) {
                String strTrim = str.substring(0, iIndexOf).trim();
                String strTrim2 = str.substring(iIndexOf + str2.length()).trim();
                if (strTrim.length() > 0 && strTrim2.length() > 0) {
                    return new String[]{strTrim, strTrim2};
                }
            }
        }
        return null;
    }

    private LyricsRepo() {
    }
}
