/*
 * Copyright (C) 2014 Disaster Survival Toolbox Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.survivalpad.android;

import android.app.Activity;
import android.content.Context;
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

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.DisasterType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends AbsNavDrawerFragment<SearchFragment.SearchFragmentListener>
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
        mListView.setOnItemClickListener(this);

        View headerView = inflater.inflate(R.layout.search_header, null);
        mKeyword = (EditText) headerView.findViewById(R.id.et_keyword);
        mKeyword.setOnEditorActionListener(this);

        mDisasterTypeAdapter = new DisasterTypeAdapter(getActivity(), this, mDisasterTypeList,
                mSelectedDisasterType);
        GridView gridView = (GridView) headerView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(mDisasterTypeAdapter);

        // HeaderViewの設定はsetAdapterの前にする必要がある
        mListView.addHeaderView(headerView);
        mListView.setAdapter(mArticleAdapter);

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

            final ImageView thumbnail = (ImageView) convertView.findViewById(R.id.iv_image);
            String imagePath = article.getImage();
            if (imagePath != null) {
                thumbnail.setVisibility(View.INVISIBLE);
                File imageFile = new File(getActivity().getCacheDir(), imagePath);

                new ImageLoadTask().execute(new ImageLoadTask.Container(thumbnail, imageFile));

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

    public interface SearchFragmentListener extends AbsNavDrawerFragment.IFragmentListener {
        public void onArticleSelected(Article article);
    }

}
