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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.util.FileUtils;

import java.io.File;

/**
 * TODO: 画面回転時に既に入力している情報を保存
 */
public class ColumnEditorFragment extends BaseEditorFragment<ColumnEditorFragment.Listener>
        implements View.OnClickListener {
    private static final String TAG = "ColumnEditorFragment";

    private static final String KEY_COLUMN = "column";

    private Article.Column column;

    public static ColumnEditorFragment newInstance(Article.Column column) {
        ColumnEditorFragment fragment = new ColumnEditorFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putSerializable(KEY_COLUMN, column);
        fragment.setArguments(args);
        return fragment;
    }

    public ColumnEditorFragment() {
    }

    private TextWatcher textWatcher;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        textWatcher = (TextWatcher) activity;
    }

    private EditText title;
    private EditText description;
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_edit, container, false);

        if (column == null) {
            column = (Article.Column) getArguments().getSerializable(KEY_COLUMN);
        }

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(column.getTitle());

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

        description = (EditText) rootView.findViewById(R.id.et_description);
        description.setText(column.getDescription());

        image = (ImageButton) rootView.findViewById(R.id.ib_camera);
        if (column.getImage() != null) {
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (column.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }
            }.execute(new ImageLoadTask.Container(image, FileUtils.getArticleImage(getActivity(), column.getImage())));
        }
        image.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void setImage(String fileName) {
        column.setImage(fileName);
    }

    @Override
    public void publish() {
        if (title != null && description != null) {
            column.setTitle(title.getText().toString());
            column.setDescription(description.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }
}
