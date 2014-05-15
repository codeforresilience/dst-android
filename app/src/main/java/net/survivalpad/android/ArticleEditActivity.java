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

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.Author;
import net.survivalpad.android.entity.History;
import net.survivalpad.android.util.DbManager;
import net.survivalpad.android.util.FileUtils;
import net.survivalpad.android.util.MediaUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ArticleEditActivity extends ActionBarActivity implements
        ActionBar.TabListener, LoaderManager.LoaderCallbacks<Article>, TextWatcher,
        SummaryEditorFragment.Listener, SummaryTranslateFragment.Listener,
        ColumnEditorFragment.Listener, ColumnTranslateFragment.Listener {

    private static final String TAG = "ArticleEditActivity";

    public static final String KEY_ARTICLE_ID = "article_id";

    private History.Type mType = History.Type.CREATE;

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHandler = new MyHandler(this);

        String className = getIntent().getComponent().getClassName();
        if (className.lastIndexOf(".ArticleUpdateActivity") > -1) {
            mType = History.Type.UPDATE;
        } else if (className.lastIndexOf(".ArticleReplicateActivity") > -1) {
            mType = History.Type.REPLICATE;
        } else if (className.lastIndexOf(".ArticleTranslateActivity") > -1) {
            mType = History.Type.TRANSLATE;
        }

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        getSupportLoaderManager().initLoader(0x0, getIntent().getExtras(), this);

        setContentView(R.layout.activity_article);

    }

    private SQLiteDatabase mDb;

    @Override
    protected void onResume() {
        super.onResume();
        mDb = new DbManager(this, DbManager.FILE_NAME, null).getWritableDatabase();

        /* IMEの表示を消す */
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }, 50);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    private ActionBar.Tab newColumn() {
        Article.Column column = new Article.Column();
        mArticle.getColumns().add(column);

        Fragment fragment = ColumnEditorFragment.newInstance(column);

        ActionBar ab = getSupportActionBar();
        ActionBar.Tab tab = ab.newTab()
                .setText("Title")
                .setTabListener(this);
        tab.setTag(fragment);
        ab.addTab(tab, ab.getTabCount() - 1, false);
        return tab;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.article_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private boolean validate() {
        if (mArticle.getTitle().length() == 0) {
            Toast.makeText(this, R.string.title_is_blank, Toast.LENGTH_LONG).show();
            return false;
        }
        if (mArticle.getDisasterTypes().size() == 0) {
            Toast.makeText(this, R.string.disastertypes_are_not_selected, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean publish() {

        ActionBar ab = getSupportActionBar();
        int len = ab.getTabCount();
        for (int i = 0; i < len; i++) {
            ActionBar.Tab tab = ab.getTabAt(i);
            if (tab.getTag() instanceof BaseEditorFragment) {
                BaseEditorFragment frag = (BaseEditorFragment) tab.getTag();
                frag.publish();
            }
        }

        if (!validate()) {
            return false;
        }

        List<Author> authorList = new ArrayList<Author>();

        new Author().findAll(mDb, authorList);

        Author author = null;
        if (authorList.size() > 0) {
            author = authorList.get(0);
        }

        mArticle.setAuthor(author);

        History history = new History();
        history.setAuthor(author);
        history.setArticle(mArticle);

        if (mArticle.getId() == -1) {
            mArticle.insert(mDb);

        } else if (mType == History.Type.UPDATE) {
            mArticle.update(mDb);

        } else if (mType == History.Type.REPLICATE) {
            mArticle.setParentId(mArticle.getId());
            mArticle.insert(mDb);

        } else if (mType == History.Type.TRANSLATE) {
            mArticle.setParentId(mArticle.getId());
            mArticle.insert(mDb);
        }

        history.setType(mType);
        history.insert(mDb);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                if (publish()) {
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        Fragment fragment = (Fragment) tab.getTag();

        fragmentTransaction.replace(R.id.container, fragment);
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // do nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        ActionBar ab = getSupportActionBar();
        String title = ab.getSelectedTab().getText().toString().toUpperCase();
        if (title.equals(getString(R.string.summary).toUpperCase())) {
            ab.setTitle(s);
        } else {
            ab.getSelectedTab().setText(s);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }

    private BaseEditorFragment mEditorFragment;
    private Uri fileUri;

    private static final int REQUEST_IMAGE_CAPTURE = 0x01;

    @Override
    public void onTakePictureClicked(BaseEditorFragment fragment) {
        mEditorFragment = fragment;

        fileUri = MediaUtils.getOutputMediaFileUri();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK
                && mEditorFragment != null) {
            final File tmp = new File(fileUri.getPath());
            final File file = new File(
                    new File(FileUtils.getArticleDir(this), "images"), tmp.getName());

            Thread th = new Thread() {
                @Override
                public void run() {
                    try {
                        FileUtils.copy(tmp, file);
                        mEditorFragment.setImage(file.getName());

                        final Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mEditorFragment.setImageBitmap(imageBitmap);
                                mEditorFragment = null;
                                fileUri = null;
                            }
                        });
                    } catch (FileNotFoundException e) {
                    }
                }
            };
            th.start();
        }
    }

    @Override
    public Loader<Article> onCreateLoader(int id, Bundle args) {
        long articleId = -1;
        if (args != null) {
            articleId = args.getLong(KEY_ARTICLE_ID);
        }
        ArticleLoader loader = new ArticleLoader(this, articleId);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Article> loader, Article data) {
        mArticle = data;
        sHandler.sendMessage(sHandler.obtainMessage(MyHandler.HANDLE_SETUP_ACTIONBAR, mArticle));
    }

    @Override
    public void onLoaderReset(Loader<Article> loader) {
        // do nothing
    }

    private static MyHandler sHandler;

    private static class MyHandler extends Handler {
        public static final int HANDLE_SETUP_ACTIONBAR = 0x01;

        WeakReference<ArticleEditActivity> activity;

        MyHandler(ArticleEditActivity activity) {
            this.activity = new WeakReference<ArticleEditActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HANDLE_SETUP_ACTIONBAR: {
                    setupUi((Article) msg.obj);
                    break;
                }
            }
        }

        private void setupUi(Article article) {
            History.Type type = activity.get().mType;

            ActionBar ab = activity.get().getSupportActionBar();
            ab.setTitle(article.getTitle());

            ActionBar.Tab summaryTag = ab.newTab()
                    .setText(R.string.summary)
                    .setTabListener(activity.get());
            if (type == History.Type.TRANSLATE) {
                summaryTag.setTag(SummaryTranslateFragment.newInstance(article, type));
            } else {
                summaryTag.setTag(SummaryEditorFragment.newInstance(article, type));
            }
            ab.addTab(summaryTag, true);

            for (Article.Column column : article.getColumns()) {
                ActionBar.Tab tab = ab.newTab()
                        .setText(column.getTitle())
                        .setTabListener(activity.get());

                if (type == History.Type.TRANSLATE) {
                    tab.setTag(ColumnTranslateFragment.newInstance(column));
                } else {
                    tab.setTag(ColumnEditorFragment.newInstance(column));
                }
                ab.addTab(tab);
            }

            if (type != History.Type.TRANSLATE) {
                ActionBar.Tab addTab = ab.newTab()
                        .setIcon(android.R.drawable.ic_input_add)
                        .setTabListener(new ActionBar.TabListener() {
                            @Override
                            public void onTabSelected(ActionBar.Tab tab,
                                                      FragmentTransaction fragmentTransaction) {
                                final ActionBar.Tab newTab = activity.get().newColumn();
                                sHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        activity.get().getSupportActionBar().selectTab(newTab);
                                    }
                                }, 100);
                            }

                            @Override
                            public void onTabUnselected(ActionBar.Tab tab,
                                                        FragmentTransaction fragmentTransaction) {
                            }

                            @Override
                            public void onTabReselected(ActionBar.Tab tab,
                                                        FragmentTransaction fragmentTransaction) {
                            }
                        });
                ab.addTab(addTab, false);
            }
        }
    }

    private static class ArticleLoader extends AsyncTaskLoader<Article> {

        private long articleId;

        public ArticleLoader(Context context, long articleId) {
            super(context);

            this.articleId = articleId;
        }

        @Override
        public Article loadInBackground() {
            Article article = new Article();

            if (articleId != -1) {
                SQLiteDatabase db = new DbManager(getContext(), DbManager.FILE_NAME, null)
                        .getReadableDatabase();
                article.findById(articleId, db);
                db.close();
            }
            return article;
        }
    }
}
