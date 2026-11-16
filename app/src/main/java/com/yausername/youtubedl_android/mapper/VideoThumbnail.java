package com.yausername.youtubedl_android.mapper;

import androidx.constraintlayout.widget.ConstraintLayout;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.radby.sounds.download.DownloadWorker;
import kotlin.Metadata;

/* JADX INFO: compiled from: VideoThumbnail.kt */
/* JADX INFO: loaded from: classes10.dex */
@JsonIgnoreProperties(ignoreUnknown = true)
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002R\u0013\u0010\u0003\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006¨\u0006\t"}, d2 = {"Lcom/yausername/youtubedl_android/mapper/VideoThumbnail;", "", "()V", "id", "", "getId", "()Ljava/lang/String;", DownloadWorker.KEY_URL, "getUrl", "library_release"}, k = 1, mv = {1, 7, 1})
public final class VideoThumbnail {

    public VideoThumbnail() {
    }
    private String id;
    private String url;

    public final String getUrl() {
        return this.url;
    }

    public final String getId() {
        return this.id;
    }
}
