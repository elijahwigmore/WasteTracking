package com.wastetracking.wastetracking;

import android.Manifest;
import android.support.v4.app.*;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;

import io.realm.ObjectServerError;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.SyncConfiguration;
import io.realm.SyncCredentials;
import io.realm.SyncUser;


import android.util.Log;

import com.wastetracking.wastetracking.Model.Address;
import com.wastetracking.wastetracking.Model.RFIDScan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Activity for reading data from an NDEF Tag.
 *
 *
 */
public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "MainActivityMain";

    private static final String NFCV = "android.nfc.tech.NfcV";

    private ListView mListView;
    private NfcAdapter mNfcAdapter;
    CustomAdapter mCustomAdapter;

    private DeviceLocationManager mDeviceLocationManager;

    private ArrayList<String> mParsedScanData;

    private String scannedValue = "NO SCANNED VALUE";
    private static final String mRealmUrl = "realm://35.153.34.189:9080/~/test";
    private SyncUser mUser;
    private Realm mRealm;

    private LocalCache mLocalCache;

    private NonSwipePageViewer mViewpager;
    private PagerAdapter mPagerAdapter;

    private ArrayList<Fragment> mAllFragments;
    private Fragment mEmptyFragment;
    private Fragment mMainFragment;
    private Fragment mMapFragment;


    // used for date operations
    private Calendar calendar;
    private SimpleDateFormat calendarDateFormat;
    private static final String CALENDAR_DATE_FORMAT_STRING = "EEE, MMM dd";
    private static final int CALENDAR_YEAR_OFFSET = 1900;

    private static final String BLANK_ADDRESS = "";

    private static final String SCAN_SUCCESSFUL_MESSAGE = "Scan Successful";
    private static final String SCAN_DUPLICATE_MESSAGE = "Bin Already Scanned Today";
    private static final String SCAN_NA_ADDRESS_MESSAGE = "Tag Not Recognized";

    //Addresses
    public ArrayList<String> addresses;
    RealmResults<Address> mCurrentRealmAddresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, LoginActivity.class));

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
                Log.d(TAG, getApplicationContext().getResources().getString(R.string.realm_authen_good));
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

                Log.e(TAG, error.toString());
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
            Toast.makeText(this, "   NFC is disabled!   ", Toast.LENGTH_LONG).show();
        } else {
            //Toast.makeText(this, "NFC is active!", Toast.LENGTH_LONG).show();
        }

        handleIntent(getIntent());

        // Setup realm on the main thread and get Realm data
        setupRealm();
        setupListView();

        // delete all records from RFIDScan table (to prevent cluttering)
//        mRealm.executeTransaction(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                realm.delete(RFIDScan.class);
//            }
//        });

        /*
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
        */

        setupArrowButtons();
        updateSelectedDateText();
        setupFragmentInteractions();

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

        } /*else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(TAG, "Scan triggered TECH_DISCOVERED");
            // TODO: Properly treat when TAG_DISCOVERED

            String placeholder = "NDEF payload detected, but cannot be mapped to a MIME type or URI!";
            Toast.makeText(this, placeholder, Toast.LENGTH_LONG).show();

        }*/
        // moved ACTION_TECH_DISCOVERED here so NfcV tags can be properly processed with our code
        else if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action) || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Log.d(TAG, "No chip technology found, but there is a tag!");

            byte[] extracted_bytes = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            String converted_string;

            try {
                converted_string = Utility.bytesToHex(extracted_bytes);
            } catch (Exception e) {
                converted_string = "Failed to convert!";
            }

            // make sure RFID tag is associated with an address before pushing it to Realm
            String address = getAddressFromRFIDValue(converted_string);
            if (address == BLANK_ADDRESS) {
                Log.d(TAG, "Tag not linked to an address");
                notifyUserScanError(SCAN_NA_ADDRESS_MESSAGE);
                return;
            }

            // Push the scanned value into the server
            boolean hasPushed = RealmPushData(converted_string);

            // Update the current main text by changing the data and notifying the adapter
            if (hasPushed) {
                String location = mDeviceLocationManager.getLocationString();

                if (mCustomAdapter.addCollectedAddress(address)) {
                    mCustomAdapter.notifyDataSetChanged();
                    updateMissingBinsText();

                    notifyUserScanSuccessful();
                } else {
                    notifyUserScanError(SCAN_DUPLICATE_MESSAGE);
                }

                setFragmentPage(0);
            }
            else {
                Log.d(TAG, "Cannot push scanned data to Realm server.");
            }
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

        // add NfcV polling (NfcA already included by default)
        String[][] techList = new String[1][1];
        techList[0][0] = NFCV;

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

    private void setupRealm(){
        mUser = getLoggedInUser();

        if (mUser != null) {

            // Create a Realm Configuration
            SyncConfiguration config = new SyncConfiguration.Builder(mUser, mRealmUrl)
                    .build();

            mRealm = Realm.getInstance(config);

            if (mRealm != null) {
                Log.d(TAG, "We got a Realm instance!");
                Log.d(TAG, mRealm.toString());
                Log.d(TAG, mRealm.getPath());
            } else {
                Log.e(TAG, "Failed at setupRealm()");
            }

        }
    }

    private void setupListView() {
        // need to setup calendar before doing date operations for Realm queries
        setupCalendar();

        mListView = (ListView) findViewById(R.id.listview_scan_log);

        try {
            updateDisplayedData();
        } catch (Exception e) {
            Log.e(TAG, "setupListView()");
            Log.e(TAG, e.toString());
        }
    }

    private void setupCalendar() {
        calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendarDateFormat = new SimpleDateFormat(CALENDAR_DATE_FORMAT_STRING);
    }

    private void updateDisplayedData() {
        mParsedScanData = getAllAddressNames();
        setArrayAdapter();
        updateMissingBinsText();
    }

    private void setArrayAdapter() {
        mCustomAdapter = new CustomAdapter(
                this, mParsedScanData
        );

        mCustomAdapter.setCollectedAddresses(getCollectedAddressNames());

        mListView.setAdapter(mCustomAdapter);
    }

    private void updateMissingBinsText() {
        TextView textView = (TextView) findViewById(R.id.listview_header);
        long addressCount = mRealm.where(Address.class).count();

        StringBuilder builder = new StringBuilder();
        builder.append("Missing Bins - (")
                .append(mCustomAdapter.getCount() - mCustomAdapter.getCollectedAddressCount())
                .append("/")
                .append(addressCount)
                .append(")");

        textView.setText(builder.toString());
    }

    public ArrayList<String> getAllAddressNames() {
        RealmResults<Address> results = mRealm.where(Address.class).findAll();

        ArrayList<String> addressNames = new ArrayList<String>();
        for (Address result : results) {
            String addressName = result.getAddress();
            if (!addressNames.contains(addressName)) {
                addressNames.add(addressName);
            }
        }

        return addressNames;
    }


    public ArrayList<Address> getObjFromAddressName(String addressName) {

        try {
            if (mCurrentRealmAddresses == null) {
                mCurrentRealmAddresses = mRealm.where(Address.class)
                        .findAll();
            }

            // There can be multiple addresses with the same name (e.g. in the US), therefore return all
            ArrayList<Address> sameNameAddresses = new ArrayList<Address>();
            for (Address address : mCurrentRealmAddresses) {
                if (address.getAddress().equals(addressName)) {
                    sameNameAddresses.add(address);
                }
            }

            return sameNameAddresses;

        } catch (Exception e) {
            Log.e(TAG, "getObjFromAddressName()");
            Log.e(TAG, e.toString());
        }

        return null;
    }


    public ArrayList<String> getCollectedAddressNames() {
        ArrayList<String> rfidValues = getRFIDValuesForSelectedDate();
        return getAddressesForRFIDValues(rfidValues);
    }

    private ArrayList<String> getRFIDValuesForSelectedDate() {
        Date baseSelectedDate = getBaseSelectedDate();
        Date baseNextDate = getDateAfterBaseSelectedDate();

        RealmResults<RFIDScan> results = mRealm.where(RFIDScan.class)
                .greaterThanOrEqualTo("Timestamp", baseSelectedDate)
                .lessThanOrEqualTo("Timestamp", baseNextDate)
                .findAll();

        ArrayList<String> scannedRFIDValues = new ArrayList<String>();
        for (RFIDScan result : results) {
            String rfidValue = result.getRFIDValue();
            if (!scannedRFIDValues.contains(rfidValue)) {
                scannedRFIDValues.add(rfidValue);
            }
        }

        return scannedRFIDValues;
    }

    private Date getBaseSelectedDate() {
        return new Date((calendar.get(Calendar.YEAR) - CALENDAR_YEAR_OFFSET),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
    }

    private Date getDateAfterBaseSelectedDate() {
        return new Date((calendar.get(Calendar.YEAR) - CALENDAR_YEAR_OFFSET),
                        calendar.get(Calendar.MONTH),
                        (calendar.get(Calendar.DAY_OF_MONTH) + 1));
    }

    private ArrayList<String> getAddressesForRFIDValues(ArrayList<String> rfidValues) {
        // only use this if we are listing scanned addresses, instead we are listing unscanned addresses
        if (rfidValues.size() == 0){
            return new ArrayList<String>();
        }

        RealmQuery<Address> addressQuery = mRealm.where(Address.class);
        int i = 0;
        for (String rfidValue : rfidValues) {
            if (i > 0) {
                addressQuery = addressQuery.or();
            }
            addressQuery = addressQuery.equalTo("RFIDValue", rfidValue);
            i++;
        }

        RealmResults<Address> addressObjects = addressQuery.findAll();
        ArrayList<String> addresses = new ArrayList<String>();
        for (Address addressObj : addressObjects) {
            addresses.add(addressObj.getAddress());
        }

        return addresses;
    }

    private String getAddressFromRFIDValue(String rfidValue) {
        ArrayList<String> singleRFIDList = new ArrayList<String>();
        singleRFIDList.add(rfidValue);

        ArrayList<String> singleAddressList =  getAddressesForRFIDValues(singleRFIDList);
        if (singleAddressList.size() > 0)
            return singleAddressList.get(0);
        else
            return BLANK_ADDRESS;
    }

    public ArrayList<String> getMissingAddressNames() {
        ArrayList<String> addressNames = getAllAddressNames();
        ArrayList<String> collectedAddressNames = getCollectedAddressNames();

        for (String addressName : collectedAddressNames) {
            addressNames.remove(addressName);
        }

        return addressNames;
    }

//    public ArrayList<String> getAddresses() {
//        return addresses;
//    }

//    private String getParsedResult(RFIDScan scan) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String timestampStr = sdf.format(scan.getTimestamp());
//
//        return getParsedResult(timestampStr,
//                               scan.getRFIDValue(),
//                               mDeviceLocationManager.getLocationString());
//    }
//
//    private String getParsedResult(String timestamp, String RFIDValue, String location) {
//        return timestamp + "\n\n" +
//                "Tag Value: " + RFIDValue + "\n" +
//                "Location: " + location;
//    }

    private void notifyUserScanSuccessful() {
        int vibrationLengthMilliseconds = 750;
        vibratePhone(vibrationLengthMilliseconds);
        displaySnackbar(SCAN_SUCCESSFUL_MESSAGE);
    }

    private void notifyUserScanError(String message) {
        long[] vibrationPattern = {0, 250, 250, 250};
        vibratePhone(vibrationPattern);
        displaySnackbar(message);
    }

    private void vibratePhone(int lengthMilliseconds) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(lengthMilliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(lengthMilliseconds);
        }
    }

    private void vibratePhone(long[] vibratePattern) {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(vibratePattern, -1);
    }

    private void displaySnackbar(String text) {
        Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), text, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void setupArrowButtons() {
        ImageButton leftButton = (ImageButton) findViewById(R.id.left_arrow_button);
        setupArrowButtonOnClickListener(leftButton, -1);

        ImageButton rightButton = (ImageButton) findViewById(R.id.right_arrow_button);
        setupArrowButtonOnClickListener(rightButton, 1);
    }

    private void setupArrowButtonOnClickListener(ImageButton button, final int incrementValue) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.DATE, incrementValue);
                updateSelectedDateText();

                updateDisplayedData();
            }
        });
    }

    private void updateSelectedDateText() {
        TextView dateTextView = (TextView) findViewById(R.id.date_text);
        dateTextView.setText(calendarDateFormat.format(calendar.getTime()));
    }

    /** Called when the user touches the "SHOW IN MAP" button */
    public void openMap(View view) {
        // Do something in response to button click
    }

    private SyncUser getLoggedInUser() {
        SyncUser user = null;

        try {
            user = SyncUser.currentUser();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.getMessage());
        }

        if (user == null) {
            Log.e(TAG, "Failed at getLoggedInUser()");
        }

        return user;
    }

    // Push a scanned data to the Realm object server
    public boolean RealmPushData(String newScannedValue) {

        if (mUser != null && mRealm != null && newScannedValue != null) {
            scannedValue = newScannedValue;
            return executeRealmTransaction();
        }
        return false;
    }

    private boolean executeRealmTransaction() {
        try {
            mRealm.executeTransaction(new Realm.Transaction() {

                @Override
                public void execute(Realm realm) {
                    RFIDScan testPair = new RFIDScan();
                    testPair.setRFIDValue(scannedValue);
                    testPair.setTimestamp(new Date());
                    realm.insert(testPair);
                }
            });

            Log.d(TAG, "Pushing string data " + scannedValue + " to Realm");
        } catch (Exception e) {
            Log.e(TAG, "Failed at executeRealmTransaction()");
            Log.e(TAG, e.toString());
            return false;
        }

        return true;
    }


    // Close Realm if it's not already closed.
    private void closeRealm() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mAllFragments.get(position);
        }

        @Override
        public int getCount() {
            return mAllFragments.size();
        }
    }

    // Setup everything needed for the fragments
    private void setupFragmentInteractions(){
        // Set up the fragment interactions
        mAllFragments = new ArrayList<Fragment>();

        // Set up the "list fragment"
        mEmptyFragment = new EmptyFragment();

        // Set up a Main Page fragment
        mMainFragment = new MainFragment();

        // Set up map fragment
        mMapFragment = new MapManagerFragment();

        // Push in all the map fragments
        mAllFragments.add(mEmptyFragment);
        mAllFragments.add(mMainFragment);
        mAllFragments.add(mMapFragment);



        // Instantiate a ViewPager and a PagerAdapter.
        mViewpager = (NonSwipePageViewer) findViewById(R.id.general_view_pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mViewpager.setAdapter(mPagerAdapter);
        mViewpager.setOffscreenPageLimit(3);
        setFragmentPage(1);

    }

    // Programmatically set the page
    public void setFragmentPage(int page){
        mViewpager.setCurrentItem(page);
    }

    @Override
    public void onBackPressed(){
        setFragmentPage(1);
    }
}

//JD Test Commit