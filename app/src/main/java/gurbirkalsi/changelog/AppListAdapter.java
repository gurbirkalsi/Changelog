package gurbirkalsi.changelog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Adapter that produces views to render list of airline information.
 */
public class AppListAdapter extends BaseAdapter {

    private static final int ITEM_VIEW_TYPE = 0;
    private static final int SEPARATOR_VIEW_TYPE = 1;

    // list of airline items served by this adapter
    private ArrayList<Object> mAppList = new ArrayList<>();

    // alphabetical headers served by this adapter
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    public void addItem(final App app) {
        mAppList.add(app);
        notifyDataSetChanged();
    }

    public void addSectionHeader(final String letter) {
        mAppList.add(letter);
        sectionHeader.add(mAppList.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? SEPARATOR_VIEW_TYPE : ITEM_VIEW_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mAppList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final int type = getItemViewType(position);

        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case ITEM_VIEW_TYPE:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.airlineName);
                    holder.imageView = (ImageView) convertView.findViewById(R.id.airlineLogo);
                    break;
                case SEPARATOR_VIEW_TYPE:
                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_separator, parent, false);
                    holder.textView = (TextView) convertView.findViewById(R.id.textSeparator);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // check whether the view is an item or separator
        if (type == ITEM_VIEW_TYPE) {
            App app = (App) getItem(position);
            holder.textView.setText(app.getApplicationName());
            holder.imageView.setImageDrawable(app.getAppIcon());
        } else {
            holder.textView.setText((String) getItem(position));
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public ImageView imageView;
    }
}
