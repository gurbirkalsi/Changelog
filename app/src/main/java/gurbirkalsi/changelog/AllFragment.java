package gurbirkalsi.changelog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Guri on 5/3/15.
 */
public class AllFragment extends Fragment {

    private AppListAdapter appListAdapter;
    private List<App> appsList = RecentFragment.appsList;

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.app_list,container,false);

        ListView listView = (ListView) v.findViewById(R.id.app_list);
        appListAdapter = new AppListAdapter();

        // Sort list of airlines by airline name
        Collections.sort(appsList, new Comparator<App>() {
            @Override
            public int compare(App airlineOne, App airlineTwo) {
                return airlineOne.getApplicationName().compareTo(airlineTwo.getApplicationName());
            }
        });

        // Pass sorted airline information to list adapter
        List<String> letters = new ArrayList<>();
        for (App appInfo : appsList) {
            String airlineFirstLetter = appInfo.getApplicationName().substring(0, 1);

            if (!letters.contains(airlineFirstLetter)) {
                letters.add(airlineFirstLetter);
                appListAdapter.addSectionHeader(airlineFirstLetter);
            }

            appListAdapter.addItem(appInfo);
        }

        listView.setAdapter(appListAdapter);

        return v;

    }

}
