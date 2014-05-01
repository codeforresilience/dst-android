package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class SearchFragment extends AbsFragment<SearchFragmentListener> {

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        mDisasterTypeAdapter = new DisasterTypeAdapter();

        GridView gridView = (GridView) rootView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(mDisasterTypeAdapter);
        return rootView;
    }

    private DisasterTypeAdapter mDisasterTypeAdapter;

    private static final int[] DISASTER_TYPES = new int[]{
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
            R.drawable.ic_launcher,
    };

    private class DisasterTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DISASTER_TYPES.length;
        }

        @Override
        public Object getItem(int position) {
            return DISASTER_TYPES[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(((Integer) getItem(position)));
            return imageView;
        }
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_search);
    }

    @Override
    public int getMenuId() {
        return R.menu.search;
    }

}
