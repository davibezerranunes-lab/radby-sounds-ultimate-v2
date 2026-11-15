package com.muzk.addon;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

/* JADX INFO: loaded from: classes13.dex */
public final class VideoFinder {
    public static void ensurePermission(Activity activity) {
        String str;
        if (Build.VERSION.SDK_INT >= 33) {
            str = "android.permission.READ_MEDIA_VIDEO";
        } else {
            str = "android.permission.READ_EXTERNAL_STORAGE";
        }
        try {
            if (activity.checkSelfPermission(str) != 0) {
                activity.requestPermissions(new String[]{str}, 1001);
            }
        } catch (Throwable unused) {
        }
    }

    public static Uri find(Context context, String str, String str2) {
        Uri uriQuery = query(context, str);
        if (uriQuery == null) {
            uriQuery = query(context, strip(str));
        }
        return (uriQuery != null || str2 == null || str2.length() <= 0) ? uriQuery : query(context, str2);
    }

    private static String strip(String str) {
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf(40);
        if (iIndexOf < 0) {
            iIndexOf = str.indexOf(91);
        }
        if (iIndexOf > 0) {
            str = str.substring(0, iIndexOf);
        }
        return str.trim();
    }

    private static Uri query(Context context, String str) {
        if (str != null && str.trim().length() != 0) {
            try {
                Cursor cursorQuery = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, "_display_name LIKE ?", new String[]{"%" + str.trim() + "%"}, null);
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.moveToFirst()) {
                            return Uri.withAppendedPath(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, String.valueOf(cursorQuery.getLong(0)));
                        }
                    } finally {
                        cursorQuery.close();
                    }
                }
            } catch (Throwable unused) {
            }
        }
        return null;
    }

    private VideoFinder() {
    }
}
