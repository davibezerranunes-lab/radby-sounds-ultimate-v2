package com.muzk.addon;

import android.content.Context;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

/* JADX INFO: loaded from: classes13.dex */
public final class Backup {
    private static final String[] ROOTS = {"databases", "shared_prefs", "files"};

    public static File create(Context context) throws IOException {
        File file = new File(context.getCacheDir(), Config.BACKUP_NAME);
        if (file.exists() && !file.delete()) {
            throw new IOException("nao foi possivel recriar o zip");
        }
        String absolutePath = new File(context.getApplicationInfo().dataDir).getAbsolutePath();
        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        try {
            for (String str : ROOTS) {
                File file2 = new File(absolutePath, str);
                if (file2.isDirectory()) {
                    addTree(zipOutputStream, file2, absolutePath);
                }
            }
            zipOutputStream.close();
            return file;
        } catch (Throwable th) {
            zipOutputStream.close();
            throw th;
        }
    }

    private static void addTree(ZipOutputStream zipOutputStream, File file, String str) throws IOException {
        File[] fileArrListFiles;
        String strReplace = file.getAbsolutePath().substring(str.length() + 1).replace(IOUtils.DIR_SEPARATOR_WINDOWS, '/');
        if (file.isDirectory()) {
            if (strReplace.contains("cache") || (fileArrListFiles = file.listFiles()) == null) {
                return;
            }
            for (File file2 : fileArrListFiles) {
                addTree(zipOutputStream, file2, str);
            }
            return;
        }
        if (strReplace.endsWith(".zip") || file.length() == 0) {
            return;
        }
        zipOutputStream.putNextEntry(new ZipEntry(strReplace));
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            byte[] bArr = new byte[16384];
            while (true) {
                int i = fileInputStream.read(bArr);
                if (i > 0) {
                    zipOutputStream.write(bArr, 0, i);
                } else {
                    fileInputStream.close();
                    zipOutputStream.closeEntry();
                    return;
                }
            }
        } catch (Throwable th) {
            fileInputStream.close();
            throw th;
        }
    }

    public static int restore(Context context, byte[] bArr) throws IOException {
        String absolutePath = new File(context.getApplicationInfo().dataDir).getAbsolutePath();
        ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(bArr)));
        try {
            byte[] bArr2 = new byte[16384];
            int i = 0;
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry != null) {
                    String name = nextEntry.getName();
                    if (!nextEntry.isDirectory() && !name.startsWith("/") && !name.contains("..")) {
                        File file = new File(absolutePath, name);
                        File parentFile = file.getParentFile();
                        if (parentFile != null && !parentFile.exists()) {
                            parentFile.mkdirs();
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        while (true) {
                            try {
                                int i2 = zipInputStream.read(bArr2);
                                if (i2 <= 0) {
                                    break;
                                }
                                fileOutputStream.write(bArr2, 0, i2);
                            } catch (Throwable th) {
                                fileOutputStream.close();
                                throw th;
                            }
                        zipInputStream.close();
                        }
                        fileOutputStream.close();
                        i++;
                    }
                } else {
                    zipInputStream.close();
                    return i;
                }
            }
        } catch (Throwable th2) {
            zipInputStream.close();
            throw th2;
        }
    }

    private Backup() {
    }
}
