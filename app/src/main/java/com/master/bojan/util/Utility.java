package com.master.bojan.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.format.Time;

import com.master.bojan.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by bojan on 15.4.18..
 */

public class Utility {

    public static String CATEGORY_EXTRA;
    public static Location LOCATION_EXTRA;


    public static AlertDialog dialog(final Context context, String message,
                                     final String type) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle(R.string.oops);

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (type != null && type.equals(Constant.DIALOG_LOCATION)) {
                            context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                        } else if (type != null && type.equals(Constant.DIALOG_WIFI)) {
                            turnOnWifi(context);
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }


    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));
    }

    public static int getPreferredSyncInterval(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sync = prefs.getString(context.getString(R.string.pref_sync_interval_key),
                "1");
        return Integer.valueOf(sync);
    }

    public static int getSelectedRadius(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sync = prefs.getString(context.getString(R.string.pref_radius_key),
                "50");
        return Integer.valueOf(sync);
    }

    public static boolean isAutoSyncAllowed(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sync = prefs.getString(context.getString(R.string.pref_sync_switch_key),
                "false");
        if (sync.equals("true"))
            return true;
        else
            return false;
    }

    public static boolean isNearbySearchSelected(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean(context.getString(R.string.pref_nearby_switch_key), false);
    }

    public static boolean hasInternetAccess(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void turnOnWifi(Context context) {

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

    public static boolean isLocationProviderEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public static void enableLocation(Context context) {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        context.sendBroadcast(intent);
        //   context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }


    public static long getFirstDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal.getTimeInMillis();

    }

    public static long getLastDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        return cal.getTimeInMillis();
    }

    public static long getFirstDayOfMonth() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getTimeInMillis();
    }

    public static long getLastDayOMonth() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));

        return cal.getTimeInMillis();
    }

    public static long getLastDayOfYear() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MONTH, 9);
        //  cal.set(Calendar.DAY_OF_MONTH, 31);
        Date d = cal.getTime();
        return cal.getTimeInMillis();
    }


    public static long stringToDateTime(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // sdf.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {

            return 0;
        }

    }

    public static long stringToDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(Constant.MY_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {

            return 0;
        }

    }


    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day

        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String prepareDateForListView(long dateL) {
        if (dateL == (new Date(0).getTime())) return "";

        Date date = new Date(dateL);
        SimpleDateFormat sdf;

        if (date.getMinutes() == 0 && date.getHours() == 0 && date.getSeconds() == 0)
            sdf = new SimpleDateFormat(Constant.MY_DATE_FORMAT);
        else
            sdf = new SimpleDateFormat(Constant.MY_DATE_TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getDefault());

        return sdf.format(date);

    }


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return Constant.TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return Constant.TYPE_MOBILE;
        }
        return Constant.TYPE_NOT_CONNECTED;
    }
}
