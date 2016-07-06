package com.avira.iklimov.smsscanner.controller;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.avira.iklimov.smsscanner.model.items.SMSObject;


public class SMSIntentService extends IntentService {

    private static final String LOG_TAG = "SMSIntentService";

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_ADD_NEW_SMS = "com.avira.iklimov.smsscanner.Controller.action.ACTION_ADD_NEW_SMS";
    private static final String ACTION_MARK_AS_RED = "com.avira.iklimov.smsscanner.Controller.action.ACTION_MARK_AS_RED";

    private static final String EXTRA_NEW_SMS = "com.avira.iklimov.smsscanner.Controller.extra.NEW_SMS";
    private static final String EXTRA_SMS_ID = "com.avira.iklimov.smsscanner.Controller.extra.SMS_ID";

    private SMSController smsController;

    /**
     * Starts this service to add a new sms. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void addNewSMS(@NonNull Context context, @NonNull SMSObject sms) {

        Intent intent = new Intent(context, SMSIntentService.class);
        intent.setAction(ACTION_ADD_NEW_SMS);
        intent.putExtra(EXTRA_NEW_SMS, sms);
        context.startService(intent);
    }

    /**
     * Starts this service to mark sms as read. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void addMarkAsRead(@NonNull Context context, int id) {

        Intent intent = new Intent(context, SMSIntentService.class);
        intent.setAction(ACTION_MARK_AS_RED);
        intent.putExtra(EXTRA_SMS_ID, id);
        context.startService(intent);
    }

    public SMSIntentService() {
        super("SMSIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            // it is IntentService, multithreading is not possible in this place,
            // no need to check
            if (smsController == null) {
                smsController = new SMSController();
            }
            try {
                executeAction(intent);
            } catch (Exception e) { // Prevent app crash for any reason
                Log.e(LOG_TAG, "onHandleIntent exception ", e);
                execudeFallback(intent, e);
                reportException(intent, e);
            }
        }
    }

    private void reportException(Intent intent, Exception e) {
        Log.e(LOG_TAG, "Exception while executing command", e);
        // logic for reporting an exception, somethis like this:
        // report for an exception via HockeyApp
        // HandledException handledException = new HandledException(e);
        // ExceptionHandler.saveException(handledException, null);
    }


    private void execudeFallback(Intent intent, Exception e) {
        // logic for some fallback
    }

    protected void executeAction(Intent intent) {
        final String action = intent.getAction();
        if (ACTION_ADD_NEW_SMS.equals(action)) {
            final SMSObject sms = intent.getExtras().getParcelable(EXTRA_NEW_SMS);
            smsController.addNewSMS(getApplicationContext(), sms);
        } else if (ACTION_MARK_AS_RED.equals(action)) {
            int id = intent.getIntExtra(EXTRA_SMS_ID, 0);
            smsController.markAsRead(getApplicationContext(), id);
        }
    }
}
