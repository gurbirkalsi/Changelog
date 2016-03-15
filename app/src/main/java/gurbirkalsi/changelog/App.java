package gurbirkalsi.changelog;

import android.graphics.drawable.Drawable;

public class App {

    private int versionNumber;
    private String versionName;
    private String lastUdpateTime;
    private String packageName;
    private String applicationName;
    private String changelogText;
    private Drawable appIcon;
    private boolean selected;

    public App(){}

    public App(int versionNumber, String versionName, String lastUpdateTime, String packageName, String applicationName, String changelogText, Drawable appIcon) {
        super();
        this.versionNumber = versionNumber;
        this.versionName = versionName;
        this.lastUdpateTime = lastUpdateTime;
        this.packageName = packageName;
        this.applicationName = applicationName;
        this.changelogText = changelogText;
        this.appIcon = appIcon;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getLastUdpateTime() {
        return lastUdpateTime;
    }

    public void setLastUdpateTime(String lastUdpateTime) {
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
        return "App [versionNumber=" + versionNumber + ", versionName=" + versionName + ", lastUpdateTime=" + lastUdpateTime + ", packageName=" + packageName + ", applicationName" + applicationName + ", changelogText" + changelogText + "]";
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
