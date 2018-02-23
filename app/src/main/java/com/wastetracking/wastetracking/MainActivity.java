package com.wastetracking.wastetracking;

import android.Manifest;
import android.os.Bundle;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;

import android.support.annotation.NonNull;


import android.util.Log;

import java.util.ArrayList;

/**
 * Activity for reading data from an NDEF Tag.
 *
 *
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";

    private ListView mListView;
    private NfcAdapter mNfcAdapter;
    ArrayAdapter<String> mArrayAdapter;

    private DeviceLocationManager mDeviceLocationManager;

    private LocalCache mLocalCache;
    private ArrayList<String> mCachedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // For Testing - Constant server address
        String serverUrl = "http://35.153.34.189:9080/";
        String realmUrl = "http://35.153.34.189:9080/auth";
        String serverUsername = "realm-admin";
        String serverPassword = "";

        // Initiate Realm Authorization
        SyncCredentials creds = SyncCredentials.usernamePassword(serverUsername, serverPassword);

        SyncUser.Callback<SyncUser> callback = new SyncUser.Callback<SyncUser>() {

            @Override
            public void onSuccess(SyncUser result) {
                Log.d(TAG, getApplicationContext().getResources().getString(R.string.realm_authen_bad));
                Log.d(TAG, result.getIdentity());
                Log.d(TAG, result.toJson());
            }

            @Override
            public void onError(ObjectServerError error) {
                Log.e(TAG, getString(R.string.realm_authen_bad));

                String errorExactMessage = error.getErrorMessage();

                if (errorExactMessage == null) {
                    errorExactMessage = getString(R.string.generic_unknown_error);
                }

                Log.e(TAG, errorExactMessage);
            }
        };

        SyncUser.loginAsync(creds, realmUrl, callback);
        Log.d(TAG, "Finished setting up Realm authorization.");

        // Prompt the special permissions dialog box
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.NFC,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Enable location management
        mDeviceLocationManager = new DeviceLocationManager(getApplicationContext());
        Log.d(TAG, mDeviceLocationManager.getLocationString());

        // Set up activity main as the main layout
        setContentView(R.layout.activity_main);

        // Set up NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is disabled!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "NFC is active!", Toast.LENGTH_LONG).show();
        }

        handleIntent(getIntent());

        // Set up local cache data, and populate with saved data upon starting up
        mLocalCache = new LocalCache(this);
        mCachedData = mLocalCache.getAllEntries();

        if (mCachedData.size() == 0) {
            mCachedData.add(getString(R.string.default_log_text));
            //mTextView.setText(R.string.default_log_text);
        }

        // Populate the main list with the cached data
        mListView = (ListView) findViewById(R.id.listview_scan_log);

        // Set up the array adapter to load the cached data in the main list view
        mArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, mCachedData
        );
        mListView.setAdapter(mArrayAdapter);

        // Give a test button the ability to get the current location.
        FloatingActionButton debugButton = (FloatingActionButton) findViewById(R.id.debug_button);
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location = mDeviceLocationManager.getLocationString();
                Log.d(TAG, location);
                Toast.makeText(v.getContext(), location, Toast.LENGTH_LONG).show();
            }
        });

        // Start the Realm Activity now that everything should be set up
        Intent realmIntent = new Intent(this, RealmActivity.class);
        startActivity(realmIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * It's important, that the activity is in the foreground (resumed). Otherwise
         * an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        /*
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /*
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        //super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "Action: " + action);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Log.d(TAG, "Scan triggered NDEF_DISCOVERED");
            // TODO: Properly treat when NDEF_DISCOVERED

            String placeholder = "NDEF payload detected! Currently not able to process!";
            Toast.makeText(this, placeholder, Toast.LENGTH_LONG).show();

        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(TAG, "Scan triggered TECH_DISCOVERED");
            // TODO: Properly treat when TAG_DISCOVERED

            String placeholder = "NDEF payload detected, but cannot be mapped to a MIME type or URI!";
            Toast.makeText(this, placeholder, Toast.LENGTH_LONG).show();

        } else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.d(TAG, "No chip technology found, but there is a tag!");

            byte[] extracted_bytes = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String converted_string;

            try {
                converted_string = Utility.bytesToHex(extracted_bytes);
            } catch (Exception e) {
                converted_string = "Failed to convert!";
            }

            String concat_string = "Scanned Value: " + converted_string;

            // Update the local storage with this new scanned value
            mLocalCache.insertEntry(Utility.getCurrentTimeStamp(), concat_string);

            // Update the current main text by changing the data and notifying the adapter
            mCachedData.add(Utility.getCurrentTimeStamp() + ", " + concat_string);
            mArrayAdapter.notifyDataSetChanged();
        }
    }


    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link Activity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

}