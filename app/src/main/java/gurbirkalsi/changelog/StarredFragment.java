package gurbirkalsi.changelog;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StarredFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

    public static SharedPreferences sharedPreferences;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    public static RecyclerView mRecyclerView;
    public static AppAdapter mAdapter;
    public static  RecyclerView.LayoutManager mLayoutManager;

    public static List<App> favoritesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view,container,false);
        v.setTag(TAG);
        sharedPreferences = getContext().getSharedPreferences("favoritedApps", 0);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);

        if (sharedPreferences.getAll().isEmpty()) {

            v = inflater.inflate(R.layout.starred_tab,container,false);

        } else {

            mLayoutManager = new LinearLayoutManager(getActivity());

            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }
            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

            mAdapter = new AppAdapter(favoritesList, R.layout.recent_tab, getActivity());

            Map<String,?> keys = sharedPreferences.getAll();

            for(Map.Entry<String,?> entry : keys.entrySet()){

                App appObject = new App();

                for (int i = 0; i < RecentFragment.appsList.size(); i++) {
                    if (RecentFragment.appsList.get(i).getPackageName().equals(entry.getValue())) {
                        appObject = RecentFragment.appsList.get(i);
                        appObject.setSelected(true);
                    }
                }

                favoritesList.add(appObject);

            }

            mRecyclerView.setAdapter(mAdapter);
            if (mAdapter.getItemCount() != 0) {
                mAdapter.notifyDataSetChanged();
            }
        }

        return v;

    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutManagerType Type of layout manager to switch to.
     */
    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    public void initializeViews() {

        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new AppAdapter(favoritesList, R.layout.recent_tab, getActivity());

        Map<String,?> keys = sharedPreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){

            App appObject = new App();

            for (int i = 0; i < RecentFragment.appsList.size(); i++) {
                if (RecentFragment.appsList.get(i).getPackageName().equals(entry.getValue())) {
                    appObject = RecentFragment.appsList.get(i);
                    appObject.setSelected(true);
                }
            }

            favoritesList.add(appObject);

        }

        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getItemCount() != 0) {
            mAdapter.notifyDataSetChanged();
        }

    }

}