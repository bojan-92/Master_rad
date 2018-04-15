package com.master.bojan.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.master.bojan.R;
import com.master.bojan.data.EventContract;
import com.master.bojan.util.Constant;
import com.master.bojan.util.Custom;
import com.master.bojan.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by bojan on 15.4.18..
 */

public class EventsSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;


    public EventsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        /*if(extras.getDouble(Constant.LONGITUDE_EXTRA) == 0 && extras.getDouble(Constant.LATITUDE_EXTRA) ==0)
        {
            return;
        }*/

        String categoryQuery = extras.getString(Constant.CATEGORY_EXTRA);
       /* String longitude = String.valueOf(extras.getDouble(Constant.LONGITUDE_EXTRA));
        String latitude = String.valueOf(extras.getDouble(Constant.LATITUDE_EXTRA));
        String radius = String.valueOf(extras.getDouble(Constant.RADIUS_EXTRA));
*/
        String city = extras.getString(Constant.LOCATION_EXTRA);

        if(city == null || city.equals(""))
            return;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String jsonResponse = null;

        final String EVENTFUL_BASE_URI = "http://api.eventful.com/json/events/search";
        final String CATEGORY_KEY = "category";
        final String APP_KEY = "app_key";
        final String LOCATION_KEY = "where";
        final String RADIUS_KEY = "within";
        ArrayList<String> categories= new ArrayList<String>();
        categories.addAll(Arrays.asList(Custom.primary_category_ids));

        //  String locationQuery = latitude+","+longitude;
        try {

            for (String category : categories) {
               /* Uri builtUri = Uri.parse(EVENTFUL_BASE_URI).buildUpon().appendQueryParameter(CATEGORY_KEY, category)
                        .appendQueryParameter(LOCATION_KEY, locationQuery).
                                appendQueryParameter(RADIUS_KEY, radius).
                                appendQueryParameter(APP_KEY, Custom.API_KEY)
                        .build();*/
                Uri builtUri = Uri.parse(EVENTFUL_BASE_URI).buildUpon().appendQueryParameter(CATEGORY_KEY, category)
                        .appendQueryParameter(LOCATION_KEY, city).
                                appendQueryParameter(APP_KEY, Custom.API_KEY)
                        .build();
                Log.i("async", "url " + builtUri.toString());

                URL url = new URL(builtUri.toString());
                // Create the request to Eventful, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //open inputStream
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) return;
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) return; //empty response

                jsonResponse = buffer.toString();
                getEventDataFromJson(jsonResponse, category);
            }
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            Log.e("async", "error parsing response");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("async", "Error closing stream", e);
                }
            }
        }
    }


    private void getEventDataFromJson(String jsonResponse, String category) {

        final String ID = "id";
        final String TITLE = "title";
        final String DESCRIPTION = "description";
        final String CITY_NAME = "city_name";
        final String COUNTRY_NAME = "country_name";
        final String REGION_NAME = "region_name";
        final String START_TIME = "start_time";
        final String CREATED_TIME = "created";
        final String STOP_TIME = "stop_time";
        final String EVENTFUL_URL = "url";
        final String VENUE_NAME = "venue_name";
        final String LONGITUDE = "longitude";
        final String LATITUDE = "latitude";
        final String WATCHING_COUNDT = "watching_count";
        final String COMMENT_COUNT = "comment_count";
        final String GOING_COUNT = "going_count";
        final String VENUE_URL = "venue_url";
        final String IMAGE = "image";
        final String SMALL_IMAGE = "small";
        final String MEDIUM_IMAGE = "medium";
        final String THUMB_IMAGE = "thumb";


        final String EVENTS_LIST = "events";
        final String EVENT = "event";

        try {

            JSONObject eventsJson = new JSONObject(jsonResponse);
            JSONObject eventsArrObject = eventsJson.getJSONObject(EVENTS_LIST);
            JSONArray eventsArray = eventsArrObject.getJSONArray(EVENT);

            // Insert the new events information into the database
            Vector<ContentValues> eventsVector = new Vector<ContentValues>(eventsArray.length());

            for (int i = 0; i < eventsArray.length(); i++) {

                JSONObject event = eventsArray.getJSONObject(i);

                //location data
                double longitude = 0;
                double latitude = 0;
                if (event.get(LONGITUDE) != null)
                    longitude = event.getDouble(LONGITUDE);
                if (event.get(LATITUDE) != null)
                    latitude = event.getDouble(LATITUDE);
                String cityName = event.getString(CITY_NAME);
                String countryName = event.getString(COUNTRY_NAME);
                String regionName = event.getString(REGION_NAME);


                Long locationId = insertLocation(longitude, latitude, cityName, countryName, regionName);

                //images data
                Long imageId = null;
                String smallUrl = null;
                String mediumUrl = null;
                String thumbUrl = null;

                if (!event.isNull(IMAGE)) {
                    JSONObject image = event.getJSONObject(IMAGE);

                    if (!image.isNull(SMALL_IMAGE)) {
                        JSONObject smallImage = image.getJSONObject(SMALL_IMAGE);

                        smallUrl = smallImage.getString(EVENTFUL_URL);
                    }
                    if (!image.isNull(MEDIUM_IMAGE)) {

                        JSONObject mediumImage = image.getJSONObject(MEDIUM_IMAGE);

                        mediumUrl = mediumImage.getString(EVENTFUL_URL);
                    }
                    if (!image.isNull(THUMB_IMAGE)) {

                        JSONObject thumbImage = image.getJSONObject(THUMB_IMAGE);
                        thumbUrl = thumbImage.getString(EVENTFUL_URL);
                    }

                }


                //event data
                String venueName = event.getString(VENUE_NAME);
                String id = event.getString(ID);
                String title = event.getString(TITLE);
                String desc = event.getString(DESCRIPTION);
                String venueUrl = event.getString(VENUE_URL);
                String urlEventful = event.getString(EVENTFUL_URL);

                double goingCount = 0;
                if (!event.getString(GOING_COUNT).equals("null"))
                    goingCount = event.getDouble(GOING_COUNT);

                int commentCount = 0;
                if (!event.getString(COMMENT_COUNT).equals("null"))
                    commentCount = event.getInt(COMMENT_COUNT);

                int watchingCount = 0;
                if (!event.getString(WATCHING_COUNDT).equals("null"))
                    watchingCount = event.getInt(WATCHING_COUNDT);
                //PERFORMERS //groups //all_day //image //commentsz
                //dates

                Long created = null;
                if (!event.getString(CREATED_TIME).equals("null"))
                    created = Utility.stringToDateTime(event.getString(CREATED_TIME));

                Long startTime = null;
                if (!event.getString(START_TIME).equals("null"))
                    startTime = Utility.stringToDateTime(event.getString(START_TIME));

                Long stopTime = null;
                if (!event.getString(STOP_TIME).equals("null"))
                    stopTime = Utility.stringToDateTime(event.getString(STOP_TIME));

                ContentValues eventValues = new ContentValues();
                eventValues.put(EventContract.EventEntry.COLUMN_LOCATION_ID, locationId);

                eventValues.put(EventContract.EventEntry.COLUMN_TITLE, title);
                eventValues.put(EventContract.EventEntry.COLUMN_DESCRIPTION, desc);
                eventValues.put(EventContract.EventEntry.COLUMN_EVENTFUL_ID, id);
                eventValues.put(EventContract.EventEntry.COLUMN_VENUE_NAME, venueName);
                eventValues.put(EventContract.EventEntry.COLUMN_VENUE_URL, venueUrl);
                eventValues.put(EventContract.EventEntry.COLUMN_URL, urlEventful);
                eventValues.put(EventContract.EventEntry.COLUMN_GOING_COUNT, goingCount);
                eventValues.put(EventContract.EventEntry.COLUMN_WATCHING_COUNT, watchingCount);
                eventValues.put(EventContract.EventEntry.COLUMN_COMMENT_COUNT, commentCount);
                eventValues.put(EventContract.EventEntry.COLUMN_CREATED_TIME, created);
                eventValues.put(EventContract.EventEntry.COLUMN_START_TIME, startTime);
                eventValues.put(EventContract.EventEntry.COLUMN_STOP_TIME, stopTime);
                eventValues.put(EventContract.EventEntry.COLUMN_SMALL_IMAGE_URL, smallUrl);
                eventValues.put(EventContract.EventEntry.COLUMN_MEDIUM_IMAGE_URL, mediumUrl);
                eventValues.put(EventContract.EventEntry.COLUMN_THUMB_IMAGE_URL, thumbUrl);
                eventValues.put(EventContract.EventEntry.COLUMN_CORD_LAT, latitude);
                eventValues.put(EventContract.EventEntry.COLUMN_CORD_LONG, longitude);


                eventsVector.add(eventValues);

            }
            if (eventsVector.size() > 0) {
               /* ContentValues[] cvArray = new ContentValues[eventsVector.size()];         //izbacen bulk insert zbok kategorija
                eventsVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(EventContract.EventEntry.CONTENT_URI, cvArray);*/
                Vector<ContentValues> eventCategoryVector = new Vector<ContentValues>();
                long id = -1;
                for (ContentValues event : eventsVector) {
                    Uri insertedUri = getContext().getContentResolver().insert(EventContract.EventEntry.CONTENT_URI,
                            event);
                    id = ContentUris.parseId(insertedUri);
                    ContentValues eventCat = new ContentValues();
                    eventCat.put(EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID, category);
                    eventCat.put(EventContract.Event_CategoryEntry.COLUMN_EVENT_ID, id);
                    eventCategoryVector.add(eventCat);
                }

                if (eventCategoryVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[eventCategoryVector.size()];
                    eventCategoryVector.toArray(cvArray);
                    getContext().getContentResolver().bulkInsert(EventContract.Event_CategoryEntry.CONTENT_URI, cvArray);
                }

            }


        } catch (JSONException e) {
            //e.printStackTrace();
            Log.e("json", "error parsing response");
        }


    }

    private long insertLocation(double longitude, double latitude, String cityName, String countryName, String regionName) {
        long id = -1;

        String selection = EventContract.LocationEntry.COLUMN_CITY_NAME + " =?";
        Cursor cursor = getContext().getContentResolver().query(EventContract.LocationEntry.CONTENT_URI,
                new String[]{EventContract.LocationEntry._ID},
                selection, new String[]{cityName}, null);

        if (cursor.moveToFirst()) {
            int locationIdIndex = cursor.getColumnIndex(EventContract.LocationEntry._ID);
            id = cursor.getLong(locationIdIndex);
        } else {
            ContentValues locValues = new ContentValues();
            locValues.put(EventContract.LocationEntry.COLUMN_CITY_NAME, cityName);
            locValues.put(EventContract.LocationEntry.COLUMN_COUNTRY_NAME, countryName);
            locValues.put(EventContract.LocationEntry.COLUMN_REGION_NAME, regionName);
            // Finally, insert location data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(EventContract.LocationEntry.CONTENT_URI,
                    locValues);
            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            id = ContentUris.parseId(insertedUri);
        }
        cursor.close();
        return id;
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        Log.e("async", "configurePeriodicSync " + syncInterval);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
   /* public static void syncImmediately(Context context,double latitude, double longitude, double radius) {
        Bundle bundle = new Bundle();
        Log.i("async", "on perform syncImmediately ");
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putDouble(Constant.LATITUDE_EXTRA, latitude);
        bundle.putDouble(Constant.LONGITUDE_EXTRA, longitude);
        bundle.putDouble(Constant.RADIUS_EXTRA,radius);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }*/

    public static void syncImmediately(Context context, String city) {
        Bundle bundle = new Bundle();
        Log.i("async", "on perform syncImmediately ");
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        bundle.putString(Constant.LOCATION_EXTRA  ,city);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with Eventful, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        int preferredSyncInterval = Utility.getPreferredSyncInterval(context);
        if (preferredSyncInterval > 0)
            EventsSyncAdapter.configurePeriodicSync(context, preferredSyncInterval * 60, (preferredSyncInterval * 60) / 3);
        else
            EventsSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        //syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
