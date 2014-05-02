package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

public class ArticleEditActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String KEY_ARTICLE = "article";

    private Article mArticle;

    private ActionBar.Tab mAddTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().hasExtra(KEY_ARTICLE)) {
            mArticle = (Article) getIntent().getSerializableExtra(KEY_ARTICLE);
        } else {
            mArticle = new Article();
        }

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setTitle("New Article");

        ActionBar.Tab summaryTag = ab.newTab()
                .setText(R.string.summary)
                .setTabListener(this);
        summaryTag.setTag(SummaryFragment.newInstance(mArticle));
        ab.addTab(summaryTag, true);

        mAddTag = ab.newTab()
                .setText("+")
                .setTabListener(new ActionBar.TabListener() {
                    @Override
                    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                        final ActionBar.Tab newTab = newColumn();
                        sHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getSupportActionBar().selectTab(newTab);
                            }
                        }, 100);
                    }

                    @Override
                    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    }

                    @Override
                    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
                    }
                });
        ab.addTab(mAddTag, false);

        setContentView(R.layout.activity_article);

    }

    private static Handler sHandler = new Handler();

    private ActionBar.Tab newColumn() {
        Fragment fragment = ColumnFragment.newInstance();

        ActionBar ab = getSupportActionBar();
        ActionBar.Tab tab = ab.newTab()
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_complete:
                finish();
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
            View rootView = inflater.inflate(R.layout.fragment_summary_edit, container, false);

            TextView abstraction = (TextView) rootView.findViewById(R.id.et_description);

            return rootView;
        }

    }

    public static class ColumnFragment extends Fragment {

        private static final String KEY_ARTICLE = "column";

        public static ColumnFragment newInstance() {
            ColumnFragment fragment = new ColumnFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public ColumnFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_column_edit, container, false);

            EditText title = (EditText) rootView.findViewById(R.id.et_title);
            EditText description = (EditText) rootView.findViewById(R.id.et_description);
            ImageView image = (ImageView) rootView.findViewById(R.id.iv_image);

            return rootView;
        }

    }

}
