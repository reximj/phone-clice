package com.phoneclice.appcloner;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String appName;
    private String packageName;
    private Drawable appIcon;
    private boolean isCloned;
    private int cloneId;

    public AppInfo() {
    }

    public AppInfo(String appName, String packageName, Drawable appIcon) {
        this.appName = appName;
        this.packageName = packageName;
        this.appIcon = appIcon;
        this.isCloned = false;
        this.cloneId = -1;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public boolean isCloned() {
        return isCloned;
    }

    public void setCloned(boolean cloned) {
        isCloned = cloned;
    }

    public int getCloneId() {
        return cloneId;
    }

    public void setCloneId(int cloneId) {
        this.cloneId = cloneId;
    }

    public String getClonedPackageName() {
        return packageName + "_clone" + cloneId;
    }
}
