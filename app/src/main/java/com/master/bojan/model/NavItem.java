package com.master.bojan.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bojan on 2.4.18..
 */

public class NavItem implements Parcelable {

    private String mTitle;
    private String mDescription;
    private int mIcon;

    public static final Creator<NavItem> CREATOR = new Creator<NavItem>() {
        @Override
        public NavItem createFromParcel(Parcel parcel) {
            return new NavItem(parcel);
        }

        @Override
        public NavItem[] newArray(int size) {
            return new NavItem[size];
        }
    };

    public NavItem(String mTitle, String mDescription, int mIcon) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mIcon = mIcon;
    }

    public NavItem(Parcel in){
        mTitle = in.readString();
        mDescription = in.readString();
        mIcon = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mDescription);
        parcel.writeInt(mIcon);
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }
}
