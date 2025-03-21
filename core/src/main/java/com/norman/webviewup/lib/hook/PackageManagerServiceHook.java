package com.norman.webviewup.lib.hook;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.norman.webviewup.lib.reflect.RuntimeAccess;
import com.norman.webviewup.lib.service.binder.BinderHook;
import com.norman.webviewup.lib.service.binder.ProxyBinder;
import com.norman.webviewup.lib.service.interfaces.IActivityThread;
import com.norman.webviewup.lib.service.interfaces.IApplicationInfo;
import com.norman.webviewup.lib.service.interfaces.IContextImpl;
import com.norman.webviewup.lib.service.interfaces.IPackageManager;
import com.norman.webviewup.lib.service.interfaces.IServiceManager;
import com.norman.webviewup.lib.service.proxy.PackageManagerProxy;
import com.norman.webviewup.lib.util.FileUtils;
import com.norman.webviewup.lib.util.ProcessUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class PackageManagerServiceHook extends BinderHook {

    private final Context context;

    private final String webViewPackageName;

    private final String apkPath;

    private final String libsPath;

    private Map<String, IBinder> binderCacheMap;


    public PackageManagerServiceHook(@NonNull Context context,
                                     @NonNull String packageName,
                                     @NonNull String apkPath,
                                     @NonNull String libsPath) {
        this.context = context;
        this.webViewPackageName = packageName;
        this.apkPath = apkPath;
        this.libsPath = libsPath;
    }

    /**
     * <a href="https://chromium.googlesource.com/chromium/src/+/6e5f4b264b8d00b7f625227cb67fcb7bc476c29e/base/android/java/src/org/chromium/base/process_launcher/ChildConnectionAllocator.java#116">服务检查源码</a>
     * <a href="https://chromium.googlesource.com/chromium/src/+/refs/heads/main/content/public/android/java/src/org/chromium/content/browser/ChildProcessCreationParamsImpl.java#21">相关服务包名定义</a>
     */
    private static final String TAG = "PackageManagerServiceHook";
    public static final String SANDBOXED_SERVICES_NAME = "org.chromium.content.app.SandboxedProcessService0";

    private final PackageManagerProxy proxy = new PackageManagerProxy() {

        @SuppressLint("LongLogTag")
        @Override
        protected ServiceInfo getServiceInfo(ComponentName componentName, long flags, int userId) {
            Log.i(TAG, "【getServiceInfo】");
            // 此方案已不需要
//            if (!TextUtils.equals(webViewPackageName, componentName.getPackageName())) {
//                return (ServiceInfo) invoke();
//            }
//            Log.i(TAG, "getServiceInfo: " + componentName.getClassName());
//            if (!SANDBOXED_SERVICES_NAME.equals(componentName.getClassName())) {
//                return (ServiceInfo) invoke();
//            }
//            // Skip the sandboxed service check, 返回任意已存在的ServiceInfo跳过检查
//            PackageInfo packageInfo = getPackageInfo(componentName.getPackageName(), PackageManager.GET_SERVICES);
//            if (packageInfo != null && packageInfo.services != null) {
//                ServiceInfo serviceInfo = packageInfo.services[0];
//                Log.i(TAG, "Skip the sandboxed service check, fake: " + serviceInfo.name);
//                return serviceInfo;
//            }
            return (ServiceInfo) invoke();
        }

        @Override
        protected PackageInfo getPackageInfo(String packageName, long flags, int userId) {
            return getPackageInfo(packageName, (int) flags);
        }

        @Override
        protected PackageInfo getPackageInfo(String packageName, int flags, int userId) {
            return getPackageInfo(packageName, flags);
        }

        @Override
        protected int getComponentEnabledSetting(ComponentName componentName, int userId) {
            return getComponentEnabledSetting(componentName);
        }

        @Override
        protected PackageInfo getPackageInfo(String packageName, int flags) {
            if (packageName.equals(webViewPackageName)) {
                PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, flags);
                if (packageInfo == null) {
                    flags &= ~PackageManager.GET_SIGNATURES;
                    packageInfo = context.getPackageManager().getPackageArchiveInfo(apkPath, flags);
                }
                if (packageInfo == null) {
                    throw new RuntimeException("apkPath is not valid  " + apkPath);
                }
                boolean is64Bit = ProcessUtils.is64Bit();
                String[] supportBitAbis = is64Bit ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
                Arrays.sort(supportBitAbis, Collections.reverseOrder());
                String nativeLibraryDir = null;

                File libsDir = new File(libsPath);
                if (!FileUtils.exist(libsDir)) {
                    throw new RuntimeException("libsDir not exist  " + libsPath);
                }
                String[] list = libsDir.list();
                if (list == null){
                    throw new RuntimeException("abi dir  not exist in " + libsPath);
                }
                Arrays.sort(supportBitAbis);
                String cpuAbi = null;
                for (String name : list) {
                    if (Arrays.binarySearch(supportBitAbis, name) >= 0) {
                        cpuAbi = name;
                        nativeLibraryDir = new File(libsDir, name).getAbsolutePath();
                        break;
                    }
                }

                if (nativeLibraryDir == null) {
                    throw new NullPointerException("unable to find supported abis "
                            + Arrays.toString(supportBitAbis)
                            + " in dir " + libsPath);
                }
                try {
                    IApplicationInfo iApplicationInfo = RuntimeAccess.objectAccess(IApplicationInfo.class, packageInfo.applicationInfo);
                    iApplicationInfo.setPrimaryCpuAbi(cpuAbi);
                } catch (Throwable ignore) {

                }

                try {
                    IApplicationInfo iApplicationInfo = RuntimeAccess.objectAccess(IApplicationInfo.class, packageInfo.applicationInfo);
                    iApplicationInfo.setNativeLibraryRootDir(libsPath);
                } catch (Throwable ignore) {

                }
                packageInfo.applicationInfo.nativeLibraryDir = nativeLibraryDir;
                if (TextUtils.isEmpty(packageInfo.applicationInfo.sourceDir)) {
                    packageInfo.applicationInfo.sourceDir = apkPath;
                }
                if (TextUtils.isEmpty(packageInfo.applicationInfo.publicSourceDir)) {
                    packageInfo.applicationInfo.publicSourceDir = apkPath;
                }
                packageInfo.applicationInfo.flags |= ApplicationInfo.FLAG_INSTALLED
                        | ApplicationInfo.FLAG_HAS_CODE;
                return packageInfo;
            }
            return (PackageInfo) invoke();
        }

        @Override
        protected int getComponentEnabledSetting(ComponentName componentName) {
            if (componentName.getPackageName().equals(webViewPackageName)) {
                return PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            } else {
                return (int) invoke();
            }
        }

        @Override
        protected String getInstallerPackageName(String packageName) {
            if (packageName.equals(webViewPackageName)) {
                // fake google play
                return "com.android.vending";
            } else {
                return (String) invoke();
            }
        }

        @Override
        protected IBinder asBinder() {
            IBinder proxyBinder = getProxyBinder();
            return proxyBinder != null ? proxyBinder : (IBinder) invoke();
        }
    };


    @Override
    protected IBinder onTargetBinderObtain() {
        IServiceManager serviceManager = RuntimeAccess.staticAccess(IServiceManager.class);
        return serviceManager.getService(IPackageManager.SERVICE);
    }

    @Override
    protected ProxyBinder onProxyBinderCreate(IBinder binder) {
        IPackageManager service = RuntimeAccess.staticAccess(IPackageManager.class);
        IServiceManager serviceManager = RuntimeAccess.staticAccess(IServiceManager.class);

        IInterface targetInterface = service.asInterface(binder);
        proxy.setTarget(targetInterface);
        IInterface proxyInterface = (IInterface) proxy.get();
        ProxyBinder proxyBinder = new ProxyBinder(targetInterface, proxyInterface);

        binderCacheMap = serviceManager.getServiceCache();
        return proxyBinder;
    }

    @Override
    protected void onTargetBinderRestore(IBinder binder) {
        IInterface targetInterface;
        try {
            targetInterface = binder.queryLocalInterface(binder.getInterfaceDescriptor());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        binderCacheMap.put(IPackageManager.SERVICE, binder);
        updateActivityThreadPackageManager(targetInterface);
        flushContextImplPackageManager();
    }

    @Override
    protected void onProxyBinderReplace(ProxyBinder binder) {
        binderCacheMap.put(IPackageManager.SERVICE, binder);
        updateActivityThreadPackageManager(binder.getProxyIInterface());
        flushContextImplPackageManager();
    }

    private static void updateActivityThreadPackageManager(IInterface iInterface) {
        IActivityThread activityThread = RuntimeAccess.staticAccess(IActivityThread.class);
        activityThread.setPackageManager(iInterface);
    }

    private void flushContextImplPackageManager() {
        Context baseContext = context.getApplicationContext();
        while (baseContext instanceof ContextWrapper) {
            baseContext = ((ContextWrapper) context).getBaseContext();
        }
        IContextImpl contextImpl = RuntimeAccess.objectAccess(IContextImpl.class, baseContext);
        contextImpl.setPackageManager(null);
    }


}
