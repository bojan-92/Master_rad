package com.master.bojan.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;

import com.master.bojan.model.QuerySelectionPair;

/**
 * Created by bojan on 15.4.18..
 */

public class QueryHandler {

    public static Cursor getCategoryLocationEvents(SQLiteQueryBuilder sEventsQueryBuilder,
                                                   EventsDbHelper mDbHelper,
                                                   String[] projection,
                                                   String[] selectionArgs,
                                                   String sortOrder, boolean checkBetweenDates) {
        QuerySelectionPair querySelectionPair = QueryBuilder.getByLocationAndCategory(checkBetweenDates);
        sEventsQueryBuilder.setTables(querySelectionPair.getTablesJoin());

        return sEventsQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, querySelectionPair.getQuerySelection(),
                selectionArgs, null, null, sortOrder);

    }

    public static Cursor getFilteredEvents(SQLiteQueryBuilder sEventsQueryBuilder,
                                           EventsDbHelper mDbHelper,
                                           String[] projection,
                                           String[] selectionArgs,
                                           String sortOrder, boolean checkBetweenDates) {
        QuerySelectionPair querySelectionPair = QueryBuilder.getFilterResults(checkBetweenDates);
        sEventsQueryBuilder.setTables(querySelectionPair.getTablesJoin());
        return sEventsQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, querySelectionPair.getQuerySelection(),
                selectionArgs, null, null, sortOrder);

    }

    public static Cursor getPrimaryCategories(SQLiteQueryBuilder sEventsQueryBuilder,
                                              EventsDbHelper mDbHelper,
                                              String[] projection,
                                              String sortOrder) {
        String isPrimaryS = "1";
        QuerySelectionPair querySelectionPair = QueryBuilder.getPrimaryCategories();
        sEventsQueryBuilder.setTables(querySelectionPair.getTablesJoin());
        return sEventsQueryBuilder.query(mDbHelper.getReadableDatabase(), projection, querySelectionPair.getQuerySelection()
                , new String[]{isPrimaryS}, null, null, sortOrder);

    }


    public static Cursor getEventWithImagesById(EventsDbHelper mDbHelper, String[] selectionArgs) {
        String selection = EventContract.EventEntry.TABLE_NAME + "."
                + EventContract.EventEntry.COLUMN_EVENTFUL_ID + " =?";
        String rawQuery = "SELECT *" + " FROM " + EventContract.EventEntry.TABLE_NAME + " WHERE " + selection;
        Cursor eventCursor = mDbHelper.getReadableDatabase().rawQuery(rawQuery, selectionArgs);
        return eventCursor;

    }
}
