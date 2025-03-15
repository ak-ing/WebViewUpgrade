package com.norman.webviewup.lib.api35;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * @author Created by Ak on 2025-02-25 21:23.
 */
public final class PluginDexMergeManager {

    private static final String TAG = "PluginDexMergeManager";
    private static PathClassLoader sClassLoader;
    private static volatile boolean sIsInstalled = false;

    public static void loadPluginDex(Context context, String pluginDexPath) {
        try {
            // 获取宿主的dexElement
            Object[] baseDexElement = findBaseDexElement(context);
            // 获取插件的dexElement
            Object[] pluginDexElement = findPluginDexElement(context, pluginDexPath);
            // 合并两个dexElement
            Object newDexElements = makeNewDexElements(baseDexElement, pluginDexElement);
            // 替换宿主的dexElement
            replaceDexElement(context, newDexElements);
            sIsInstalled = true;
            Log.i(TAG, "loadPluginDex: success");
        } catch (Exception e) {
            sIsInstalled = false;
            Log.e(TAG, "loadPluginDex: ", e);
        }
    }

    private static void replaceDexElement(Context context, Object newDexElements) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        ClassLoader baseClassLoader = context.getClassLoader();
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(),
                baseClassLoader, "pathList");
        Class<?> dexClazz = Class.forName("dalvik.system.DexPathList");
        Field dexElementsFiled = dexClazz.getDeclaredField("dexElements");
        dexElementsFiled.setAccessible(true);
        dexElementsFiled.set(pathListObj, newDexElements);
    }

    /**
     * 获取宿主的dexElement
     */
    private static Object[] findBaseDexElement(Context context) {
        ClassLoader baseClassLoader = context.getClassLoader();
        // 获取 BaseDexClassLoader : pathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(),
                baseClassLoader, "pathList");

        // 获取宿主的dexElement
        // PathList: Element[] dexElements
        return (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");
    }

    /**
     * 获取插件的dexElement
     */
    private static Object[] findPluginDexElement(Context context, String pluginDexPath) {
        //加载插件的类加载器
        sClassLoader = new PathClassLoader(pluginDexPath, null, context.getClassLoader());
        //这样获取到的就是插件中的DexPathList
        Object pathListObj = RefInvoke.getFieldObject(DexClassLoader.class.getSuperclass(), sClassLoader, "pathList");
        //获取插件的dexElement
        return (Object[]) RefInvoke.getFieldObject(pathListObj, "dexElements");
    }

    /**
     * 合并两个dexElement
     * 通过反射来创Element类型的数组
     */
    private static Object makeNewDexElements(Object[] baseDexElement, Object[] pluginDexElement) {
        if (baseDexElement != null && pluginDexElement != null) {
            Class<?> componentType = baseDexElement.getClass().getComponentType();
            if (componentType == null) {
                Log.e(TAG, "makeNewDexElements: componentType is null");
                return null;
            }
            Object newDexElements = java.lang.reflect.Array.newInstance(
                    baseDexElement.getClass().getComponentType(),
                    baseDexElement.length + pluginDexElement.length
            );
            System.arraycopy(baseDexElement, 0, newDexElements, 0, baseDexElement.length);
            System.arraycopy(pluginDexElement, 0, newDexElements, baseDexElement.length, pluginDexElement.length);
            return newDexElements;
        }
        return null;
    }

    public static PathClassLoader getClassLoader() {
        return sClassLoader;
    }

    public static boolean isInstalled() {
        return sIsInstalled;
    }
}
