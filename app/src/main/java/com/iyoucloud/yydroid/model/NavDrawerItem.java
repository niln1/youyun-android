package com.iyoucloud.yydroid.model;

public class NavDrawerItem {

    String itemTitle;
    int imgResID;
    boolean isUserProfile;

    public NavDrawerItem(boolean isSpinner) {
        this(null, 0);
        this.isUserProfile = isSpinner;
    }

    public NavDrawerItem(String itemName, int imgResID) {
        super();
        this.itemTitle = itemName;
        this.imgResID = imgResID;
    }

    public String getItemTitle() {
        return itemTitle;
    }
    public void setItemTitle(String itemTitle) {
        itemTitle = itemTitle;
    }
    public int getImgResID() {
        return imgResID;
    }
    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

    public boolean isUserProfile() {
        return isUserProfile;
    }
}