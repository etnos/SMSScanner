package com.avira.iklimov.smsscanner.view;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.avira.iklimov.smsscanner.R;
import com.avira.iklimov.smsscanner.model.database.SmsDB;
import com.avira.iklimov.smsscanner.model.database.SmsProvider;


public class SMSActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = "SMSActivityFragment";

    protected SmsListAdapter adapter;
    protected static int LOADER_ID = SmsProvider.getUniqueLoaderId();

    public SMSActivityFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sms_list_fragment, container, false);

        final ListView listView = (ListView) view.findViewById(R.id.smsListView);

        listView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        listView.setFastScrollEnabled(true);
        listView.setScrollingCacheEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = (Cursor) listView.getItemAtPosition(position);

                if (cursor != null) {
                    int smsId = cursor.getInt(cursor.getColumnIndex(SmsDB.SMS_ID_COLUMN));
                    Intent intent = new Intent(getActivity(), SmsContentActivity.class);
                    intent.putExtra(SmsContentActivity.EXTRA_ID, smsId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    getActivity().startActivity(intent);
                }
            }
        });

        listView.setSaveEnabled(true);

        adapter = new SmsListAdapter(getActivity());

        listView.setAdapter(adapter);

        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        final Uri uri = SmsProvider.getSmsUri();
        String selection = null;
        String[] selectionArgs = null;
        String[] projection = null;

        return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, SmsDB.SMS_TIME_STAMP_COLUMN + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (isAdded()) {
            if (adapter != null) {
                adapter.changeCursor(cursor);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null && isAdded()) {
            adapter.changeCursor(null);
        }
    }
}
