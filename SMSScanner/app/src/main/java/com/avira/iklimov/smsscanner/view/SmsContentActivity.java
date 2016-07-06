package com.avira.iklimov.smsscanner.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avira.iklimov.smsscanner.BuildConfig;
import com.avira.iklimov.smsscanner.R;
import com.avira.iklimov.smsscanner.controller.SMSIntentService;
import com.avira.iklimov.smsscanner.model.database.SmsProvider;
import com.avira.iklimov.smsscanner.model.items.SMSObject;

public class SmsContentActivity extends Activity {

    public static final String EXTRA_ID = "ID";
    public final static String LOG_TAG = "SmsContentActivity";
    SmsObserver smsObserver;
    int smsId;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smsId = getIntent().getIntExtra(EXTRA_ID, 0);

        SMSObject smsObject = initSms(this, smsId);

        showSmsContentDialog(this, smsObject);

    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (BuildConfig.DEBUG) {
                Log.d(LOG_TAG, "Handler  handleMessage");
            }

            cancelNotification(SmsContentActivity.this, smsId);

            return true;
        }
    });


    @Override
    protected void onResume() {
        super.onResume();

        if (smsObserver == null) {
            smsObserver = new SmsObserver(handler);
        }

        Uri uri = SmsProvider.getSingleSmsUri(smsId);

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "registerContentObserver uri " + uri);
        }

        getContentResolver().registerContentObserver(uri, true, smsObserver);


        // mark sms as read - remove notification
        markAsRead(this, smsId);

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (smsObserver != null) {
            getContentResolver().unregisterContentObserver(smsObserver);
        }
    }

    protected SMSObject initSms(final Context context, int smsId) {
        Uri smsUri = SmsProvider.getSingleSmsUri(smsId);
        SMSObject smsObject = null;
        Cursor cursor = context.getContentResolver().query(smsUri, null, null, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                smsObject = new SMSObject(cursor);
            }
            cursor.close();
        }
        return smsObject;
    }

    protected void showSmsContentDialog(final Context context, final SMSObject smsObject) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.sms_content_dialog);
        dialog.setTitle(smsObject.getSenderNumber());

        TextView txtContent = (TextView) dialog.findViewById(R.id.txtContent);
        TextView txtFrom = (TextView) dialog.findViewById(R.id.txtFrom);
        TextView txtTime = (TextView) dialog.findViewById(R.id.txtTimeStamp);
        Button btnResponse = (Button) dialog.findViewById(R.id.btnResponse);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);


        txtContent.setText(smsObject.getContent());
        txtTime.setText(smsObject.getTime());
        txtFrom.setText(smsObject.getSenderNumber());

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialog != null) {
                    dialog.dismiss();
                }

                SmsContentActivity.this.finish();
            }
        });

        btnResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, R.string.not_available, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    protected void markAsRead(Context context, int id) {
        SMSIntentService.addMarkAsRead(context, id);
    }


    private void cancelNotification(Context context, int id) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "close notification " + id);
        }
        NotificationManager notifMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMgr.cancel(id);


    }
}
