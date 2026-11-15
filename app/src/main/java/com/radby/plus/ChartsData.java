package com.radby.plus;

import android.content.Context;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.media3.extractor.metadata.dvbsi.AppInfoTableDecoder;
import com.google.common.net.HttpHeaders;
import com.radby.sounds.download.DownloadWorker;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.CharsetNames;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes14.dex */
public final class ChartsData {
    private static final long TTL_MS = 10800000;
    private static final String UA = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120 Mobile Safari/537.36";

    public static final class Track {
        public final String art;
        public final String artist;
        public final String query;
        public final String title;

        Track(String str, String str2, String str3) {
            this.title = str == null ? "" : str.trim();
            this.artist = str2 != null ? str2.trim() : "";
            this.art = str3;
            String strTrim = (this.artist + " " + this.title).trim();
            this.query = strTrim.length() == 0 ? this.title : strTrim;
        }
    }

    private ChartsData() {
    }

    public static String deviceCountry(Context context) {
        String country;
        try {
            country = Locale.getDefault().getCountry();
        } catch (Throwable th) {
            country = "";
        }
        if (country == null || country.length() != 2) {
            try {
                Object systemService = context.getSystemService("phone");
                Object objInvoke = systemService.getClass().getMethod("getNetworkCountryIso", new Class[0]).invoke(systemService, new Object[0]);
                if (objInvoke instanceof String) {
                    country = (String) objInvoke;
                }
            } catch (Throwable th2) {
            }
        }
        if (country == null || country.length() != 2) {
            country = "BR";
        }
        return country.toLowerCase(Locale.US);
    }

    public static List<Track> country(Context context, String str) throws Exception {
        String str2 = "country-" + str;
        List<Track> listCached = cached(context, str2);
        if (listCached != null) {
            return listCached;
        }
        List<Track> listApple = apple(context, str, 50, "apple-" + str);
        if (listApple.isEmpty()) {
            throw new IllegalStateException("parada indisponivel para " + str);
        }
        store(context, str2, listApple);
        return listApple;
    }

    public static List<Track> world(Context context) throws Exception {
        List<Track> listCached = cached(context, "world");
        if (listCached != null) {
            return listCached;
        }
        String[] strArr = {"us", "gb", "br", "mx", ArchiveStreamFactory.AR, "fr", "de", "es", "it", "pt", "ca", "au"};
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        LinkedHashMap linkedHashMap2 = new LinkedHashMap();
        for (int i = 0; i < 12; i++) {
            String str = strArr[i];
            try {
                List<Track> listApple = apple(context, str, 25, "apple-" + str);
                for (int i2 = 0; i2 < listApple.size(); i2++) {
                    Track track = listApple.get(i2);
                    String lowerCase = (track.artist + "|" + track.title).toLowerCase(Locale.US);
                    Double d = (Double) linkedHashMap.get(lowerCase);
                    linkedHashMap.put(lowerCase, Double.valueOf((d == null ? 0.0d : d.doubleValue()) + (1.0d / ((double) (i2 + 6)))));
                    if (!linkedHashMap2.containsKey(lowerCase)) {
                        linkedHashMap2.put(lowerCase, track);
                    }
                }
                if (linkedHashMap2.size() > 400) {
                    break;
                }
            } catch (Exception e) {
            }
        }
        ArrayList arrayList = new ArrayList(linkedHashMap.entrySet());
        Collections.sort(arrayList, new Comparator<Map.Entry<String, Double>>() { // from class: com.radby.plus.ChartsData.1
            @Override // java.util.Comparator
            public int compare(Map.Entry<String, Double> entry, Map.Entry<String, Double> entry2) {
                return entry2.getValue().compareTo(entry.getValue());
            }
        });
        ArrayList arrayList2 = new ArrayList();
        for (int i3 = 0; i3 < arrayList.size() && arrayList2.size() < 50; i3++) {
            Track track2 = (Track) linkedHashMap2.get(((Map.Entry) arrayList.get(i3)).getKey());
            if (track2 != null) {
                arrayList2.add(track2);
            }
        }
        if (arrayList2.isEmpty()) {
            throw new IllegalStateException("sem dados do mundo agora");
        }
        store(context, "world", arrayList2);
        return arrayList2;
    }

    public static List<Track> region(Context context, String str) throws Exception {
        boolean z;
        String str2 = "region-" + str;
        List<Track> listCached = cached(context, str2);
        if (listCached != null) {
            return listCached;
        }
        int[] iArrGenresFor = genresFor(str);
        ArrayList<List> arrayList = new ArrayList();
        for (int i : iArrGenresFor) {
            try {
                arrayList.add(deezer(context, i, 20));
            } catch (Exception e) {
            }
        }
        List<Track> arrayList2 = new ArrayList<>();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int i2 = 0;
        for (boolean z2 = true; arrayList2.size() < 50 && z2; z2 = z) {
            z = false;
            for (List list : arrayList) {
                if (i2 < list.size()) {
                    Track track = (Track) list.get(i2);
                    String lowerCase = (track.artist + "|" + track.title).toLowerCase(Locale.US);
                    if (!linkedHashMap.containsKey(lowerCase)) {
                        linkedHashMap.put(lowerCase, Boolean.TRUE);
                        arrayList2.add(track);
                        z = true;
                    }
                }
            }
            i2++;
            if (i2 > 60) {
                break;
            }
        }
        if (arrayList2.isEmpty()) {
            arrayList2 = country(context, str);
        }
        store(context, str2, arrayList2);
        return arrayList2;
    }

    private static int[] genresFor(String str) {
        if ("br".equals(str)) {
            return new int[]{80, 472, 79, 76, 78};
        }
        if ("us".equals(str) || "gb".equals(str) || "ca".equals(str) || "au".equals(str)) {
            return new int[]{AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, 132, 165, 152, 113};
        }
        if ("mx".equals(str) || ArchiveStreamFactory.AR.equals(str) || "es".equals(str)) {
            return new int[]{122, 132, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, 113};
        }
        return new int[]{132, AppInfoTableDecoder.APPLICATION_INFORMATION_TABLE_ID, 152, 113};
    }

    private static List<Track> apple(Context context, String str, int i, String str2) throws Exception {
        List<Track> listCached = cached(context, str2);
        if (listCached != null) {
            return listCached;
        }
        JSONArray jSONArray = new JSONObject(fetch("https://rss.applemarketingtools.com/api/v2/" + str + "/music/most-played/" + i + "/songs.json")).getJSONObject("feed").getJSONArray("results");
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i2);
            String strOptString = jSONObject.optString("artworkUrl100", null);
            if (strOptString != null) {
                strOptString = strOptString.replace("100x100bb", "300x300bb");
            }
            arrayList.add(new Track(jSONObject.optString("name"), jSONObject.optString("artistName"), strOptString));
        }
        if (!arrayList.isEmpty()) {
            store(context, str2, arrayList);
        }
        return arrayList;
    }

    private static List<Track> deezer(Context context, int i, int i2) throws Exception {
        String str = "deezer-" + i;
        List<Track> listCached = cached(context, str);
        if (listCached != null) {
            return listCached;
        }
        JSONArray jSONArrayOptJSONArray = new JSONObject(fetch("https://api.deezer.com/chart/" + i + "/tracks?limit=" + i2)).optJSONArray("data");
        ArrayList arrayList = new ArrayList();
        if (jSONArrayOptJSONArray != null) {
            for (int i3 = 0; i3 < jSONArrayOptJSONArray.length(); i3++) {
                JSONObject jSONObject = jSONArrayOptJSONArray.getJSONObject(i3);
                JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("artist");
                JSONObject jSONObjectOptJSONObject2 = jSONObject.optJSONObject("album");
                arrayList.add(new Track(jSONObject.optString(DownloadWorker.KEY_TITLE), jSONObjectOptJSONObject == null ? "" : jSONObjectOptJSONObject.optString("name"), jSONObjectOptJSONObject2 != null ? jSONObjectOptJSONObject2.optString("cover_medium", null) : null));
            }
        }
        if (!arrayList.isEmpty()) {
            store(context, str, arrayList);
        }
        return arrayList;
    }

    private static String fetch(String str) throws Exception {
        Throwable th;
        HttpURLConnection httpURLConnection;
        try {
            httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            try {
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(AccessibilityNodeInfoCompat.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_MAX_LENGTH);
                httpURLConnection.setRequestProperty(HttpHeaders.USER_AGENT, UA);
                httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT, "application/json");
                httpURLConnection.setRequestProperty(HttpHeaders.ACCEPT_ENCODING, "identity");
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode != 200) {
                    throw new IllegalStateException("HTTP " + responseCode);
                }
                String all = readAll(httpURLConnection.getInputStream());
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                return all;
            } catch (Throwable th2) {
                th = th2;
                if (httpURLConnection != null) {
                    httpURLConnection.disconnect();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            httpURLConnection = null;
        }
        return null;
    }

    private static String readAll(InputStream inputStream) throws Exception {
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

    private static List<Track> cached(Context context, String str) {
        try {
            File fileCacheFile = cacheFile(context, str);
            if (!fileCacheFile.isFile() || System.currentTimeMillis() - fileCacheFile.lastModified() > TTL_MS) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(fileCacheFile), CharsetNames.UTF_8));
            String line = bufferedReader.readLine();
            bufferedReader.close();
            if (line != null && line.length() != 0) {
                ArrayList arrayList = new ArrayList();
                for (String str2 : line.split(IOUtils.LINE_SEPARATOR_UNIX)) {
                    if (str2.length() != 0) {
                        String[] strArrSplit = str2.split("\t", -1);
                        if (strArrSplit.length >= 2) {
                            arrayList.add(new Track(strArrSplit[0], strArrSplit[1], (strArrSplit.length <= 2 || strArrSplit[2].length() <= 0) ? null : strArrSplit[2]));
                        }
                    }
                }
                if (arrayList.isEmpty()) {
                    return null;
                }
                return arrayList;
            }
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    private static void store(Context context, String str, List<Track> list) {
        try {
            StringBuilder sb = new StringBuilder();
            for (Track track : list) {
                sb.append(clean(track.title)).append('\t').append(clean(track.artist)).append('\t').append(clean(track.art)).append('\n');
            }
            File fileCacheFile = cacheFile(context, str);
            File file = new File(fileCacheFile.getAbsolutePath() + ".tmp");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(sb.toString().getBytes(CharsetNames.UTF_8));
            fileOutputStream.close();
            file.renameTo(fileCacheFile);
        } catch (Throwable th) {
        }
    }

    private static String clean(String str) {
        if (str == null) {
            return "";
        }
        return str.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private static File cacheFile(Context context, String str) {
        File file = new File(context.getCacheDir(), "radby-charts");
        if (!file.isDirectory()) {
            file.mkdirs();
        }
        return new File(file, str + ".tsv");
    }
}
