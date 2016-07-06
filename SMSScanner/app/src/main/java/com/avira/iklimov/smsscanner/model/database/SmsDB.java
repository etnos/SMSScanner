package com.avira.iklimov.smsscanner.model.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.avira.iklimov.smsscanner.BuildConfig;

public final class SmsDB extends SQLiteOpenHelper {

    private static final String LOG_TAG = "SmsDB";
    private static final String DATABASE_NAME = "SmsDB.db";
    private static final int DATABASE_VERSION = 3;

    // Table sms 
    public static final String SMS_TABLE = "sms";
    public static final String SMS_ID_COLUMN = "_id";
    public static final String SMS_SENDER_NUMBER_COLUMN = "sender_number";
    public static final String SMS_CONTENT_COLUMN = "content";
    public static final String SMS_TIME_STAMP_COLUMN = "timeStamp";
    public static final String SMS_DATE = "date";
    public static final String SMS_IS_READ_COLUMN = "isRead";

    // sms create statement
    private static final String DATABASE_SMS_CREATE = "CREATE TABLE " + SMS_TABLE + " (" +
            SMS_ID_COLUMN + " integer primary key autoincrement," +
            SMS_SENDER_NUMBER_COLUMN + " text not null," +
            SMS_CONTENT_COLUMN + " text," +
            SMS_DATE + " text," +
            SMS_TIME_STAMP_COLUMN + " integer not null," +
            SMS_IS_READ_COLUMN + " integer)";



    public SmsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Creating a new Database. Current version " + DATABASE_VERSION);
        }
        db.execSQL(DATABASE_SMS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+SMS_TABLE);
        db.execSQL(DATABASE_SMS_CREATE);
    }
}
