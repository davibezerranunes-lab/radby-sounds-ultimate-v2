package com.radby.plus;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.List;

/* JADX INFO: loaded from: classes14.dex */
public final class HomeInjector {
    private static boolean installed;

    private HomeInjector() {
    }

    public static void install(Application application) {
        if (application == null || installed) {
            return;
        }
        installed = true;
        application.registerActivityLifecycleCallbacks(new Callbacks());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void injectInto(Activity activity) {
        try {
            Object objInvoke = activity.getClass().getMethod("getSupportFragmentManager", new Class[0]).invoke(activity, new Object[0]);
            if (objInvoke == null) {
                return;
            }
            Object objInvoke2 = objInvoke.getClass().getMethod("getFragments", new Class[0]).invoke(objInvoke, new Object[0]);
            if (!(objInvoke2 instanceof List)) {
                return;
            }
            for (Object obj : (List) objInvoke2) {
                if (obj != null && "com.radby.sounds.ui.home.HomeFragment".equals(obj.getClass().getName())) {
                    Object objInvoke3 = obj.getClass().getMethod("getView", new Class[0]).invoke(obj, new Object[0]);
                    if (objInvoke3 instanceof View) {
                        HomeEntry.attach((View) objInvoke3);
                    } else {
                        continue;
                    }
                }
            }
        } catch (Throwable th) {
        }
    }

    private static final class Callbacks implements Application.ActivityLifecycleCallbacks {
        private final Handler handler;
        private final Runnable task;

        private Callbacks() {
            this.handler = new Handler(Looper.getMainLooper());
            this.task = new Runnable() { // from class: com.radby.plus.HomeInjector.Callbacks.1
                @Override // java.lang.Runnable
                public void run() {
                }
            };
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(final Activity activity) {
            if (!"com.radby.sounds.ui.MainActivity".equals(activity.getClass().getName())) {
                return;
            }
            int[] iArr = {0, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION, 110, 1400, 2400, 4000};
            for (int i = 0; i < 6; i++) {
                this.handler.postDelayed(new Runnable() { // from class: com.radby.plus.HomeInjector.Callbacks.2
                    @Override // java.lang.Runnable
                    public void run() {
                        HomeInjector.injectInto(activity);
                    }
                }, iArr[i]);
            }
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        @Override // android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
