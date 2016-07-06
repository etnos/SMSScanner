package com.avira.iklimov.smsscanner.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.avira.iklimov.smsscanner.model.items.SMSObject;

public class SmsReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = "SmsReceiver";

    public static final String SMS_BUNDLE = "pdus";

    public SmsReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        abortBroadcast();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // prevent multiple actions
            String action = intent.getAction();
            if ("android.provider.Telephony.SMS_RECEIVED".equals(action)) {
                return;
            }
        }
        try {
            handleReceivedSms(context, intent);
        } catch (Exception e) {
            // no matter what happend, never crash the app here
            Log.e(LOG_TAG, "onReceive exception ", e);
            reportException(intent, e);
        }
    }

    private void reportException(Intent intent, Exception e) {
        Log.e(LOG_TAG, "Exception while executing command", e);
        // logic for reporting an exception, somethis like this:
        // report for an exception via HockeyApp
        // HandledException handledException = new HandledException(e);
        // ExceptionHandler.saveException(handledException, null);
    }

    protected void handleReceivedSms(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            Object[] sms = (Object[]) intentExtras.get(SMS_BUNDLE);
            for (Object sm : sms) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sm);

                String smsBody = smsMessage.getMessageBody();
                String address = smsMessage.getOriginatingAddress();
                long timeStamp = smsMessage.getTimestampMillis();

                SMSObject smsObject = new SMSObject(address, smsBody, timeStamp);

                SMSIntentService.addNewSMS(context, smsObject);
            }
        }
    }
}
