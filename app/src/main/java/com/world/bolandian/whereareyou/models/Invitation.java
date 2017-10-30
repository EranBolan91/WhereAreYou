package com.world.bolandian.whereareyou.models;

/**
 * Created by Bolandian on 18/10/2017.
 */

public class Invitation {
    private String approved;
    private String senderName;
    private String senderUid;
    private String senderImg;
    private String senderToken;

    public Invitation(String approved, String senderName, String senderUid, String senderImg, String senderToken) {
        this.approved = approved;
        this.senderName = senderName;
        this.senderUid = senderUid;
        this.senderImg = senderImg;
        this.senderToken = senderToken;
    }

    public String getApproved() {
        return approved;
    }

    public void setApproved(String approved) {
        this.approved = approved;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getSenderImg() {
        return senderImg;
    }

    public void setSenderImg(String senderImg) {
        this.senderImg = senderImg;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }
}
