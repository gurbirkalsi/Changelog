package gurbirkalsi.changelog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

public class RecentFragment extends Fragment {

    TextView recentChangeText;

    String scrappedAppName = new String();
    String scrappedRecentChange = new String();

    ArrayList<String> scrappedAppData = new ArrayList<String>();

    List<PackageInfo> apps;

    PackageManager packageManager;

    public class SearchResult extends AsyncTask<Void, ArrayList<String>, Void> {

        String resultTextFormat;
        String url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 0; i < apps.size(); i++)

                try {

                    //Log.v("App", apps.get(i).applicationInfo.name);
                    url = "https://play.google.com/store/apps/details?id=" + apps.get(i).packageName;

                    Document document = Jsoup.connect(url).get();
                    Elements recentChangesDiv = document.select("div[class=recent-change]");
                    Log.v("Recent Change", recentChangesDiv.toString());
                    resultTextFormat = recentChangesDiv.toString();
                    resultTextFormat = resultTextFormat.replaceAll("<div class=\"recent-change\">", "").trim();
                    resultTextFormat = resultTextFormat.replaceAll("</div>", "").trim();
                    resultTextFormat = resultTextFormat.replaceAll("\n", "").trim();

                    if (resultTextFormat.equals("")) {

                        scrappedRecentChange = "No change reported by developer.";

                    } else {

                        scrappedRecentChange = (resultTextFormat);

                    }

                    int appVersionCode = apps.get(i).versionCode;
                    String appVersionCodeString = Integer.toString(appVersionCode);
                    long appLastUpdateTime = apps.get(i).lastUpdateTime;
                    String appLastUpdateTimeString = Long.toString(appLastUpdateTime);
                    scrappedAppData.add(0, appVersionCodeString);
                    scrappedAppData.add(1, apps.get(i).versionName);
                    scrappedAppData.add(2, appLastUpdateTimeString);
                    scrappedAppData.add(3, apps.get(i).packageName);
                    scrappedAppData.add(4, packageManager.getApplicationLabel(apps.get(i).applicationInfo).toString());
                    scrappedAppData.add(5, scrappedRecentChange);

                    publishProgress(scrappedAppData);
                    scrappedAppData = new ArrayList<String>();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            return null;

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

                recentChangeTextView.setText((CharSequence) values[0].get(5));
                recentChangeTextView.setPadding(0, 50, 0, 0);
                appNameTextView.setText((CharSequence) values[0].get(4));

                cardView.addView(appNameTextView);
                cardView.addView(recentChangeTextView);
                linearLayout.addView(cardView, layoutParams);

                String dataString = values[0].get(0) + ", " + values[0].get(1) + ", " + values[0].get(2) + ", " + values[0].get(3) + ", " + values[0].get(4) + ", " + values[0].get(5);

                writeToFile(dataString, values[0].get(4));
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.recent_tab, container, false);

        apps = getPackages();

        //Sorts the apps alphabetically
        //Collections.sort(apps, new RecentFragment.CustomComparator());

        File dir = getActivity().getFilesDir();
        File[] subFiles = dir.listFiles();
        ArrayList<String> appFiles = new ArrayList<String>();

        Feed
        SQLiteDatabase appInfoDatbase = mDb

        for (File file : subFiles)
        {
            appFiles.add(file.getName());
        }

        for (int i = 0; i < apps.size(); i++) {

                for (File file : subFiles)
                {

                    String currentAppDirectory = dir + "/" + packageManager.getApplicationLabel(apps.get(i).applicationInfo).toString();;
                    if (file.toString().equals(currentAppDirectory)) {

                        ArrayList<String> storedFileData = readFromFile(file.getAbsolutePath());
                        String storedVersionCode = storedFileData.get(0);
                        int appVersionCode = apps.get(i).versionCode;
                        String appVersionCodeString = Integer.toString(appVersionCode);

                        if (appFiles.contains(file.getName()) && appVersionCodeString.equals(storedVersionCode)) {

                            LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.card_linear_layout);

                            RelativeLayout relativeLayout = new RelativeLayout(getActivity());
                            RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);

                            TextView recentChangeTextView = new TextView(getActivity());
                            TextView appNameTextView = new TextView(getActivity());
                            ImageView appIcon = new ImageView(getActivity());
                            CardView cardView = new CardView(getActivity());

                            appIcon.setId(1);
                            appNameTextView.setId(2);
                            recentChangeTextView.setId(3);

                            relativeLayoutParams.addRule(RelativeLayout.ALIGN_TOP, 1);
                            relativeLayoutParams.addRule(RelativeLayout.ALIGN_BOTTOM);


                            cardView.setPadding(0, 50, 0, 50);
                            cardView.setPaddingRelative(16, 16, 16, 16);
                            CardView.LayoutParams layoutParams = new CardView.LayoutParams(CardView.LayoutParams.FILL_PARENT, CardView.LayoutParams.FILL_PARENT);
                            layoutParams.setMargins(50, 50, 0, 50);
                            cardView.setUseCompatPadding(true);
                            cardView.setRadius(4);

                            Drawable drawable = packageManager.getApplicationIcon(apps.get(i).applicationInfo);
                            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                            Drawable appIconDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 81, 81, true));

                            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(81, 81);
                            appIcon.setLayoutParams(imageLayoutParams);

                            appIcon.setImageDrawable(appIconDrawable);
                            recentChangeTextView.setText(storedFileData.get(5));
                            recentChangeTextView.setPadding(0, 50, 0, 0);
                            appNameTextView.setText(storedFileData.get(4));

                            cardView.addView(appIcon);
                            cardView.addView(appNameTextView);
                            cardView.addView(recentChangeTextView);
                            relativeLayout.addView(cardView, layoutParams);
                            linearLayout.addView(relativeLayout);

                            apps.remove(apps.get(i));

                        }
                    }
                }
        }

        new SearchResult().execute();

        return v;

    }

    //PACKAGE HANDLER METHODS
    private List<PackageInfo> getPackages() {
        packageManager = getActivity().getPackageManager();
        apps = packageManager.getInstalledPackages(0); /* false = no system packages */
        return apps;
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
            FileInputStream fileInputStream = new FileInputStream(new File(fileName));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String receiveString;

            receiveString = bufferedReader.readLine();
            fileData = new ArrayList<String>();
            Collections.addAll(fileData, receiveString.split(", "));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileData;
    }

}
