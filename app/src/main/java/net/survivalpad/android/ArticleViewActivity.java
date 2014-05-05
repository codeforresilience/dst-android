package net.survivalpad.android;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.util.DbManager;

public class ArticleViewActivity extends ActionBarActivity
        implements ActionBar.TabListener, LoaderManager.LoaderCallbacks<Article> {
    private static final String TAG = "ArticleViewActivity";

    public static final String KEY_ARTICLE_ID = "article_id";

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sHandler = new MyHandler(this);

        setContentView(R.layout.activity_article);

        getSupportLoaderManager().initLoader(0x0, getIntent().getExtras(), this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getSupportLoaderManager().restartLoader(0x0, getIntent().getExtras(), this);

        ActionBar ab = getSupportActionBar();

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.article, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent intent = new Intent();

        int id = item.getItemId();
        if (id == R.id.action_update) {
            intent.setClassName(getPackageName(), getPackageName() + ".ArticleUpdateActivity");
        } else if (id == R.id.action_replicate) {
            intent.setClassName(getPackageName(), getPackageName() + ".ArticleReplicateActivity");
        } else if (id == R.id.action_translate) {
            intent.setClassName(getPackageName(), getPackageName() + ".ArticleTranslateActivity");
        }

        intent.putExtra(ArticleEditActivity.KEY_ARTICLE_ID, mArticle.getId());
        startActivity(intent);

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

    private static class ArticleLoader extends AsyncTaskLoader<Article> {
        private long articleId;

        public ArticleLoader(Context context, long articleId) {
            super(context);
            this.articleId = articleId;
        }

        @Override
        public Article loadInBackground() {
            Article article = new Article();
            if (articleId > -1) {
                SQLiteDatabase db = new DbManager(getContext(), DbManager.FILE_NAME, null)
                        .getReadableDatabase();
                article.findById(articleId, db);
                db.close();
            }
            return article;
        }

    }

    @Override
    public android.support.v4.content.Loader<Article> onCreateLoader(int id, Bundle args) {
        ArticleLoader loader = new ArticleLoader(this, args.getLong(KEY_ARTICLE_ID));
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Article> loader, Article data) {
        mArticle = data;
        sHandler.sendMessage(sHandler.obtainMessage(MyHandler.HANDLE_SETUP_ACTIONBAR, mArticle));
    }

    private static MyHandler sHandler;

    private static class MyHandler extends Handler {
        public static final int HANDLE_SETUP_ACTIONBAR = 0x01;

        WeakReference<ArticleViewActivity> activity;

        MyHandler(ArticleViewActivity activity) {
            this.activity = new WeakReference<ArticleViewActivity>(activity);
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
            ActionBar ab = activity.get().getSupportActionBar();
            ab.removeAllTabs();
            ab.setTitle(article.getTitle());

            ActionBar.Tab tab = ab.newTab()
                    .setText(R.string.summary)
                    .setTabListener(activity.get());

            Fragment fragment = SummaryFragment.newInstance(article);
            tab.setTag(fragment);
            ab.addTab(tab, true);

            for (Article.Column column : article.getColumns()) {
                tab = ab.newTab()
                        .setText(column.getTitle())
                        .setTabListener(activity.get());

                fragment = ColumnFragment.newInstance(column);
                tab.setTag(fragment);
                ab.addTab(tab);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Article> loader) {
        // do nothing
    }

    public static class SummaryFragment extends Fragment {

        private static final String KEY_ARTICLE = "article";

        public static SummaryFragment newInstance(Article article) {
            SummaryFragment fragment = new SummaryFragment();
            Bundle args = new Bundle();
            args.putSerializable(KEY_ARTICLE, article);
            fragment.setArguments(args);
            return fragment;
        }

        public SummaryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Article article = (Article) getArguments().getSerializable(KEY_ARTICLE);
            View rootView = inflater.inflate(R.layout.fragment_summary, container, false);

            ImageView image = (ImageView) rootView.findViewById(R.id.iv_image);
            String imageFileName = article.getImage();
            if (imageFileName != null) {
                image.setVisibility(View.INVISIBLE);
                File imageFile = new File(getActivity().getCacheDir(), article.getImage());
                new ImageLoadTask().execute(new ImageLoadTask.Container(image, imageFile));
            } else {
                image.setVisibility(View.GONE);
            }

            TextView abstraction = (TextView) rootView.findViewById(R.id.tv_description);
            abstraction.setText(article.getAbstaction());

            return rootView;
        }

    }

    public static class ColumnFragment extends Fragment {

        private static final String KEY_ARTICLE = "column";

        public static ColumnFragment newInstance(Article.Column column) {
            ColumnFragment fragment = new ColumnFragment();
            Bundle args = new Bundle();
            args.putSerializable(KEY_ARTICLE, column);
            fragment.setArguments(args);
            return fragment;
        }

        public ColumnFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Article.Column column = (Article.Column) getArguments().getSerializable(KEY_ARTICLE);
            View rootView = inflater.inflate(R.layout.fragment_column, container, false);

            TextView title = (TextView) rootView.findViewById(R.id.tv_title);
            title.setText(column.getTitle());

            TextView description = (TextView) rootView.findViewById(R.id.tv_description);
            description.setText(column.getDescription());

            ImageView image = (ImageView) rootView.findViewById(R.id.iv_image);
            String imageFileName = column.getImage();
            if (imageFileName != null) {
                image.setVisibility(View.INVISIBLE);

                File imageFile = new File(getActivity().getCacheDir(), column.getImage());
                new ImageLoadTask().execute(new ImageLoadTask.Container(image, imageFile));
            } else {
                image.setVisibility(View.GONE);
            }

            return rootView;
        }

    }

}
