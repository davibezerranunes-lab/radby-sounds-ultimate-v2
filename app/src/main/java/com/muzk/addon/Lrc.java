package com.muzk.addon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.compress.archivers.tar.TarConstants;

/* JADX INFO: loaded from: classes13.dex */
public final class Lrc {
    private static final Pattern STAMP = Pattern.compile("\\[(\\d{1,2}):(\\d{1,2})(?:[.:](\\d{1,3}))?\\]");

    public static final class Line {
        public final String text;
        public final long timeMs;

        Line(long j, String str) {
            this.timeMs = j;
            this.text = str;
        }
    }

    public static List<Line> parse(String str) {
        long j;
        long j2;
        ArrayList arrayList = new ArrayList();
        if (str == null) {
            return arrayList;
        }
        Matcher matcher = Pattern.compile("\\[offset:\\s*([+-]?\\d+)\\]").matcher(str);
        int i = 1;
        if (matcher.find()) {
            try {
                j = Long.parseLong(matcher.group(1));
            } catch (Exception unused) {
                j = 0;
            }
        } else {
            j = 0;
        }
        String[] strArrSplit = str.split("\\r?\\n");
        int length = strArrSplit.length;
        int i2 = 0;
        while (i2 < length) {
            String str2 = strArrSplit[i2];
            Matcher matcher2 = STAMP.matcher(str2);
            ArrayList arrayList2 = new ArrayList();
            int iEnd = 0;
            while (matcher2.find()) {
                long j3 = Long.parseLong(matcher2.group(i));
                long j4 = Long.parseLong(matcher2.group(2));
                String strGroup = matcher2.group(3);
                if (strGroup != null) {
                    if (strGroup.length() != 3) {
                        strGroup = (strGroup + TarConstants.VERSION_POSIX).substring(0, 3);
                    }
                    j2 = Long.parseLong(strGroup);
                } else {
                    j2 = 0;
                }
                arrayList2.add(Long.valueOf((((j3 * 60) + j4) * 1000) + j2 + j));
                iEnd = matcher2.end();
                i = 1;
            }
            if (!arrayList2.isEmpty()) {
                String strTrim = str2.substring(iEnd).trim();
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    arrayList.add(new Line(Math.max(0L, ((Long) it.next()).longValue()), strTrim));
                }
            }
            i2++;
            i = 1;
        }
        Collections.sort(arrayList, new Comparator<Line>() { // from class: com.muzk.addon.Lrc.1
            @Override // java.util.Comparator
            public int compare(Line line, Line line2) {
                if (line.timeMs < line2.timeMs) {
                    return -1;
                }
                return line.timeMs > line2.timeMs ? 1 : 0;
            }
        });
        return arrayList;
    }

    public static boolean isSynced(String str) {
        return str != null && STAMP.matcher(str).find();
    }

    private Lrc() {
    }
}
