package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getTitle());
        return rootView;
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
