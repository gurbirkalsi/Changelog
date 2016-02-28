package gurbirkalsi.changelog;

import java.util.List;

/**
 * Created by Guri on 1/25/16.
 */
public class AppManager {

    private static AppManager mInstance;
    private List<App> apps;

    public static AppManager getInstance() {
        if (mInstance == null) {
            mInstance = new AppManager();
        }

        return mInstance;
    }
}
