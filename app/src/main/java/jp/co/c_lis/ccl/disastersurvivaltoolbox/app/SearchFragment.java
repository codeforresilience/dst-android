package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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

    private ListView mListView;
    private EditText mKeyword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        mListView = (ListView) rootView.findViewById(R.id.lv_main);
        mListView.setAdapter(mArticleAdapter);
        mListView.setOnItemClickListener(this);

        View headerView = inflater.inflate(R.layout.search_header, null);
        mKeyword = (EditText) headerView.findViewById(R.id.et_keyword);
        mKeyword.setOnEditorActionListener(this);

        mDisasterTypeAdapter = new DisasterTypeAdapter();
        GridView gridView = (GridView) headerView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(mDisasterTypeAdapter);
        mListView.addHeaderView(headerView);

        return rootView;
    }

    private DisasterTypeAdapter mDisasterTypeAdapter;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Article article = (Article) mArticleAdapter.getItem(position - mListView.getHeaderViewsCount());
        if (mListener != null) {
            article.findById(article.getId(), mDb);
            mListener.onArticleSelected(article);
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DisasterType type = (DisasterType) buttonView.getTag();
        if (isChecked) {
            mSelectedDisasterType.add(type);
        } else {
            mSelectedDisasterType.remove(type);
        }
        executeSearch();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            executeSearch();
            return true;
        }
        return false;
    }

    public void executeSearch() {
        mArticleList.clear();

        String keyword = mKeyword.getText().toString();

        new Article().find(mDb,
                mSelectedDisasterType,
                "".equals(keyword) ? null : keyword,
                mArticleList);
        mArticleAdapter.notifyDataSetChanged();

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mKeyword.getWindowToken(), 0);
    }

    private List<DisasterType> mDisasterTypeList = new ArrayList<DisasterType>();
    private List<DisasterType> mSelectedDisasterType = new ArrayList<DisasterType>();

    private class DisasterTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDisasterTypeList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDisasterTypeList.get(position);
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
            view.setChecked(true);
            view.setCompoundDrawablesWithIntrinsicBounds(0, type.getIcon(), 0, 0);
            view.setTextOn(type.getNameEn());
            view.setTextOff(type.getNameEn());
            view.setText(type.getNameEn());
            view.setPadding(5, 5, 5, 5);
            view.setOnCheckedChangeListener(SearchFragment.this);

            view.setTag(type);

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
            title.setText(article.getTitle());

            TextView description = (TextView) convertView.findViewById(R.id.tv_description);
            description.setText(article.getAbstaction());

            ImageView thumbnail = (ImageView) convertView.findViewById(R.id.iv_image);
            if (article.getImage() != null) {
                thumbnail.setVisibility(View.VISIBLE);
                try {
                    thumbnail.setImageBitmap(BitmapFactory.decodeStream(
                            getActivity().getAssets().open(article.getImage())));
                } catch (IOException e) {
                }
            } else {
                thumbnail.setVisibility(View.GONE);
            }

            TextView likeCount = (TextView) convertView.findViewById(R.id.tv_like_count);
            likeCount.setText(String.valueOf(article.getLikeCount()));

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

    private Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();

                new DisasterType().findAll(mDb, mDisasterTypeList);
                mSelectedDisasterType.addAll(mDisasterTypeList);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDisasterTypeAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        th.start();
    }
}
