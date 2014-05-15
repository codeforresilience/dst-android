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
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.DisasterType;
import net.survivalpad.android.entity.History;
import net.survivalpad.android.util.DbManager;
import net.survivalpad.android.util.FileUtils;
import net.survivalpad.android.view.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * TODO: 画面回転時に既に入力している情報を保存
 */
public class SummaryEditorFragment extends BaseEditorFragment<SummaryEditorFragment.Listener>
        implements View.OnClickListener,
        ImageView.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {

    private static final String KEY_ARTICLE = "article";
    private static final String KEY_TYPE = "type";

    private static final Locale[] LOCALES = new Locale[]{
            Locale.ENGLISH,
            Locale.GERMANY,
            Locale.JAPANESE,
    };

    private static int getLocaleIndex(Locale[] locales, String locale) {
        int len = locales.length;
        for (int i = 0; i < len; i++) {
            Locale loc = locales[i];
            if (loc.getLanguage().equals(locale)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Locale locale = (Locale) parent.getAdapter().getItem(position);
        article.setLanguage(locale.getLanguage());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        article.setLanguage(null);
    }

    private class LanguageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return LOCALES.length;
        }

        @Override
        public Object getItem(int position) {
            return LOCALES[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Locale locale = (Locale) getItem(position);

            TextView tv = (TextView) View.inflate(getActivity(), R.layout.language_row, null);
            tv.setText(locale.getDisplayLanguage());
            return tv;
        }
    }

    private Article article;

    public static SummaryEditorFragment newInstance(Article article, History.Type type) {
        SummaryEditorFragment fragment = new SummaryEditorFragment();
        fragment.setRetainInstance(true);

        Bundle args = new Bundle();
        args.putSerializable(KEY_ARTICLE, article);
        args.putInt(KEY_TYPE, type.value);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryEditorFragment() {
    }

    private SQLiteDatabase mDb;
    private TextWatcher textWatcher;

    private Handler mHandler = new Handler();

    private List<DisasterType> mDisasterTypeList = new ArrayList<DisasterType>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mDb = new DbManager(activity, DbManager.FILE_NAME, null).getWritableDatabase();
        textWatcher = (TextWatcher) activity;

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();

                mDisasterTypeList.clear();
                new DisasterType().findAll(mDb, mDisasterTypeList);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        disasterTypeAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        th.start();

    }

    private DisasterTypeAdapter disasterTypeAdapter;

    private EditText title;
    private EditText abstraction;
    private EditText source;
    private EditText sourceUrl;
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (article == null) {
            article = (Article) getArguments().getSerializable(KEY_ARTICLE);
            article.getDisasterTypes().clear();
            new Article.ArticleDisasterType()
                    .findByArticleId(article.getId(), article.getDisasterTypes(), mDb);
        }

        View rootView = inflater.inflate(R.layout.fragment_summary_edit, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.sp_language);
        spinner.setAdapter(new LanguageAdapter());

        int localeIndex = getLocaleIndex(LOCALES, article.getLanguage());
        spinner.setSelection(localeIndex);

        spinner.setOnItemSelectedListener(this);

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(article.getTitle());

        /*
         * FIXME イベントの設定タイミング
         * この時点でTextWatcherを設定すると、画面の回転時にonTextChangedが発生して、
         * ActionBar関係の処理でNullPointerExceptionが発生する。
         *
         * そのため、onFocusのみを監視して、実際のTextWatcher設定を遅延している。
         */
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && textWatcher != null) {
                    title.addTextChangedListener(textWatcher);
                    textWatcher = null;
                }
            }
        });

        disasterTypeAdapter = new DisasterTypeAdapter(getActivity(), this, mDisasterTypeList,
                article.getDisasterTypes());

        GridView gridView = (GridView) rootView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(disasterTypeAdapter);

        image = (ImageButton) rootView.findViewById(R.id.ib_camera);

        if (article.getImage() != null) {
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (article.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }

            }.execute(new ImageLoadTask.Container(image,
                    FileUtils.getArticleImage(getActivity(), article.getImage())));
        }
        image.setOnClickListener(this);

        abstraction = (EditText) rootView.findViewById(R.id.et_description);
        abstraction.setText(article.getAbstraction());

        source = (EditText) rootView.findViewById(R.id.et_source);
        sourceUrl = (EditText) rootView.findViewById(R.id.et_source_url);

        source.setText(article.getSource());
        sourceUrl.setText(article.getSourceUrl());

        return rootView;
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    @Override
    public void publish() {
        article.setTitle(title.getText().toString());
        article.setAbstraction(abstraction.getText().toString());
        article.setSource(source.getText().toString());
        article.setSourceUrl(sourceUrl.getText().toString());

    }

    @Override
    public void setImage(String fileName) {
        article.setImage(fileName);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void onCheckedChanged(ImageView view, boolean isChecked) {
        DisasterType type = (DisasterType) view.getTag();
        if (isChecked) {
            article.getDisasterTypes().add(type);
        } else {
            article.getDisasterTypes().remove(type);
        }
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }

}
