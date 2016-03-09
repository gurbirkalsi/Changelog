package gurbirkalsi.changelog;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    PackageManager packageManager;
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
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        App appObject = apps.get(i);
        viewHolder.appName.setText(appObject.getApplicationName());
        viewHolder.versionNumber.setText(String.valueOf(appObject.getVersionNumber()));
        viewHolder.updateDate.setText(String.valueOf(appObject.getLastUdpateTime()));
        viewHolder.appIcon.setImageDrawable(appObject.getAppIcon());
        viewHolder.appChangelog.setText(appObject.getChangelogText());
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

        public ViewHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.app_name);
            versionNumber = (TextView) itemView.findViewById(R.id.version_code);
            updateDate = (TextView) itemView.findViewById(R.id.update_date);
            appIcon = (ImageView) itemView.findViewById(R.id.app_icon);
            appChangelog = (TextView) itemView.findViewById(R.id.app_changelog);
        }
    }
}
