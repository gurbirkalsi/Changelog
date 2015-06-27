package gurbirkalsi.changelog;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Guri on 5/3/15.
 */
public class AllFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.all_tab,container,false);
        return v;
    }

}
