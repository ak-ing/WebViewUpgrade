package com.norman.webviewup.lib.api35;

import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Created by Ak on 2025-02-25 21:43.
 */
class MockClass1 implements InvocationHandler {

    static final Map<String, String> pluginServices = new HashMap<>();
    private static final String TAG = "MockClass1";
    // 替身StubService的包名(宿主app)
    private static String stubPackage = "";

    Object mBase;

    public MockClass1(Object base, String packageName) {
        Log.d(TAG, "MockClass1: init");
        mBase = base;
        stubPackage = packageName;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("bindServiceInstance".equals(method.getName())) {
            Log.i(TAG, method.getName());
            // 找到参数里面的第一个Intent 对象
            int index = -1;
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                return method.invoke(mBase, args);
            }

            Intent rawIntent = (Intent) args[index];
            if (rawIntent != null && rawIntent.getComponent() != null
                    && rawIntent.getComponent().getClassName().contains("SandboxedProcessService")) {
                // 缓存原本要启动的service组件
                pluginServices.put(StubService.NAME, rawIntent.getComponent().getClassName());
                // 使用替身替换掉原始的Intent
                rawIntent.setComponent(new ComponentName(stubPackage, StubService.NAME));
                // Replace Intent, cheat AMS
                args[index] = rawIntent;

                Log.d(TAG, "hook success");
                return method.invoke(mBase, args);
            }
        }

        //if ("startService".equals(method.getName())) {
        //    return method.invoke(mBase, args);
        //} else if ("stopService".equals(method.getName())) {
        //    return method.invoke(mBase, args);
        //} else if ("bindService".equals(method.getName())) {
        //    return method.invoke(mBase, args);
        //}

        return method.invoke(mBase, args);
    }

    /**
     * 获取原本要启动的Service名称
     */
    public static String getRawServiceName(String stubServiceName) {
        return pluginServices.get(stubServiceName);
    }
}