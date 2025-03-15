package com.norman.webviewup.lib.api35;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.AppComponentFactory;

import com.norman.webviewup.lib.hook.PackageManagerServiceHook;

/**
 * @author Created by Ak on 2025-02-27 22:12.
 */
@SuppressLint("LongLogTag")
@RequiresApi(api = Build.VERSION_CODES.P)
public class SandboxAppComponentFactory extends AppComponentFactory {
    private static final String TAG = "SandboxAppComponentFactory";

    @NonNull
    @Override
    public ClassLoader instantiateClassLoader(@NonNull ClassLoader cl, @NonNull ApplicationInfo aInfo) {
        Log.i(TAG, "【instantiateClassLoader】");
        return super.instantiateClassLoader(cl, aInfo);
    }

    @NonNull
    @Override
    public Application instantiateApplicationCompat(@NonNull ClassLoader cl, @NonNull String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Log.i(TAG, "【instantiateApplicationCompat】");
        return super.instantiateApplicationCompat(cl, className);
    }

    @NonNull
    @Override
    public Service instantiateServiceCompat(@NonNull ClassLoader cl, @NonNull String className, @Nullable Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // 此处被webView启动的新进程中调用
        // 这里打印 PluginDexMergeManager.isInstalled() 为 false (实际已经是true的)
        Log.i(TAG, "instantiateServiceCompat: className = " + className + " " + PluginDexMergeManager.isInstalled());
        // TODO: 2025-03-15 访问不到
        if (StubService.NAME.equals(className) && PluginDexMergeManager.isInstalled()) {
            // 取出原始的Service
            String rawServiceName = MockClass1.getRawServiceName(StubService.NAME);
            Log.i(TAG, "instantiateServiceCompat: rawServiceName = " + rawServiceName);
            return super.instantiateServiceCompat(PluginDexMergeManager.getClassLoader(),
                    rawServiceName, intent);
            //return super.instantiateServiceCompat(PluginDexMergeManager.getClassLoader(),
            //        PackageManagerServiceHook.SANDBOXED_SERVICES_NAME, intent);
        }
        return super.instantiateServiceCompat(cl, className, intent);
    }
}
