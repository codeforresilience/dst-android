package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HomeFragment<T extends HomeFragment.Listener> extends AbsFragment {

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(getTitle());
        return rootView;
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_home);
    }

    @Override
    public int getMenuId() {
        return R.menu.home;
    }

    public interface Listener extends IFragmentListener {
    }
}
