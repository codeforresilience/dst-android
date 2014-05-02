package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.DisasterType;

public class SearchFragment extends AbsFragment<SearchFragmentListener>
        implements AdapterView.OnItemClickListener,
        TextView.OnEditorActionListener,
        CompoundButton.OnCheckedChangeListener {

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragment() {
        mArticleAdapter = new ArticleAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.lv_main);
        listView.setAdapter(mArticleAdapter);
        listView.setOnItemClickListener(this);

        View headerView = inflater.inflate(R.layout.search_header, null);
        EditText keyword = (EditText) headerView.findViewById(R.id.et_keyword);
        keyword.setOnEditorActionListener(this);

        mDisasterTypeAdapter = new DisasterTypeAdapter();
        GridView gridView = (GridView) headerView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(mDisasterTypeAdapter);
        listView.addHeaderView(headerView);

        return rootView;
    }

    private DisasterTypeAdapter mDisasterTypeAdapter;

    private static final DisasterType[] DISASTER_TYPES = new DisasterType[]{
            new DisasterType(R.drawable.disaster_type_earthquake, "地震", "Earthquake"),
            new DisasterType(R.drawable.disaster_type_typhoon, "台風", "Typhoon"),
            new DisasterType(R.drawable.disaster_type_snow, "大雪", "Snow"),
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article article = (Article) mArticleAdapter.getItem(position);
        if (mListener != null) {
            mListener.onArticleSelected(article);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        search();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search();
            return true;
        }
        return false;
    }

    private void search() {
        mArticleList.clear();
        Article.loadDummy(mArticleList);
        mArticleAdapter.notifyDataSetChanged();
    }

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
            DisasterType type = (DisasterType) getItem(position);
            ToggleButton view = (ToggleButton) View.inflate(getActivity(), R.layout.disaster_type, null);

            // 上に画像を表示
            view.setCompoundDrawablesWithIntrinsicBounds(0, type.getIcon(), 0, 0);
            view.setTextOn(type.getNameEn());
            view.setTextOff(type.getNameEn());
            view.setText(type.getNameEn());
            view.setPadding(5, 5, 5, 5);
            view.setOnCheckedChangeListener(SearchFragment.this);
            return view;
        }
    }

    private final List<Article> mArticleList = new ArrayList<Article>();

    private class ArticleAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mArticleList.size();
        }

        @Override
        public Object getItem(int position) {
            return mArticleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mArticleList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.article_row, null);
            }

            Article article = (Article) getItem(position);
            TextView title = (TextView) convertView.findViewById(R.id.tv_title);
            title.setText(article.getAbstaction());

            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.iv_image);
            try {
                thumbnail.setImageBitmap(BitmapFactory.decodeStream(
                        getActivity().getAssets().open(article.getImage())));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    private ArticleAdapter mArticleAdapter;

    @Override
    public CharSequence getTitle() {
        return getText(R.string.title_search);
    }

    @Override
    public int getMenuId() {
        return R.menu.search;
    }

}
