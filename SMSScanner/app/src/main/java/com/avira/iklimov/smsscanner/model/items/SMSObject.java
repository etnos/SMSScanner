package com.avira.iklimov.smsscanner.model.items;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.avira.iklimov.smsscanner.model.database.SmsDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SMSObject implements Parcelable {

    public final static String LOG_TAG = "SMSObject";

    private int id;
    private String senderNumber;
    private String content;
    private String date;
    private long timeStamp;
    private int isRead;

    public SMSObject(String senderNumber, String content, long timeStamp) {
        this.senderNumber = senderNumber;
        this.content = content;
        this.timeStamp = timeStamp;
        this.date = getTime();
        this.isRead = 0;
    }

    public SMSObject(@NonNull Cursor cursor) {
        init(cursor);
    }

    private void init(Cursor cursor) {
        id = cursor.getInt(cursor.getColumnIndex(SmsDB.SMS_ID_COLUMN));
        isRead = cursor.getInt(cursor.getColumnIndex(SmsDB.SMS_IS_READ_COLUMN));
        senderNumber = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_SENDER_NUMBER_COLUMN));
        content = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_CONTENT_COLUMN));
        timeStamp = cursor.getLong(cursor.getColumnIndex(SmsDB.SMS_TIME_STAMP_COLUMN));
        date = cursor.getString(cursor.getColumnIndex(SmsDB.SMS_DATE));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(senderNumber);
        dest.writeString(content);
        dest.writeString(date);
        dest.writeLong(timeStamp);
        dest.writeInt(isRead);
    }

    public static final Parcelable.Creator<SMSObject> CREATOR
            = new Parcelable.Creator<SMSObject>() {
        public SMSObject createFromParcel(Parcel in) {
            return new SMSObject(in);
        }

        public SMSObject[] newArray(int size) {
            return new SMSObject[size];
        }
    };

    public SMSObject(Parcel in) {
        id = in.readInt();
        senderNumber = in.readString();
        content = in.readString();
        date = in.readString();
        timeStamp = in.readLong();
        isRead = in.readInt();
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public String getContent() {
        return content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getIsRead() {
        return isRead;
    }

    public String getTime(){
        return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", new Locale("DE")).format(new Date(timeStamp));
    }

    public void setId(Uri smsUri) {
        List<String> pathSegments = smsUri.getPathSegments();
        // the last segment is id of sms in db
        String smsId = pathSegments.get(pathSegments.size() - 1);
        try {
            id = Integer.parseInt(smsId);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "can not parse id", e);
        }
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }
}
