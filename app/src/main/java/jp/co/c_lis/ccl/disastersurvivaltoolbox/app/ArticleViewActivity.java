package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;

public class ArticleViewActivity extends ActionBarActivity implements ActionBar.TabListener {
    private static final String TAG = "ArticleViewActivity";

    public static final String KEY_ARTICLE_ID = "article_id";

    private long mArticleId;
    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticleId = getIntent().getLongExtra(KEY_ARTICLE_ID, -1);
        setContentView(R.layout.activity_article);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mArticle = new Article();
        if (mArticleId != -1) {
            SQLiteDatabase db = new DbManager(this, DbManager.FILE_NAME, null).getReadableDatabase();
            mArticle.findById(mArticleId, db);
            db.close();
        }

        ActionBar ab = getSupportActionBar();
        ab.setTitle(mArticle.getTitle());

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.removeAllTabs();

        ActionBar.Tab tab = ab.newTab()
                .setText(R.string.summary)
                .setTabListener(this);

        Fragment fragment = SummaryFragment.newInstance(mArticle);
        tab.setTag(fragment);
        ab.addTab(tab, true);

        for (Article.Column column : mArticle.getColumns()) {
            tab = ab.newTab()
                    .setText(column.getTitle())
                    .setTabListener(this);

            fragment = ColumnFragment.newInstance(column);
            tab.setTag(fragment);
            ab.addTab(tab);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.article, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update) {
            Log.d(TAG, "article_id = " + mArticle.getId());

            Intent intent = new Intent();
            intent.setClassName(getPackageName(), getPackageName() + ".ArticleUpdateActivity");
            intent.putExtra(ArticleEditActivity.KEY_ARTICLE, mArticle);
            startActivity(intent);
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
