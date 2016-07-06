package com.avira.iklimov.smsscanner.controller;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.avira.iklimov.smsscanner.BuildConfig;
import com.avira.iklimov.smsscanner.R;
import com.avira.iklimov.smsscanner.model.database.SmsDB;
import com.avira.iklimov.smsscanner.model.database.SmsProvider;
import com.avira.iklimov.smsscanner.model.items.SMSObject;
import com.avira.iklimov.smsscanner.view.SmsContentActivity;

/**
 * class responsible for saving sms to db, removing sms from "inbox", sending notification, mark sms as read logic
 */
public class SMSController {

    public final static String LOG_TAG = "SMSController";

    protected boolean addNewSMS(@NonNull Context context, @NonNull SMSObject sms) {

        Uri insert = saveSmsToDB(context, sms);

        if (insert != null) {
            // parse the id number from uri
            sms.setId(insert);
            removeSmsFromAndroid(context, sms);
            showNotification(context, sms);
        }

        return false;
    }

    private void removeSmsFromAndroid(Context context, SMSObject sms) {
        try {
            Uri uriSms = Uri.parse("content://sms");
            Cursor cursor = context.getContentResolver().query(uriSms,
                    new String[]{"_id", "thread_id", "address",
                            "person", "date", "body"}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        long id = cursor.getLong(cursor.getColumnIndex("_id"));
                        long threadId = cursor.getLong(cursor.getColumnIndex("thread_id"));
                        String address = cursor.getString(cursor.getColumnIndex("address"));
                        String body = cursor.getString(cursor.getColumnIndex("body"));
                        String date = cursor.getString(cursor.getColumnIndex("date"));
                        if (sms.getContent().equals(body) && address.equals(sms.getSenderNumber()) && date.equals(String.valueOf(sms.getTimeStamp()))) {
                            int rows = context.getContentResolver().delete(Uri.parse("content://sms/" + id), "date=?", new String[]{cursor.getString(4)});
                            if (rows > 0) {
                                if (BuildConfig.DEBUG) {
                                    Log.d(LOG_TAG, "sms successfully deleted ");
                                }
                            }
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(LOG_TAG, " sms deletion exception", e);
        }
    }

    protected Uri saveSmsToDB(@NonNull Context context, @NonNull SMSObject sms) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Add new sms sender number " + sms.getSenderNumber());
            Log.d(LOG_TAG, "Add new sms content " + sms.getContent());
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsDB.SMS_SENDER_NUMBER_COLUMN, sms.getSenderNumber());
        contentValues.put(SmsDB.SMS_CONTENT_COLUMN, sms.getContent());
        contentValues.put(SmsDB.SMS_IS_READ_COLUMN, 0);
        contentValues.put(SmsDB.SMS_TIME_STAMP_COLUMN, sms.getTimeStamp());
        contentValues.put(SmsDB.SMS_DATE, sms.getDate());


        return contentResolver.insert(SmsProvider.getSmsUri(), contentValues);
    }


    protected void showNotification(Context context, SMSObject sms) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(sms.getSenderNumber())
                .setContentText(sms.getTime());
        Intent intent = new Intent(context, SmsContentActivity.class);
        intent.putExtra(SmsContentActivity.EXTRA_ID, sms.getId());
        PendingIntent pi = PendingIntent.getActivity(context, sms.getId(), intent, 0);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(sms.getId(), mBuilder.build());


    }

    protected void markAsRead(Context context, int id) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "markAsRead  id " + id);
        }
        Uri uri = SmsProvider.getSingleSmsUri(id);

        ContentValues contentValues = new ContentValues();
        contentValues.put(SmsDB.SMS_IS_READ_COLUMN, 1);

        context.getContentResolver().update(uri, contentValues, null, null);
    }


}
