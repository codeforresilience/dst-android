package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.History;

public class HomeFragment extends AbsFragment<HomeFragmentListener> implements AdapterView.OnItemClickListener {
    private static final String TAG = "HomeFragment";

    private ListView mHistoryView;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        mHistoryAdapter = new HistoryAdapter();
    }

    private final List<History> mHistoryList = new ArrayList<History>();


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        History history = (History) mHistoryAdapter.getItem(position);
        if (mListener != null) {
            mListener.onHistorySelected(history);
        }
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHistoryList.size();
        }

        @Override
        public Object getItem(int position) {
            return mHistoryList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mHistoryList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.article_row, null);
            }

            History history = (History) getItem(position);
            TextView title = (TextView) convertView.findViewById(R.id.tv_title);
            title.setText(history.getAbstraction());

            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.iv_image);
            String imagePath = history.getArticle().getImage();
            if (imagePath != null) {
                thumbnail.setVisibility(View.VISIBLE);
                File imageFile = new File(getActivity().getCacheDir(), history.getArticle().getImage());
                thumbnail.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));

            } else {
                thumbnail.setVisibility(View.GONE);
            }
            TextView likeCount = (TextView) convertView.findViewById(R.id.tv_like_count);
            likeCount.setText(String.valueOf(history.getArticle().getLikeCount()));

            return convertView;
        }
    }

    private HistoryAdapter mHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mHistoryView = (ListView) rootView.findViewById(R.id.lv_main);
        mHistoryView.setAdapter(mHistoryAdapter);
        mHistoryView.setOnItemClickListener(this);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "onResume");

        mHistoryList.clear();
        new History().findAll(mDb, mHistoryList);
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_home);
    }

    @Override
    public int getMenuId() {
        return R.menu.home;
    }

}
