package com.norman.webviewup.lib.api35;

import android.os.Handler;
import android.os.Message;
import android.util.Log;


/**
 * @author Created by Ak on 2025-02-25 21:49.
 */
class MockClass2 implements Handler.Callback {

    private static final String TAG = "MockClass2";
    Handler mBase;

    public MockClass2(Handler base) {
        Log.d(TAG, "MockClass2: init");
        mBase = base;
    }

    @Override
    public boolean handleMessage(Message msg) {

        Log.d(TAG, String.valueOf(msg.what));
        switch (msg.what) {

            // ActivityThread里面 "CREATE_SERVICE" 这个字段的值是114
            // 本来使用反射的方式获取最好, 这里为了简便直接使用硬编码
            case 114:
                //handleCreateService(msg);
                break;
        }

        mBase.handleMessage(msg);
        return true;
    }

    private void handleCreateService(Message msg) {
        // 这里简单起见,直接取出插件Service

//        Object obj = msg.obj;
//        ServiceInfo serviceInfo = (ServiceInfo) RefInvoke.getFieldObject(obj, "info");
//
//        String realServiceName = null;
//
//        for (String key : pluginServices.keySet()) {
//            String value = pluginServices.get(key);
//            if (value.equals(serviceInfo.name)) {
//                realServiceName = key;
//                break;
//            }
//        }
//
//        serviceInfo.name = realServiceName;
    }
}
