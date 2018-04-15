package com.master.bojan.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by bojan on 15.4.18..
 */

public class EventsProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private EventsDbHelper mDbHelper;
    private static final SQLiteQueryBuilder sEventsQueryBuilder;

    static {
        sEventsQueryBuilder = new SQLiteQueryBuilder();
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new EventsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case EVENTS: {
                retCursor = mDbHelper.getReadableDatabase().query(EventContract.EventEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case EVENT_BY_EVENTFUL_ID: {
                String eventfulId = EventContract.EventEntry.getEventfulIdFromUri(uri);
                retCursor = QueryHandler.getEventWithImagesById(mDbHelper, new String[]{eventfulId});
                break;
            }
            case EVENTS_BY_CATEGORY: {
                retCursor = QueryHandler.getCategoryLocationEvents(sEventsQueryBuilder, mDbHelper, projection, selectionArgs, sortOrder, false);
                break;
            }
            case EVENTS_CAT_LOC_BETWEEN_DATES_ID: {
                retCursor = QueryHandler.getCategoryLocationEvents(sEventsQueryBuilder, mDbHelper, projection, selectionArgs, sortOrder, true);
                break;
            }
            case EVENTS_ADVANCED_SEARCH: {
                retCursor = QueryBuilder.getAdvancedSearchResults(sEventsQueryBuilder, mDbHelper, uri, projection, selection, selectionArgs, sortOrder);

                break;
            }
            case EVENTS_FILTER: {
                retCursor = QueryHandler.getFilteredEvents(sEventsQueryBuilder, mDbHelper, projection, selectionArgs, sortOrder, false);
                break;
            }
            case EVENTS_FILTER_BETWEEN_DATES: {
                retCursor = QueryHandler.getFilteredEvents(sEventsQueryBuilder, mDbHelper, projection, selectionArgs, sortOrder, true);
                break;
            }
            case LOCATIONS: {
                retCursor = mDbHelper.getReadableDatabase().query(EventContract.LocationEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);

                break;

            }

            case CATEGORIES: {
                retCursor = mDbHelper.getReadableDatabase().query(EventContract.CategoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            }

            case PRIMARY_CATEGORIES: {
                retCursor = QueryHandler.getPrimaryCategories(sEventsQueryBuilder, mDbHelper, projection, sortOrder);
                break;
            }
            case EVENT_CATEGORY: {
                retCursor = mDbHelper.getReadableDatabase().query(EventContract.Event_CategoryEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;

            }
            default: {
                Log.e("error", "Unknown uri: " + uri);

                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

        }

        //register uri changes and notify corresponding resolver
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        //return MIME type
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case EVENTS:
                return EventContract.EventEntry.CONTENT_TYPE;
            case EVENT:
                return EventContract.EventEntry.CONTENT_ITEM_TYPE;
            case EVENT_BY_EVENTFUL_ID:
                return EventContract.EventEntry.CONTENT_ITEM_TYPE;
            case EVENTS_CAT_LOC_BETWEEN_DATES_ID:
                return EventContract.EventEntry.CONTENT_TYPE;
            case EVENTS_ADVANCED_SEARCH:
                return EventContract.EventEntry.CONTENT_TYPE;
            case EVENTS_FILTER:
                return EventContract.EventEntry.CONTENT_TYPE;
            case EVENTS_FILTER_BETWEEN_DATES:
                return EventContract.EventEntry.CONTENT_TYPE;

            case LOCATIONS:
                return EventContract.LocationEntry.CONTENT_TYPE;

            case CATEGORIES:
                return EventContract.CategoryEntry.CONTENT_TYPE;
            case EVENTS_BY_CATEGORY:
                return EventContract.CategoryEntry.CONTENT_TYPE;
            case EVENT_CATEGORY:
                return EventContract.Event_CategoryEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int matcher = sUriMatcher.match(uri);

        Uri retUri;
        switch (matcher) {
            case EVENTS: {
                long id = db.insertWithOnConflict(EventContract.EventEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0)
                    retUri = EventContract.EventEntry.buildEventUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case LOCATIONS: {
                long id = db.insertWithOnConflict(EventContract.LocationEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0)
                    retUri = EventContract.LocationEntry.buildLocationUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case CATEGORIES: {
                long id = db.insertWithOnConflict(EventContract.CategoryEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0)
                    retUri = EventContract.CategoryEntry.buildCategoryUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;
            }
            case EVENT_CATEGORY: {
                long id = db.insertWithOnConflict(EventContract.Event_CategoryEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (id > 0)
                    retUri = EventContract.Event_CategoryEntry.buildEvent_CategoryEntryUri(id);
                else
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int matcher = sUriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (matcher) {
            case EVENTS: {
                rowsDeleted = db.delete(EventContract.EventEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOCATIONS: {
                rowsDeleted = db.delete(EventContract.LocationEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case CATEGORIES: {
                rowsDeleted = db.delete(EventContract.CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case EVENT_CATEGORY: {
                rowsDeleted = db.delete(EventContract.Event_CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted > 0)
            this.getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int matcher = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (matcher) {
            case EVENTS: {
                rowsUpdated = db.update(EventContract.EventEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case LOCATIONS: {
                rowsUpdated = db.update(EventContract.LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case CATEGORIES: {
                rowsUpdated = db.update(EventContract.CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case EVENT_CATEGORY: {
                rowsUpdated = db.update(EventContract.Event_CategoryEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated > 0)
            this.getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    //for multi insert
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(EventContract.EventEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case CATEGORIES: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(EventContract.CategoryEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case EVENT_CATEGORY: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insertWithOnConflict(EventContract.Event_CategoryEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;


            }
            default:
                return super.bulkInsert(uri, values);
        }
    }


    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = EventContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, EventContract.PATH_EVENT, EVENTS);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/by_category_dates_location", EVENTS_CAT_LOC_BETWEEN_DATES_ID);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/by_category_location", EVENTS_BY_CATEGORY);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/eventful/*", EVENT_BY_EVENTFUL_ID);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/filter", EVENTS_FILTER);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/filter_between_dates", EVENTS_FILTER_BETWEEN_DATES);

        // matcher.addURI(authority, EventContract.PATH_EVENT + "/DATE/#", EVENTS_FOR_START_DATE);
        matcher.addURI(authority, EventContract.PATH_EVENT + "/#", EVENT);
        // matcher.addURI(authority, EventContract.PATH_EVENT + "/eventfulID/location/*", EVENTS_BY_CATEGORY);

        matcher.addURI(authority, EventContract.PATH_EVENT + "/search", EVENTS_ADVANCED_SEARCH);


        matcher.addURI(authority, EventContract.PATH_LOCATION, LOCATIONS);
        matcher.addURI(authority, EventContract.PATH_CATEGORY, CATEGORIES);
        matcher.addURI(authority, EventContract.PATH_CATEGORY + "/isPrimary", PRIMARY_CATEGORIES); //1 is true, 0 is false
        matcher.addURI(authority, EventContract.PATH_EVENT_CATEGORY, EVENT_CATEGORY);


        return matcher;

    }


    static final int EVENTS = 100;
    static final int EVENT = 102;
    static final int EVENTS_BY_CATEGORY = 103;
    static final int EVENT_BY_EVENTFUL_ID = 104;
    static final int EVENTS_CAT_LOC_BETWEEN_DATES_ID = 105;
    static final int EVENTS_ADVANCED_SEARCH = 106;
    static final int EVENTS_FILTER = 107;
    static final int EVENTS_FILTER_BETWEEN_DATES = 108;
    static final int LOCATIONS = 300;
    static final int CATEGORIES = 400;
    static final int PRIMARY_CATEGORIES = 401;
    static final int EVENT_CATEGORY = 500;
}
