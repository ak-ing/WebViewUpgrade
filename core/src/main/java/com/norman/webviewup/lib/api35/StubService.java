package com.norman.webviewup.lib.api35;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * @author Created by Ak on 2025-02-27 22:07.
 */
public class StubService extends Service {
    public StubService() {
        super();
        //Thread.dumpStack();   // 打印调用堆栈信息
        Log.i("TAG", "create StubService: " );
    }

    public static final String NAME = "com.norman.webviewup.lib.api35.StubService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
