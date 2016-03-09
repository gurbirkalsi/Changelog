/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package gurbirkalsi.changelog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class RecentFragment extends Fragment {

    private static final String TAG = "RecyclerViewFragment";
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    SharedPreferences sharedPreferences;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected LayoutManagerType mCurrentLayoutManagerType;

    protected RecyclerView mRecyclerView;
    protected AppAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataset;

    List<App> appsList = new ArrayList<>();

    List<PackageInfo> appPackages;
    PackageManager packageManager;

    public class SearchResult extends AsyncTask<String, ArrayList<String>, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<String> doInBackground(String... appUrl) {

            ArrayList<String> appInformation = new ArrayList<>();

            JSONObject object = null;
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(appUrl[0]);              //Using client ID and secret to bypass rate limit of 60 requests per hour (for testing purposes)
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-Mashape-Key","K2uQE4H4PWmsh2I9vyo9I39a8WADp1BhYSyjsnjBuFCchECwys");
                urlConnection.setRequestProperty("Accept","application/json");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                object = new JSONObject(stringBuilder.toString());
                appsList.add(new App(0, object.get("versionName").toString(), object.get("datePublished").toString(), appUrl[1], (String) object.get("name"), (String) object.get("changelog"), packageManager.getApplicationIcon(appUrl[1])));
                appInformation.add(appUrl[1]);
                appInformation.add(object.get("changelog").toString());
                appInformation.add(appUrl[2]);
            } catch (JSONException | IOException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return appInformation;
        }

        @Override
        protected void onPostExecute(ArrayList<String> appInformation) {

            if (appInformation.size() != 0) {

                sharedPreferences = getActivity().getSharedPreferences(appInformation.get(0), 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(appInformation.get(2), appInformation.get(1));
                editor.apply();
                mRecyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context mContext = this.getActivity().getApplicationContext();
        //0 = mode private. only this app can read these preferences
        sharedPreferences = mContext.getSharedPreferences("myAppPrefs", 0);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recycler_view, container, false);
        rootView.setTag(TAG);

        appPackages = getPackages();

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mLayoutManager = new LinearLayoutManager(getActivity());

        mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                    .getSerializable(KEY_LAYOUT_MANAGER);
        }
        setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

        mAdapter = new AppAdapter(appsList, R.layout.recent_tab, getActivity());

        if (getFirstRun()) {

            Toast toast = Toast.makeText(getActivity(), "This is the first run", Toast.LENGTH_SHORT);
            toast.show();
            setRunned();

            for (int i = 0; i < appPackages.size(); i++) {

                String appUrlBuilder = "https://gplaystore.p.mashape.com/applicationDetails?id=" + appPackages.get(i).packageName + "&lang=en";

                new SearchResult().execute(appUrlBuilder, appPackages.get(i).packageName, String.valueOf(appPackages.get(i).versionCode));

                if (mAdapter.getItemCount() != 0) {
                    mAdapter.notifyDataSetChanged();
                }

            }

        } else {

            Toast toast = Toast.makeText(getActivity(), "This is not first run", Toast.LENGTH_SHORT);
            toast.show();

            for (int i = 0; i < appPackages.size(); i++) {

                sharedPreferences = getActivity().getSharedPreferences(appPackages.get(i).packageName, Context.MODE_PRIVATE);

                if (!sharedPreferences.contains(String.valueOf(appPackages.get(i).versionCode))) {

                    String appUrlBuilder = "https://gplaystore.p.mashape.com/applicationDetails?id=" + appPackages.get(i).packageName + "&lang=en";

                    new SearchResult().execute(appUrlBuilder, appPackages.get(i).packageName, String.valueOf(appPackages.get(i).versionCode));

                    if (mAdapter.getItemCount() != 0) {
                        mAdapter.notifyDataSetChanged();
                    }

                } else {

                    ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = packageManager.getApplicationInfo(appPackages.get(i).packageName, 0);
                    } catch (final PackageManager.NameNotFoundException e) {}
                    final String title = (String)((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");

                    try {
                        appsList.add(new App(0, appPackages.get(i).versionName, String.valueOf(appPackages.get(i).lastUpdateTime), appPackages.get(i).packageName, title, sharedPreferences.getString(String.valueOf(appPackages.get(i).versionCode), null), packageManager.getApplicationIcon(appPackages.get(i).packageName)));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (mAdapter.getItemCount() != 0) {
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.notifyDataSetChanged();
                    }

                }

            }

        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
        super.onSaveInstanceState(savedInstanceState);
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

    //PACKAGE HANDLER METHODS
    private List<PackageInfo> getPackages() {
        packageManager = getActivity().getPackageManager();
        appPackages = packageManager.getInstalledPackages(1); /* 0 = no system packages */
        return appPackages;
    }

    public boolean getFirstRun() {
        return sharedPreferences.getBoolean("firstRun", true);
    }

    public void setRunned() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("firstRun", false);
        edit.commit();
    }


}