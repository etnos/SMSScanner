package com.avira.iklimov.smsscanner.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.avira.iklimov.smsscanner.R;
import com.avira.iklimov.smsscanner.controller.SMSIntentService;
import com.avira.iklimov.smsscanner.model.items.SMSObject;


public class SMSActivity extends Activity {

    private int count = 1;

    boolean isDefault = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sms_list_activity);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            isDefault = Telephony.Sms.getDefaultSmsPackage(this).equals(getPackageName());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isDefault) {
            makeDefault();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sms, menu);
        menu.findItem(R.id.action_default).setVisible(!isDefault);
        menu.findItem(R.id.action_settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            simulateSms();
            return true;
        } else if (id == R.id.action_default) {
            makeDefault();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // make application default for receiving sms
    private void makeDefault() {
        Intent intent =
                new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                getPackageName());
        startActivity(intent);
    }

    private void simulateSms() {
        SMSObject sms = new SMSObject("123-123-123", "test  djgbhsdifbg dfhsbgibdfs igbisfdghb isdfgbisdfg count - " + count++, System.currentTimeMillis());
        SMSIntentService.addNewSMS(this, sms);
    }
}
