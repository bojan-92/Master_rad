package com.master.bojan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by bojan on 15.4.18..
 */

public class EventsDbHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "events.db";

    public EventsDbHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //context.deleteDatabase(DATABASE_NAME);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + EventContract.LocationEntry.TABLE_NAME + " (" +
                EventContract.LocationEntry._ID + " INTEGER PRIMARY KEY," +
                EventContract.LocationEntry.COLUMN_CITY_NAME + " TEXT, " +
                EventContract.LocationEntry.COLUMN_COUNTRY_NAME + " TEXT,"+
                EventContract.LocationEntry.COLUMN_REGION_NAME + " TEXT, "+
                " UNIQUE (" +EventContract.LocationEntry.COLUMN_CITY_NAME+ ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE "+EventContract.CategoryEntry.TABLE_NAME + "("+
                EventContract.CategoryEntry._ID +" INTEGER PRIMARY KEY, "+
                EventContract.CategoryEntry.COLUMN_CATEGORY_NAME +" TEXT, "+
                EventContract.CategoryEntry.COLUMN_EVENTFUL_ID + " TEXT, "+
                EventContract.CategoryEntry.COLUMN_IS_PRIMARY_CATEGORY + " BOOLEAN, "+
                EventContract.CategoryEntry.COLUMN_IMAGE_PATH +" TEXT," +
                " UNIQUE (" + EventContract.CategoryEntry.COLUMN_EVENTFUL_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_CATEGORY_EVENT_TABLE = "CREATE TABLE "+EventContract.Event_CategoryEntry.TABLE_NAME + "("+
                EventContract.Event_CategoryEntry._ID +" INTEGER PRIMARY KEY, "+
                EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID +" TEXT, "+
                EventContract.Event_CategoryEntry.COLUMN_EVENT_ID + " INTEGER, "+
                " UNIQUE (" +EventContract.Event_CategoryEntry.COLUMN_EVENT_ID + ", " +
                EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID + ") ON CONFLICT REPLACE);";



        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE "+EventContract.EventEntry.TABLE_NAME+" ("+
                EventContract.EventEntry._ID + " INTEGER PRIMARY KEY," +
                EventContract.EventEntry.COLUMN_DESCRIPTION + "  TEXT," +
                EventContract.EventEntry.COLUMN_START_TIME + " INTEGER," +
                EventContract.EventEntry.COLUMN_CREATED_TIME + " INTEGER," +
                EventContract.EventEntry.COLUMN_STOP_TIME + " INTEGER," +
                EventContract.EventEntry.COLUMN_COMMENT_COUNT + " INTEGER," +
                EventContract.EventEntry.COLUMN_EVENTFUL_ID + " TEXT UNIQUE NOT NULL," +
                EventContract.EventEntry.COLUMN_GOING_COUNT + " INTEGER," +
                EventContract.EventEntry.COLUMN_WATCHING_COUNT + " INTEGER," +
                EventContract.EventEntry.COLUMN_URL + " TEXT," +
                EventContract.EventEntry.COLUMN_VENUE_NAME + " TEXT," +
                EventContract.EventEntry.COLUMN_VENUE_URL + " TEXT," +
                EventContract.EventEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                EventContract.EventEntry.COLUMN_LOCATION_ID + " INTEGER NOT NULL," +
                EventContract.EventEntry.COLUMN_MEDIUM_IMAGE_URL +" TEXT, "+
                EventContract.EventEntry.COLUMN_SMALL_IMAGE_URL + " TEXT, "+
                EventContract.EventEntry.COLUMN_THUMB_IMAGE_URL +" TEXT,"+
                EventContract.EventEntry.COLUMN_CORD_LAT + " REAL, " +
                EventContract.EventEntry.COLUMN_CORD_LONG + " REAL, " +

                // EventContract.EventEntry.COLUMN_IMAGE_ID + " LONG," +

                // Set up the location column as a foreign key to location table.
                " FOREIGN KEY (" + EventContract.EventEntry.COLUMN_LOCATION_ID + ") REFERENCES " +
                EventContract.LocationEntry.TABLE_NAME + " (" + EventContract.LocationEntry._ID + "), " +
                " UNIQUE (" + EventContract.EventEntry.COLUMN_EVENTFUL_ID + ") ON CONFLICT REPLACE);";




        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(SQL_CREATE_EVENT_TABLE);
        db.execSQL(SQL_CREATE_CATEGORY_EVENT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + EventContract.EventEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventContract.CategoryEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventContract.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EventContract.Event_CategoryEntry.TABLE_NAME);

        onCreate(db);
    }
}
