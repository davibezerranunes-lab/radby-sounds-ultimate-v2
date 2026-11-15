package com.radby.sounds.download;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media3.common.MimeTypes;
import androidx.preference.PreferenceManager;
import androidx.work.CoroutineWorker;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.ListenableWorker;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;
import com.radby.sounds.MuzkApp;
import com.radby.sounds.R;
import com.yausername.youtubedl_android.YoutubeDL;
import com.yausername.youtubedl_android.YoutubeDLRequest;
import com.yausername.youtubedl_android.YoutubeDLResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.InterruptibleKt;

/* JADX INFO: compiled from: DownloadWorker.kt */
/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u0000 $2\u00020\u0001:\u0001$B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000e\u0010\u0010\u001a\u00020\u0011H\u0096@¢\u0006\u0002\u0010\u0012J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\tH\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0016H\u0002J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\tH\u0002J$\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\t2\u0006\u0010!\u001a\u00020\"2\n\b\u0002\u0010#\u001a\u0004\u0018\u00010\tH\u0003R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006%"}, d2 = {"Lcom/muzk/app/download/DownloadWorker;", "Landroidx/work/CoroutineWorker;", "context", "Landroid/content/Context;", "params", "Landroidx/work/WorkerParameters;", "<init>", "(Landroid/content/Context;Landroidx/work/WorkerParameters;)V", DownloadWorker.KEY_URL, "", DownloadWorker.KEY_TITLE, "notificationId", "", "processId", "lastNotified", "", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "buildRequest", "Lcom/yausername/youtubedl_android/YoutubeDLRequest;", "dir", "Ljava/io/File;", "format", "publishToMusic", "", "file", "foregroundInfo", "Landroidx/work/ForegroundInfo;", "progress", "text", "notifyDone", "name", "success", "", DownloadWorker.KEY_ERROR, "Companion", "app_debug"}, k = 1, mv = {2, 0, 0})
public final class DownloadWorker extends CoroutineWorker {
    public static final String FOLDER = "Muzk";
    public static final String FORMAT_MP3 = "mp3";
    public static final String KEY_ERROR = "error";
    public static final String KEY_NOTIFICATION_ID = "notification_id";
    public static final String KEY_PROGRESS = "progress";
    public static final String KEY_TITLE = "title";
    public static final String KEY_URL = "url";
    public static final String PREF_FORMAT = "audio_format";
    private long lastNotified;
    private final int notificationId;
    private final String processId;
    private final String title;
    private final String url;


    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DownloadWorker(Context context, WorkerParameters params) {
        super(context, params);
        Intrinsics.checkNotNullParameter(context, "context");
        Intrinsics.checkNotNullParameter(params, "params");
        String string = getInputData().getString(KEY_URL);
        this.url = string == null ? "" : string;
        String string2 = getInputData().getString(KEY_TITLE);
        this.title = string2 != null ? string2 : "";
        this.notificationId = getInputData().getInt(KEY_NOTIFICATION_ID, this.url.hashCode());
        String string3 = getId().toString();
        Intrinsics.checkNotNullExpressionValue(string3, "toString(...)");
        this.processId = string3;
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0014  */
    @Override // androidx.work.CoroutineWorker
    public Object doWork(Continuation<? super ListenableWorker.Result> continuation) {
        Context ctx = getApplicationContext();
        File workDir = new File(ctx.getCacheDir(), "muzk_dl");
        if (!workDir.exists() && !workDir.mkdirs()) {
            notifyDone(this.title, false, "pasta temporaria indisponivel");
            return ListenableWorker.Result.failure();
        }
        String format = PreferenceManager.getDefaultSharedPreferences(ctx).getString(PREF_FORMAT, FORMAT_MP3);
        if (format == null) {
            format = FORMAT_MP3;
        }
        try {
            YoutubeDL.getInstance().execute(buildRequest(workDir, format), this.processId, (Function3<? super Float, ? super Long, ? super String, Unit>) null);
            File downloaded = newestAudio(workDir);
            if (downloaded == null) {
                notifyDone(this.title, false, "arquivo baixado nao encontrado");
                return ListenableWorker.Result.failure();
            }
            publishToMusic(downloaded);
            notifyDone(downloaded.getName(), true, null);
            return ListenableWorker.Result.success();
        } catch (Throwable th) {
            notifyDone(this.title, false, th.getMessage());
            return ListenableWorker.Result.failure();
        }
    }

    private static File newestAudio(File dir) {
        File[] files = dir.listFiles();
        if (files == null) {
            return null;
        }
        File best = null;
        long bestTime = -1;
        for (File f : files) {
            String n = f.getName().toLowerCase(Locale.ROOT);
            if (f.isFile() && (n.endsWith(".mp3") || n.endsWith(".m4a") || n.endsWith(".opus") || n.endsWith(".ogg") || n.endsWith(".webm"))) {
                if (f.lastModified() > bestTime) {
                    bestTime = f.lastModified();
                    best = f;
                }
            }
        }
        return best;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final YoutubeDLRequest buildRequest(File dir, String format) {
        YoutubeDLRequest $this$buildRequest_u24lambda_u240 = new YoutubeDLRequest(this.url);
        $this$buildRequest_u24lambda_u240.addOption("--no-playlist");
        $this$buildRequest_u24lambda_u240.addOption("--no-mtime");
        $this$buildRequest_u24lambda_u240.addOption("--no-warnings");
        $this$buildRequest_u24lambda_u240.addOption("-f", "bestaudio/best");
        $this$buildRequest_u24lambda_u240.addOption("-x");
        if (Intrinsics.areEqual(format, FORMAT_MP3)) {
            $this$buildRequest_u24lambda_u240.addOption("--audio-format", FORMAT_MP3);
            $this$buildRequest_u24lambda_u240.addOption("--audio-quality", "320K");
        } else {
            $this$buildRequest_u24lambda_u240.addOption("--audio-format", "m4a");
            $this$buildRequest_u24lambda_u240.addOption("--audio-quality", "0");
        }
        $this$buildRequest_u24lambda_u240.addOption("--embed-thumbnail");
        $this$buildRequest_u24lambda_u240.addOption("--convert-thumbnails", "jpg");
        $this$buildRequest_u24lambda_u240.addOption("--ppa", "ThumbnailsConvertor:-qmin 1 -q:v 1 -vf crop=\"'if(gt(ih,iw),iw,ih)':'if(gt(iw,ih),ih,iw)'\"");
        $this$buildRequest_u24lambda_u240.addOption("--embed-metadata");
        $this$buildRequest_u24lambda_u240.addOption("--parse-metadata", "%(artist,creator,uploader,channel)s:%(meta_artist)s");
        $this$buildRequest_u24lambda_u240.addOption("--parse-metadata", "%(track,title)s:%(meta_title)s");
        $this$buildRequest_u24lambda_u240.addOption("-o", dir.getAbsolutePath() + "/%(track,title)s.%(ext)s");
        $this$buildRequest_u24lambda_u240.addOption("--windows-filenames");
        return $this$buildRequest_u24lambda_u240;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:11:0x0029  */
    /* JADX WARN: Code duplicated, block: B:20:0x0044  */
    public final void publishToMusic(File file) throws Exception {
        String str;
        String lowerCase = FilesKt.getExtension(file).toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        switch (lowerCase) {
            case "m4a":
                str = MimeTypes.AUDIO_MP4;
                break;
            case "mp3":
                str = MimeTypes.AUDIO_MPEG;
                break;
            case "ogg":
            case "opus":
                str = MimeTypes.AUDIO_OGG;
                break;
            default:
                str = "audio/*";
                break;
        }
        String mime = str;
        if (Build.VERSION.SDK_INT < 29) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Radby Sonds");
            dir.mkdirs();
            File target = new File(dir, file.getName());
            int n = 1;
            while (target.exists()) {
                File file2 = new File(dir, FilesKt.getNameWithoutExtension(file) + " (" + n + ")." + FilesKt.getExtension(file));
                n++;
                target = file2;
            }
            FilesKt.copyTo(file, target, false, 0);
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{target.getAbsolutePath()}, new String[]{mime}, null);
            return;
        }
        ContentResolver resolver = getApplicationContext().getContentResolver();
        ContentValues $this$publishToMusic_u24lambda_u241 = new ContentValues();
        $this$publishToMusic_u24lambda_u241.put("_display_name", file.getName());
        $this$publishToMusic_u24lambda_u241.put("mime_type", mime);
        $this$publishToMusic_u24lambda_u241.put("relative_path", Environment.DIRECTORY_MUSIC + "/Radby Sonds");
        $this$publishToMusic_u24lambda_u241.put("is_music", (Integer) 1);
        $this$publishToMusic_u24lambda_u241.put("is_pending", (Integer) 1);
        Uri collection = MediaStore.Audio.Media.getContentUri("external_primary");
        Uri uri = resolver.insert(collection, $this$publishToMusic_u24lambda_u241);
        if (uri == null) {
            throw new IllegalStateException("Não consegui criar o arquivo em Música".toString());
        }
        try {
            OutputStream outputStreamOpenOutputStream = resolver.openOutputStream(uri);
            if (outputStreamOpenOutputStream == null) {
                throw new IllegalStateException("Nao consegui gravar em Musica");
            }
            try (FileInputStream in = new FileInputStream(file)) {
                ByteStreamsKt.copyTo(in, outputStreamOpenOutputStream, 0);
            } finally {
                outputStreamOpenOutputStream.close();
            }
            $this$publishToMusic_u24lambda_u241.clear();
            $this$publishToMusic_u24lambda_u241.put("is_pending", (Integer) 0);
            resolver.update(uri, $this$publishToMusic_u24lambda_u241, null, null);
        } catch (Exception e) {
            resolver.delete(uri, null, null);
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final ForegroundInfo foregroundInfo(int progress, String text) {
        boolean z;
        Context applicationContext = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(applicationContext, "getApplicationContext(...)");
        WorkManager workManager = WorkManager.getInstance(applicationContext);
        UUID id = getId();
        Intrinsics.checkNotNullExpressionValue(id, "getId(...)");
        PendingIntent cancel = workManager.createCancelPendingIntent(id);
        NotificationCompat.Builder smallIcon = new NotificationCompat.Builder(getApplicationContext(), MuzkApp.CHANNEL_DOWNLOADS).setSmallIcon(R.drawable.ic_download);
        String str = this.title;
        if (StringsKt.isBlank(str)) {
            String string = getApplicationContext().getString(R.string.dl_downloading);
            Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
            str = string;
        }
        NotificationCompat.Builder ongoing = smallIcon.setContentTitle(str).setContentText(text).setOnlyAlertOnce(true).setOngoing(true);
        int iCoerceAtLeast = RangesKt.coerceAtLeast(progress, 0);
        if (progress < 0) {
            z = true;
        } else {
            z = false;
        }
        Notification notification = ongoing.setProgress(100, iCoerceAtLeast, z).addAction(0, getApplicationContext().getString(R.string.cancel), cancel).build();
        Intrinsics.checkNotNullExpressionValue(notification, "build(...)");
        if (Build.VERSION.SDK_INT >= 29) {
            return new ForegroundInfo(this.notificationId, notification, 1);
        }
        return new ForegroundInfo(this.notificationId, notification);
    }

    static /* synthetic */ void notifyDone$default(DownloadWorker downloadWorker, String str, boolean z, String str2, int i, Object obj) {
        if ((i & 4) != 0) {
            str2 = null;
        }
        downloadWorker.notifyDone(str, z, str2);
    }

    public final void notifyDone(String name, boolean success, String error) {
        Context ctx = getApplicationContext();
        Intrinsics.checkNotNullExpressionValue(ctx, "getApplicationContext(...)");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, MuzkApp.CHANNEL_DOWNLOADS)
                .setSmallIcon(success ? R.drawable.ic_check : R.drawable.ic_error)
                .setContentTitle(ctx.getString(success ? R.string.dl_done : R.string.dl_failed))
                .setAutoCancel(true);
        String text = success ? name : name + ": " + StringsKt.take(error == null ? "" : error, 120);
        Notification notification = builder.setContentText(text).build();
        Intrinsics.checkNotNullExpressionValue(notification, "build(...)");
        try {
            NotificationManagerCompat.from(ctx).notify(this.notificationId + 1, notification);
        } catch (Throwable th) {
        }
    }
}
