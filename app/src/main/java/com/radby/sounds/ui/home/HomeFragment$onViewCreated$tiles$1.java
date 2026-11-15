package com.radby.sounds.ui.home;

import androidx.constraintlayout.widget.ConstraintLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: HomeFragment.kt */
/* JADX INFO: loaded from: classes12.dex */
@Metadata(k = 3, mv = {2, 0, 0})
/* synthetic */ class HomeFragment$onViewCreated$tiles$1 implements Function1<HomeTile, Unit> {
    private final HomeFragment receiver;

    HomeFragment$onViewCreated$tiles$1(HomeFragment receiver) {
        this.receiver = receiver;
    }

    @Override // kotlin.jvm.functions.Function1
    public Unit invoke(HomeTile homeTile) {
        Intrinsics.checkNotNullParameter(homeTile, "p0");
        this.receiver.openTile(homeTile);
        return Unit.INSTANCE;
    }
}