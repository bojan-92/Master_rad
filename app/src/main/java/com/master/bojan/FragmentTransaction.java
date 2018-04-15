package com.master.bojan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by bojan on 15.4.18..
 */

public class FragmentTransaction {

    public static void to(int id, Fragment newFragment, FragmentActivity activity)
    {
        to(id, newFragment, activity, null, false);
    }

    public static void to(int id, Fragment newFragment, FragmentActivity activity, String backStack, boolean toBackStack)
    {
        android.support.v4.app.FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_NONE)
                .replace(id, newFragment);
        if(toBackStack) transaction.addToBackStack(backStack);
        transaction.commit();
    }



    public static void add(int id, Fragment newFragment, FragmentActivity activity, String backStack)
    {
        activity.getSupportFragmentManager().beginTransaction().setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_NONE)
                .add(id, newFragment)
                .commit();
    }
}
