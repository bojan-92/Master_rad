package com.master.bojan.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.master.bojan.model.QuerySelectionPair;

/**
 * Created by bojan on 15.4.18..
 */

public class QueryBuilder {

    public static QuerySelectionPair getByLocationAndCategory(boolean checkBetweenDates) {
        QuerySelectionPair querySelectionPair = new QuerySelectionPair();
        querySelectionPair.setTablesJoin(
                EventContract.EventEntry.TABLE_NAME + " as E INNER JOIN " +
                        EventContract.Event_CategoryEntry.TABLE_NAME + " as EC ON E." +
                        EventContract.EventEntry._ID + " = EC." +
                        EventContract.Event_CategoryEntry.COLUMN_EVENT_ID +
                        " INNER JOIN " + EventContract.LocationEntry.TABLE_NAME + " as L ON " +
                        "L." + EventContract.LocationEntry._ID + " = E." +
                        EventContract.EventEntry.COLUMN_LOCATION_ID
        );

        if (checkBetweenDates) {
            querySelectionPair.setQuerySelection("EC." +
                    EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID + " =? AND L." +
                    EventContract.LocationEntry.COLUMN_CITY_NAME + " =? AND E." +
                    EventContract.EventEntry.COLUMN_START_TIME + "  BETWEEN ? AND ?"

            );
        } else {
            querySelectionPair.setQuerySelection("EC." +
                    EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID + " =? AND L." +
                    EventContract.LocationEntry.COLUMN_CITY_NAME + " =? "

            );
        }
        return querySelectionPair;
    }


    public static QuerySelectionPair getFilterResults(boolean checkBetweenDates) {
        QuerySelectionPair querySelectionPair = new QuerySelectionPair();
        querySelectionPair.setTablesJoin(EventContract.EventEntry.TABLE_NAME + " as E INNER JOIN " +
                EventContract.Event_CategoryEntry.TABLE_NAME +
                " as EC ON " + "E." + EventContract.EventEntry._ID +
                " = " + "EC." + EventContract.Event_CategoryEntry.COLUMN_EVENT_ID +
                " INNER JOIN " + EventContract.LocationEntry.TABLE_NAME + " as LOC ON " +
                "E." + EventContract.EventEntry.COLUMN_LOCATION_ID + " = " +
                "LOC. " + EventContract.LocationEntry._ID);

        if (checkBetweenDates) {
            querySelectionPair.setQuerySelection(EventContract.EventEntry.COLUMN_TITLE + " LIKE ? AND " +
                    EventContract.LocationEntry.COLUMN_CITY_NAME + " =? AND " +
                    EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID + " =? AND " +
                    EventContract.EventEntry.COLUMN_START_TIME + " BETWEEN ? AND ?"
            );
        } else {

            querySelectionPair.setQuerySelection(EventContract.EventEntry.COLUMN_TITLE + " LIKE ? AND " +
                    EventContract.LocationEntry.COLUMN_CITY_NAME + " =? AND " +
                    EventContract.Event_CategoryEntry.COLUMN_EVENTFUL_CATEGORY_ID + " =?"
            );
        }

        return querySelectionPair;
    }

    public static QuerySelectionPair getPrimaryCategories() {
        QuerySelectionPair querySelectionPair = new QuerySelectionPair();
        querySelectionPair.setTablesJoin(EventContract.CategoryEntry.TABLE_NAME);
        querySelectionPair.setQuerySelection(EventContract.CategoryEntry.TABLE_NAME + "."
                + EventContract.CategoryEntry.COLUMN_IS_PRIMARY_CATEGORY + "=?");

        return querySelectionPair;
    }

    public static Cursor getAdvancedSearchResults(SQLiteQueryBuilder sEventsQueryBuilder,
                                                  EventsDbHelper mDbHelper,
                                                  Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String fields = "";
        String projectionFields;
        int i = 0;
        for (String s : projection) {
            i++;
            fields += s;
            if (i != projection.length) {
                fields += " , ";
            }
        }
        if (fields.endsWith(","))
            projectionFields = fields.substring(0, fields.length() - 2);
        else
            projectionFields = fields;

        String rawQuery = "SELECT " + projectionFields + " FROM " +
                EventContract.EventEntry.TABLE_NAME + " as E INNER JOIN " +
                EventContract.Event_CategoryEntry.TABLE_NAME +
                " as EC ON " +
                "E." + EventContract.EventEntry._ID +
                " = " +
                "EC." + EventContract.Event_CategoryEntry.COLUMN_EVENT_ID +
                " INNER JOIN " + EventContract.LocationEntry.TABLE_NAME + " as LOC ON " +
                "E." + EventContract.EventEntry.COLUMN_LOCATION_ID + " = " +
                "LOC. " + EventContract.LocationEntry._ID +
                " WHERE " + selection + " ORDER BY " + sortOrder;

        Cursor retVal = mDbHelper.getReadableDatabase().rawQuery(rawQuery, selectionArgs);


        return retVal;
    }

}
