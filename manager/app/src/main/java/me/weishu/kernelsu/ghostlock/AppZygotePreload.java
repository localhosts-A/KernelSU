package me.weishu.kernelsu.ghostlock;

import android.app.ZygotePreload;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Arrays;

public class AppZygotePreload implements ZygotePreload {
    public static final String TAG = "KernelSUGhostlock";

    private static native boolean setEnv(String name, String value);

    @Override
    public void doPreload(@NonNull ApplicationInfo appInfo) {
        try {
            System.loadLibrary("kernelsu");

            String library = selectGhostlockLibrary();
            if (library == null) {
                Log.i(TAG, "unsupported target fingerprint: " + Build.FINGERPRINT);
                return;
            }

            File ksud = new File(appInfo.nativeLibraryDir, "libksud.so");
            setEnv("KSUD_PATH", ksud.getAbsolutePath());
            setEnv("KSUD_PACKAGE", appInfo.packageName);
            setEnv("KSUD_ALLOW_SHELL", "1");

            Log.d(TAG, "executing ghostlock target " + library + " ...");
            System.loadLibrary(library);
        } catch (Throwable t) {
            Log.e(TAG, "failed to run ghostlock", t);
        }
    }

    private static String selectGhostlockLibrary() {
        if (!Arrays.asList(Build.SUPPORTED_ABIS).contains("arm64-v8a")) {
            return null;
        }

        String fingerprint = Build.FINGERPRINT == null ? "" : Build.FINGERPRINT;
        if (fingerprint.contains("AP3A")) {
            return "ghostlock_dali_ap3a";
        }
        if (fingerprint.contains("BP2A")) {
            return "ghostlock_dali_bp2a";
        }
        return null;
    }
}
