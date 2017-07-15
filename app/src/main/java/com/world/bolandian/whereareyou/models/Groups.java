package com.world.bolandian.whereareyou.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bolandian on 01/07/2017.
 */

public class Groups implements Parcelable {
    private String ownerGroupUID;
    private String groupUID;
    private String groupName;

    public Groups(){

    }

    public Groups(String ownerGroupUID, String groupUID, String groupName) {
        this.ownerGroupUID = ownerGroupUID;
        this.groupUID = groupUID;
        this.groupName = groupName;
    }

    protected Groups(Parcel in) {
        ownerGroupUID = in.readString();
        groupUID = in.readString();
        groupName = in.readString();
    }

    public static final Creator<Groups> CREATOR = new Creator<Groups>() {
        @Override
        public Groups createFromParcel(Parcel in) {
            return new Groups(in);
        }

        @Override
        public Groups[] newArray(int size) {
            return new Groups[size];
        }
    };

    public String getOwnerGroupUID() {
        return ownerGroupUID;
    }

    public void setOwnerGroupUID(String ownerGroupUID) {
        this.ownerGroupUID = ownerGroupUID;
    }

    public String getGroupUID() {
        return groupUID;
    }

    public void setGroupUID(String groupUID) {
        this.groupUID = groupUID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public String toString() {
        return "Groups{" +
                "ownerGroupUID='" + ownerGroupUID + '\'' +
                ", groupUID='" + groupUID + '\'' +
                ", groupName='" + groupName + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(ownerGroupUID);
        parcel.writeString(groupUID);
        parcel.writeString(groupName);
    }
}
