package me.weishu.kernelsu.ghostlock;

import static me.weishu.kernelsu.ghostlock.AppZygotePreload.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import me.weishu.kernelsu.ui.util.KsuCliKt;

public class GhostlockBootCompletedReceiver extends BroadcastReceiver {
    public static final String ACTION_LAUNCH = "me.weishu.kernelsu.ghostlock.LAUNCH";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        var action = intent.getAction();
        if (!Intent.ACTION_LOCKED_BOOT_COMPLETED.equals(action)
                && !Intent.ACTION_BOOT_COMPLETED.equals(action)
                && !ACTION_LAUNCH.equals(action)) {
            return;
        }
        if (KsuCliKt.rootAvailable()) return;
        try {
            context.startService(new Intent(context, GhostlockService.class));
            Log.i(TAG, "GhostlockService started from boot action: " + action);
        } catch (Throwable e) {
            Log.e(TAG, "Failed to start GhostlockService from boot action: " + action, e);
        }
    }
}
