package com.avira.iklimov.smsscanner.model.database;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.avira.iklimov.smsscanner.BuildConfig;

import java.util.ArrayList;

public class SmsProvider extends ContentProvider {
    private static final String LOG_TAG = "SmsProvider";

    //    public static final String AUTHORITY = "com.avira.iklimov.smsscanner.SmsDatabase";
    public static final String AUTHORITY = "com.avira.iklimov.smsscanner";
    public static final String URI_CONTENT_PREFIX = "content://";

    private SmsDB mLocalDatabase;

    // sms constants
    public static final int ALL_SMS = 1;
    public static final int SINGLE_SMS = 2;

    private static Uri SMS_URI = Uri.parse("/sms");

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     * counter to be used as unique ID for newly created loader,
     * should be changed on each use to be unique
     */
    private static int loaderId = 0;

    /**
     * get a unique ID for a newly created loader
     *
     * @return unique id to be used as a loader id
     */
    public static synchronized int getUniqueLoaderId() {
        loaderId++;
        return loaderId;
    }

    @Override
    public boolean onCreate() {
        uriMatcher.addURI(AUTHORITY, "sms", ALL_SMS);
        uriMatcher.addURI(AUTHORITY, "sms/#", SINGLE_SMS);
        mLocalDatabase = new SmsDB(getContext());
        return true;
    }

    public static Uri getSmsUri() {
        return Uri.parse(URI_CONTENT_PREFIX + AUTHORITY + SMS_URI);
    }

    public static Uri getSingleSmsUri(final int smsId) {
        return Uri.parse(URI_CONTENT_PREFIX + AUTHORITY + SMS_URI + "/" + smsId);
    }

    @Nullable
    private Uri getContentUriFromUri(@NonNull Uri uri) {
        // Only for actual tables
        switch (uriMatcher.match(uri)) {
            case ALL_SMS:
            case SINGLE_SMS:
                return SMS_URI;
            default:
                break;
        }

        return null;
    }

    @Nullable
    private String getTableNameFromUri(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ALL_SMS:
            case SINGLE_SMS:
                return SmsDB.SMS_TABLE;
            default:
                break;
        }

        return null;
    }

    @NonNull
    private ArrayList<Uri> getAssociatedViewUris(@NonNull Uri uri) {
        // Only for actual views
        ArrayList<Uri> viewUris = new ArrayList<>();
        ;
        switch (uriMatcher.match(uri)) {
            case ALL_SMS:
            case SINGLE_SMS:
                viewUris.add(getSmsUri());
                break;
            default:
                break;
        }

        return viewUris;
    }

    public void sendNotification(Uri uri) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "Send notification for Uri:  " + uri);
        }
        this.getContext().getContentResolver().notifyChange(uri, null);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        // Return a string that identifies the MIME type for a Content Provider URI
        switch (uriMatcher.match(uri)) {
            case ALL_SMS:
            case SINGLE_SMS:
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + ".sms";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Open database
        SQLiteDatabase db;
        try {
            db = mLocalDatabase.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = mLocalDatabase.getReadableDatabase();
        }
        // Replace these with valid SQL statements if necessary.
        String groupBy = null;
        String having = null;
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        // If this is a row query, limit the result set to the passed in row.
        String rowID;
        switch (uriMatcher.match(uri)) {
            case SINGLE_SMS:
                rowID = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(SmsDB.SMS_ID_COLUMN + "=" + rowID);
                break;
            default:
                break;
        }
        // Specify the table on which to perform the query. This can be a specific table or a join as required.
        queryBuilder.setTables(getTableNameFromUri(uri));

        // Execute...
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    @Nullable
    public Uri insert(Uri uri, ContentValues values) {

        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG, "insert uri " + uri);
        }

        // Open database
        SQLiteDatabase db = mLocalDatabase.getWritableDatabase();
        // Try to do an insert as per usual
        long id = db.insert(getTableNameFromUri(uri), null, values);

        if (id > -1) {
            // the insert was successful
            sendNotification(uri);
            // For non-query statements, we also check if we need to notify any view urls. If we update/insert/remove something from a table used by a view, the view must know.
            ArrayList<Uri> viewUris = getAssociatedViewUris(uri);
            for (Uri viewUri : viewUris) {
                sendNotification(viewUri);

            }

            Uri contentUri = getContentUriFromUri(uri);
            if (contentUri != null) {
                return ContentUris.withAppendedId(contentUri, id);
            }

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Open database
        SQLiteDatabase db = mLocalDatabase.getWritableDatabase();
        // Perform update
        String tableName = getTableNameFromUri(uri);
        //Make it safe because UPDATE null ... isnt a valid statement
        if (tableName != null) {
            int updateCount = db.update(tableName, values, selection, selectionArgs);
            sendNotification(uri);

            // For non-query statements, we also check if we need to notify any view urls. If we update/insert/remove something from a table used by a view, the view must know.
            ArrayList<Uri> viewUris = getAssociatedViewUris(uri);
            for (Uri viewUri : viewUris) {
                sendNotification(viewUri);
            }

            return updateCount;
        } else {
            return 0;
        }
    }


}
