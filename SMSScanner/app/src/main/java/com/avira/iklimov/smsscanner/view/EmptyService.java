package com.avira.iklimov.smsscanner.view;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * this class should be empty
 */
public class EmptyService extends Service {
    public EmptyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
