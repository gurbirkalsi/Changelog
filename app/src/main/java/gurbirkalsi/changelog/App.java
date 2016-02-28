package gurbirkalsi.changelog;

import android.graphics.drawable.Drawable;

public class App {

    private int versionCode;
    private String versionName;
    private long lastUdpateTime;
    private String packageName;
    private String applicationName;
    private String changelogText;
    private Drawable appIcon;

    public App(){}

    public App(int versionCode, String versionName, long lastUpdateTime, String packageName, String applicationName, String changelogText, Drawable appIcon) {
        super();
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.lastUdpateTime = lastUpdateTime;
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.changelogText = changelogText;
        this.appIcon = appIcon;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public long getLastUdpateTime() {
        return lastUdpateTime;
    }

    public void setLastUdpateTime(long lastUdpateTime) {
        this.lastUdpateTime = lastUdpateTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getChangelogText() {
        return changelogText;
    }

    public void setChangelogText(String changelogText) {
        this.changelogText = changelogText;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "App [versionCode=" + versionCode + ", versionName=" + versionName + ", lastUpdateTime=" + lastUdpateTime + ", packageName=" + packageName + ", applicationName" + applicationName + ", changelogText" + changelogText + "]";
    }

}
