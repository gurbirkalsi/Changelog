package gurbirkalsi.changelog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Handler;

import static android.content.Context.*;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Guri on 5/3/15.
 */
public class RecentFragment extends Fragment {

    TextView recentChangeText;

    String scrappedAppName = new String();
    String scrappedRecentChange = new String();

    ArrayList<String> scrappedAppData = new ArrayList<String>();

    ArrayList<PInfo> apps;

    public class SearchResult extends AsyncTask<Void, ArrayList<String>, Void> {

        String resultTextFormat;

        String result;
        String url;

        ProgressDialog mProgressDialog;


        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < apps.size(); i++) {

                try {

                    Log.v("App", apps.get(i).appname);
                    url = "https://play.google.com/store/apps/details?id=" + apps.get(i).pname;

                    Document document = Jsoup.connect(url).get();
                    Elements recentChangesDiv = document.select("div[class=recent-change]");
                    Log.v("Recent Change", recentChangesDiv.toString());
                    resultTextFormat = recentChangesDiv.toString();
                    resultTextFormat = resultTextFormat.replaceAll("<div class=\"recent-change\">", "").trim();
                    resultTextFormat = resultTextFormat.replaceAll("</div>", "").trim();

                    scrappedRecentChange = (resultTextFormat);

                    int appVersionCode = apps.get(i).versionCode;
                    String appVersionCodeString = Integer.toString(appVersionCode);
                    long appLastUpdateTime = apps.get(i).lastUpdateTime;
                    String appLastUpdateTimeString = Long.toString(appLastUpdateTime);
                    scrappedAppData.add(0, appVersionCodeString);
                    scrappedAppData.add(1, apps.get(i).versionName);
                    scrappedAppData.add(2, appLastUpdateTimeString);
                    scrappedAppData.add(3, apps.get(i).pname);
                    scrappedAppData.add(4, apps.get(i).appname);
                    scrappedAppData.add(5, scrappedRecentChange);

                    publishProgress(scrappedAppData);

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {


        }

        @Override
        protected void onProgressUpdate(ArrayList<String>... values) {
            super.onProgressUpdate(values);
                LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.card_linear_layout);

                TextView recentChangeTextView = new TextView(getActivity());
                TextView appNameTextView = new TextView(getActivity());
                CardView cardView = new CardView(getActivity());

                cardView.setPadding(0, 50, 0, 50);
                cardView.setPaddingRelative(10, 10, 10, 10);
                CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.FILL_PARENT);
                layoutParams.setMargins(50, 50, 0, 50);
                cardView.setUseCompatPadding(true);

                recentChangeTextView.setText((CharSequence) values[5]);
                recentChangeTextView.setPadding(0, 50, 0, 0);
                appNameTextView.setText((CharSequence) values[4]);

                cardView.addView(appNameTextView);
                cardView.addView(recentChangeTextView);
                linearLayout.addView(cardView, layoutParams);

                String dataString = values[0] + values[1];

                writeToFile(values, values[4]);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.recent_tab,container,false);

        apps = getPackages();

        //Sorts the apps alphabetically
        Collections.sort(apps, new RecentFragment.CustomComparator());

        File dir = getActivity().getFilesDir();
        File[] subFiles = dir.listFiles();
        ArrayList<String> appFiles = new ArrayList<String>();

        for (File file : subFiles)
        {
            appFiles.add(file.getName());
        }

        for (int i = 0; i < apps.size(); i++) {

            if (subFiles != null)
            {
                for (File file : subFiles)
                {
                    ArrayList<String> storedFileData = readFromFile(file.getAbsolutePath());
                    String storedVersionCode = storedFileData.get(0);
                    int appVersionCode = apps.get(i).versionCode;
                    String appVersionCodeString = Integer.toString(appVersionCode);

                    if (appFiles.contains(file.getName()) && appVersionCodeString == storedVersionCode)
                    {
                        apps.remove(apps.get(i));
                        ArrayList<String> fileData = readFromFile(file.getName());

                        LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.card_linear_layout);

                        TextView recentChangeTextView = new TextView(getActivity());
                        TextView appNameTextView = new TextView(getActivity());
                        CardView cardView = new CardView(getActivity());

                        cardView.setPadding(0, 50, 0, 50);
                        cardView.setPaddingRelative(10, 10, 10, 10);
                        CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.FILL_PARENT);
                        layoutParams.setMargins(50, 50, 0, 50);
                        cardView.setUseCompatPadding(true);

                        recentChangeTextView.setText(fileData.get(0).indexOf(5));
                        recentChangeTextView.setPadding(0, 50, 0, 0);
                        appNameTextView.setText(fileData.get(0).indexOf(4));

                        cardView.addView(appNameTextView);
                        cardView.addView(recentChangeTextView);
                        linearLayout.addView(cardView, layoutParams);
                    }
                }
            }

        }

        new SearchResult().execute();

        return v;

    }

    //PACKAGE HANDLER METHODS
    class PInfo {
        private String appname = "";
        private String pname = "";
        private String versionName = "";
        private int versionCode = 0;
        private long lastUpdateTime = 0;
        private Drawable icon;
        private void prettyPrint() {
            final String TAG = "";
            Log.v(TAG, appname + "\t" + pname + "\t" + versionName + "\t" + versionCode + "\t" + lastUpdateTime);
        }
    }

    private ArrayList<PInfo> getPackages() {
        ArrayList<PInfo> apps = getInstalledApps(false); /* false = no system packages */
        final int max = apps.size();
        for (int i=0; i<max; i++) {
            apps.get(i).prettyPrint();
        }
        return apps;
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = getActivity().getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PInfo newInfo = new PInfo();
            newInfo.appname = p.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.lastUpdateTime = p.lastUpdateTime;
            newInfo.icon = p.applicationInfo.loadIcon(getActivity().getPackageManager());
            res.add(newInfo);
        }
        return res;
    }

    public class CustomComparator implements Comparator<PInfo> {

        @Override
        public int compare(PInfo lhs, PInfo rhs) {
            return lhs.appname.compareTo(rhs.appname);
        }
    }

    //FILE HANDLER METHODS
    private void writeToFile(String data, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput(fileName, MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private ArrayList<String> readFromFile(String fileName) {

        ArrayList<String> fileData = null;

        try {
            FileInputStream fileInputStream = getActivity().openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;

            receiveString = bufferedReader.readLine();
            fileData = new ArrayList<String>();
            Collections.addAll(fileData, receiveString.split(" "));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }

}
