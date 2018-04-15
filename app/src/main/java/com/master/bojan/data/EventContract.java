package com.master.bojan.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by bojan on 15.4.18..
 */

public class EventContract {

    public static final String CONTENT_AUTHORITY = "com.master.bojan.java";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_EVENT = "event";
    public static final String PATH_LOCATION = "location";
    public static final String PATH_CATEGORY = "category";
    public static final String PATH_EVENT_CATEGORY = "event_category";

    public static final class EventEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT).build();
        //for multiple data
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_EVENT;
        //for item
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_EVENT;

        public static final String TABLE_NAME = "events";
        public static final String COLUMN_LOCATION_ID = "location_id";
        public static final String COLUMN_VENUE_NAME = "venue_name";
        public static final String COLUMN_EVENTFUL_ID = "eventful_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_URL = "event_url";
        public static final String COLUMN_VENUE_URL = "venue_url";
        public static final String COLUMN_GOING_COUNT = "going_count";
        public static final String COLUMN_WATCHING_COUNT = "watching_count";
        public static final String COLUMN_COMMENT_COUNT = "comment_count";
        public static final String COLUMN_CREATED_TIME = "created_time";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_STOP_TIME = "stop_time";
        public static final String COLUMN_SMALL_IMAGE_URL = "small_image_url";
        public static final String COLUMN_MEDIUM_IMAGE_URL = "medium_image_url";
        public static final String COLUMN_THUMB_IMAGE_URL = "thumb_image_url";
        public static final String COLUMN_CORD_LAT = "cord_latitude";
        public static final String COLUMN_CORD_LONG = "cord_longitude";


        public static Uri buildEventUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEventfulUri(String id) {
            return CONTENT_URI.buildUpon().appendPath("eventful").appendPath(id).build();
        }

        public static Uri buildSearchUri() {
            return CONTENT_URI.buildUpon().appendPath("search").build();
        }

        public static Uri buildFilterUri() {
            return CONTENT_URI.buildUpon().appendPath("filter").build();
        }

        public static Uri buildFilterBetweenDatesUri() {
            return CONTENT_URI.buildUpon().appendPath("filter_between_dates").build();
        }

        public static String getEventfulIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static Uri buildCategoryLocationEventfulUri() {
            return CONTENT_URI.buildUpon().appendPath("by_category_location").build();
        }

        public static Uri buildCategoryLocationEventsUriBetweenDates() {
            return CONTENT_URI.buildUpon().appendPath("by_category_dates_location").build();
        }

    }

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CATEGORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_CATEGORY_NAME = "name";
        public static final String COLUMN_EVENTFUL_ID = "cat_eventful_id";
        public static final String COLUMN_IMAGE_PATH = "image";
        public static final String COLUMN_IS_PRIMARY_CATEGORY = "is_primary_category";


        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCategoryEventfulUri(String eventfulId) {
            return CONTENT_URI.buildUpon().appendPath("eventfulID").appendPath(eventfulId).build();  //1 is true
        }

        public static Uri buildPrimaryCategoriesUri() {
            return CONTENT_URI.buildUpon().appendPath("isPrimary").build();  //1 is true
        }

        public static int getIsPrimaryFromUri(Uri uri) {
            return Integer.parseInt(uri.getPathSegments().get(2));
        }

        public static String getEventfulIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static final class Event_CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_EVENT_CATEGORY).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_CATEGORY;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EVENT_CATEGORY;

        // Table name
        public static final String TABLE_NAME = "event_category";
        public static final String COLUMN_EVENTFUL_CATEGORY_ID = "category_eventful_id";
        public static final String COLUMN_EVENT_ID = "event_id";

        public static Uri buildEvent_CategoryEntryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_COUNTRY_NAME = "country_name";
        public static final String COLUMN_REGION_NAME = "region_name";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
