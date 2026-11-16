package com.radby.sounds.ui.search;

import com.radby.sounds.data.RemoteTrack;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/* JADX INFO: compiled from: SearchFragment.kt */
@Metadata(k = 3, mv = {2, 0, 0})
/* synthetic */ final class SearchFragment$onViewCreated$adapter$2 implements Function1<RemoteTrack, Unit> {
    private final SearchViewModel receiver;

    SearchFragment$onViewCreated$adapter$2(Object obj) {
        this.receiver = (SearchViewModel) obj;
    }

    @Override // kotlin.jvm.functions.Function1
    public Unit invoke(RemoteTrack p0) {
        this.receiver.download(p0);
        return Unit.INSTANCE;
    }
}