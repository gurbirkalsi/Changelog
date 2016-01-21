package gurbirkalsi.changelog;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecentFragment extends Fragment {

    List<PackageInfo> appPackages;
    PackageManager packageManager;

    MySQLiteHelper appDatabase;




    public class SearchResult extends AsyncTask<String, ArrayList<String>, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... appUrl) {

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
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return object;
        }

        @Override
        protected void onPostExecute(JSONObject response) {

            if (response == null) {

                Log.v("TAG", "This is a system process, not represented in UI");

            } else {

                LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.card_linear_layout);

                TextView recentChangeTextView = new TextView(getActivity());
                TextView appNameTextView = new TextView(getActivity());
                CardView cardView = new CardView(getActivity());

                cardView.setPadding(0, 50, 0, 50);
                cardView.setPaddingRelative(10, 10, 10, 10);
                CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.FILL_PARENT);
                layoutParams.setMargins(50, 50, 0, 50);
                cardView.setUseCompatPadding(true);

                try {
                    recentChangeTextView.setText((CharSequence) response.get("changelog"));
                    recentChangeTextView.setPadding(0, 50, 0, 0);
                    appNameTextView.setText((CharSequence) response.get("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                cardView.addView(appNameTextView);
                cardView.addView(recentChangeTextView);
                linearLayout.addView(cardView, layoutParams);

            }



        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recycler_view, container, false);

        super.onCreate(savedInstanceState);

        RecyclerView mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());



        appDatabase = new MySQLiteHelper(getActivity());

        //Retrieve all app packages from PackageManager
        appPackages = getPackages();

        //if (appDatabase.getAllApps().isEmpty()) {

            for (int i = 0; i < appPackages.size(); i++) {

                final String applicationName = (String)((appPackages.get(i).applicationInfo != null) ? packageManager.getApplicationLabel(appPackages.get(i).applicationInfo) : "???");

                appDatabase.addApp(new App(
                        appPackages.get(i).versionCode,
                        appPackages.get(i).versionName,
                        appPackages.get(i).lastUpdateTime,
                        appPackages.get(i).packageName,
                        applicationName,
                        "No changelog text"
                        ));

                String appUrlBuilder = "https://gplaystore.p.mashape.com/applicationDetails?id="+appPackages.get(i).packageName+"&lang=en";

                new SearchResult().execute(appUrlBuilder);

            }

        return v;
    }


    //PACKAGE HANDLER METHODS
    private List<PackageInfo> getPackages() {
        packageManager = getActivity().getPackageManager();
        appPackages = packageManager.getInstalledPackages(0); /* 0 = no system packages */
        return appPackages;
    }


}
