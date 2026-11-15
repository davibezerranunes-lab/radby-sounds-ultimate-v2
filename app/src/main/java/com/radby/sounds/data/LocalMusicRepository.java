package com.radby.sounds.data;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.radby.sounds.download.DownloadWorker;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

/* JADX INFO: compiled from: LocalMusicRepository.kt */
/* JADX INFO: loaded from: classes6.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0086@¢\u0006\u0002\u0010\t¨\u0006\n"}, d2 = {"Lcom/muzk/app/data/LocalMusicRepository;", "", "<init>", "()V", "loadTracks", "", "Lcom/muzk/app/data/LocalTrack;", "context", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"}, k = 1, mv = {2, 0, 0})
public final class LocalMusicRepository {
    public static final LocalMusicRepository INSTANCE = new LocalMusicRepository();

    private LocalMusicRepository() {
    }

    /* JADX INFO: renamed from: com.radby.sounds.data.LocalMusicRepository$loadTracks$2, reason: invalid class name */
    /* JADX INFO: compiled from: LocalMusicRepository.kt */
    @Metadata(d1 = {"\u0000\u000e\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001*\u00020\u0003H\n"}, d2 = {"<anonymous>", "", "Lcom/muzk/app/data/LocalTrack;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 0, 0})
    @DebugMetadata(c = "com.radby.sounds.data.LocalMusicRepository$loadTracks$2", f = "LocalMusicRepository.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {})
    static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super List<LocalTrack>>, Object> {
        final /* synthetic */ Context $context;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        AnonymousClass2(Context context, Continuation<? super AnonymousClass2> continuation) {
            super(2, (Continuation<Object>) continuation);
            this.$context = context;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation<Unit>) (Object) new AnonymousClass2(this.$context, (Continuation<Object>) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super List<LocalTrack>> continuation) {
            return ((AnonymousClass2) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Code duplicated, block: B:51:0x0134  */
        /* JADX WARN: Code duplicated, block: B:58:0x0147  */
        /* JADX WARN: Code duplicated, block: B:64:0x0156  */
        /* JADX WARN: Code duplicated, block: B:67:0x0160  */
        /* JADX WARN: Code duplicated, block: B:68:0x0163  */
        /* JADX WARN: Code duplicated, block: B:71:0x017c  */
        /* JADX WARN: Code duplicated, block: B:95:0x0181 A[SYNTHETIC] */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            com.radby.compat.Kt.throwOnFailure(obj);
            if (this.label != 0) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            Uri collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            List<String> projection = new ArrayList<>();
            projection.add("_id");
            projection.add(DownloadWorker.KEY_TITLE);
            projection.add("artist");
            projection.add("album");
            projection.add("duration");
            projection.add("date_added");
            projection.add("_data");
            if (Build.VERSION.SDK_INT >= 29) {
                projection.add("relative_path");
            }
            List<LocalTrack> tracks = new ArrayList<>();
            Cursor c = this.$context.getContentResolver().query(collection, projection.toArray(new String[0]), "is_music != 0", null, "date_added DESC");
            if (c != null) {
                try {
                    int idCol = c.getColumnIndexOrThrow("_id");
                    int titleCol = c.getColumnIndexOrThrow(DownloadWorker.KEY_TITLE);
                    int artistCol = c.getColumnIndexOrThrow("artist");
                    int albumCol = c.getColumnIndexOrThrow("album");
                    int durCol = c.getColumnIndexOrThrow("duration");
                    int dateCol = c.getColumnIndexOrThrow("date_added");
                    int dataCol = c.getColumnIndexOrThrow("_data");
                    int relCol = Build.VERSION.SDK_INT >= 29 ? c.getColumnIndex("relative_path") : -1;
                    while (c.moveToNext()) {
                        long id = c.getLong(idCol);
                        Uri uri = ContentUris.withAppendedId(collection, id);
                        String title = c.getString(titleCol);
                        if (title == null) {
                            title = "Sem titulo";
                        }
                        String artist = c.getString(artistCol);
                        if (artist == null || StringsKt.isBlank(artist) || Intrinsics.areEqual(artist, "<unknown>")) {
                            artist = "Artista desconhecido";
                        }
                        String album = c.getString(albumCol);
                        if (album == null) {
                            album = "";
                        }
                        long dur = c.getLong(durCol);
                        String relPath = relCol >= 0 ? c.getString(relCol) : null;
                        String folderPath;
                        if (relPath != null) {
                            folderPath = relPath.endsWith("/") ? relPath.substring(0, relPath.length() - 1) : relPath;
                        } else {
                            String data = c.getString(dataCol);
                            String parent = data != null ? new File(data).getParent() : null;
                            folderPath = parent == null ? "" : parent;
                        }
                        String folderName = StringsKt.substringAfterLast(folderPath, "/", null);
                        if (StringsKt.isBlank(folderName)) {
                            folderName = "Raiz";
                        }
                        tracks.add(new LocalTrack(id, uri, title, artist, album, dur, folderPath, folderName, c.getLong(dateCol)));
                    }
                } finally {
                    c.close();
                }
            }
            return tracks;
        }
    }

    public final Object loadTracks(Context context, Continuation<? super List<LocalTrack>> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), new AnonymousClass2(context, null), continuation);
    }
}
