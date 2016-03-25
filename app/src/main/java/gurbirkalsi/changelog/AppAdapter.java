package gurbirkalsi.changelog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.like.IconType;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder>{

    PackageManager packageManager;
    SharedPreferences sharedPreferences;
    private List<App> apps;
    private int rowLayout;
    private Context mContext;

    public AppAdapter(List<App> apps, int rowLayout, Context context) {
        this.apps = apps;
        this.rowLayout = rowLayout;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        packageManager = this.mContext.getPackageManager();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {

        final App appObject = apps.get(i);
        viewHolder.appName.setText(appObject.getApplicationName());
        viewHolder.versionNumber.setText(String.valueOf(appObject.getVersionNumber()));
        viewHolder.updateDate.setText(String.valueOf(appObject.getLastUdpateTime()));
        viewHolder.appIcon.setImageDrawable(appObject.getAppIcon());
        viewHolder.appChangelog.setText(appObject.getChangelogText());
        viewHolder.favoriteButton.setIcon(IconType.Star);
        viewHolder.favoriteButton.setIconSizeDp(20);
        viewHolder.favoriteButton.setLiked(apps.get(i).isSelected());
        viewHolder.favoriteButton.setTag(apps.get(i));
        viewHolder.favoriteButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                App app = (App) likeButton.getTag();
                app.setSelected(true);

                sharedPreferences = mContext.getSharedPreferences("favoritedApps", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(appObject.getApplicationName(), appObject.getPackageName());
                editor.apply();

                Map<String,?> keys = sharedPreferences.getAll();

                for(Map.Entry<String,?> entry : keys.entrySet()){

                    App appObject = new App();

                    for (int i = 0; i < RecentFragment.appsList.size(); i++) {
                        if (RecentFragment.appsList.get(i).getPackageName().equals(entry.getValue()) && StarredFragment.favoritesList.contains(RecentFragment.appsList.get(i)) == false) {
                            appObject = RecentFragment.appsList.get(i);
                            appObject.setSelected(true);
                            StarredFragment.favoritesList.add(appObject);
                        }
                    }

                }

                if (StarredFragment.mAdapter == null) {

                    StarredFragment starredFragment = new StarredFragment();
                    starredFragment.initializeViews();

                }

                StarredFragment.mAdapter.notifyDataSetChanged();

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                App app = (App) likeButton.getTag();
                app.setSelected(false);

                sharedPreferences = mContext.getSharedPreferences("favoritedApps", 0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(appObject.getApplicationName());
                editor.apply();

                for (int i = 0; i < StarredFragment.favoritesList.size(); i++) {
                    if (StarredFragment.favoritesList.get(i).getPackageName().equals(appObject.getPackageName())) {
                        StarredFragment.favoritesList.remove(i);
                        StarredFragment.mAdapter.notifyDataSetChanged();
                        RecentFragment.mAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps == null ? 0 : apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView appName;
        private TextView versionNumber;
        private TextView updateDate;
        private ImageView appIcon;
        private TextView appChangelog;
        private LikeButton favoriteButton;

        public ViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            versionNumber = (TextView) itemView.findViewById(R.id.version_code);
            updateDate = (TextView) itemView.findViewById(R.id.update_date);
            appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            appChangelog = (TextView) itemView.findViewById(R.id.app_changelog);
            favoriteButton = (LikeButton) itemView.findViewById(R.id.favorite_button);
        }
    }
}
