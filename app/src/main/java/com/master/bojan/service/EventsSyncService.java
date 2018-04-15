package com.master.bojan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by bojan on 15.4.18..
 */

public class EventsSyncService extends Service {


    private static final Object sSyncAdapterLock = new Object();
    private static EventsSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("EventsSyncService", "onCreate - EventsSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new EventsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}
