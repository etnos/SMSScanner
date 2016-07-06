package com.avira.iklimov.smsscanner.view;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avira.iklimov.smsscanner.BuildConfig;

public class SmsObserver extends ContentObserver {

    public final static String LOG_TAG = "SmsObserver";

    Handler handler;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public SmsObserver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange)
    {
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "onChange " + uri);
        }

        handler.dispatchMessage(new Message());


        // do s.th.
        // depending on the handler you might be on the UI
        // thread, so be cautious!
    }

}
