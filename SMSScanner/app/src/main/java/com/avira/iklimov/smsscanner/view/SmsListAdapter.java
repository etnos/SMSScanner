package com.avira.iklimov.smsscanner.view;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.avira.iklimov.smsscanner.R;
import com.avira.iklimov.smsscanner.model.database.SmsDB;
import com.avira.iklimov.smsscanner.model.items.SmsListViewHolder;

public class SmsListAdapter extends CursorAdapter {


    private final LayoutInflater inflater;

    public SmsListAdapter(Context context) {
        super(context, null, true);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return inflater.inflate(R.layout.sms_list_item, null);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final String senderNumber = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_SENDER_NUMBER_COLUMN));
        final String content = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_CONTENT_COLUMN));
        final String date = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_DATE));

        final SmsListViewHolder viewHolder;
        if (view.getTag() != null) {
            viewHolder = (SmsListViewHolder) view.getTag();
        } else {
            viewHolder = new SmsListViewHolder();
            viewHolder.txtSenderNumber = (TextView) view.findViewById(R.id.txtSenderNumber);
            viewHolder.txtContent = (TextView) view.findViewById(R.id.txtContent);
            viewHolder.txtDate = (TextView) view.findViewById(R.id.txtDate);
            view.setTag(viewHolder);
        }

        viewHolder.txtSenderNumber.setText(senderNumber);
        viewHolder.txtContent.setText(content);
        viewHolder.txtDate.setText(date);
    }
}
