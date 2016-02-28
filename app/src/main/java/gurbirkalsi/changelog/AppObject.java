package gurbirkalsi.changelog;

import android.graphics.drawable.Drawable;

/**
 * Created by Guri on 1/23/16.
 */
public class AppObject {
    private String appName;
    private String versionCode;
    private String updateDate;
    private Drawable appIcon;
    private String appChangelog;

    AppObject (String name, String version, String date, Drawable icon, String changelog) {
        appName = name;
        versionCode = version;
        updateDate = date;
        appIcon = icon;
        appChangelog = changelog;
    }

}
