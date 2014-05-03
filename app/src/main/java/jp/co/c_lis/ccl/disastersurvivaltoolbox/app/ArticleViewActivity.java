package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

public class ArticleViewActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static final String KEY_ARTICLE = "article";

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticle = (Article) getIntent().getSerializableExtra(KEY_ARTICLE);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(mArticle.getTitle());

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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

            fragment = ArticleFragment.newInstance(column);
            tab.setTag(fragment);
            ab.addTab(tab);
        }

        setContentView(R.layout.activity_article);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.article, menu);
        return super.onCreateOptionsMenu(menu);
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
                image.setVisibility(View.VISIBLE);
                try {
                    image.setImageBitmap(BitmapFactory.decodeStream(
                            getActivity().getAssets().open(imageFileName)));
                } catch (IOException e) {
                }
            } else {
                image.setVisibility(View.GONE);
            }

            TextView abstraction = (TextView) rootView.findViewById(R.id.tv_description);
            abstraction.setText(article.getAbstaction());

            return rootView;
        }

    }

    public static class ArticleFragment extends Fragment {

        private static final String KEY_ARTICLE = "column";

        public static ArticleFragment newInstance(Article.Column column) {
            ArticleFragment fragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putSerializable(KEY_ARTICLE, column);
            fragment.setArguments(args);
            return fragment;
        }

        public ArticleFragment() {
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
                image.setVisibility(View.VISIBLE);
                try {
                    image.setImageBitmap(BitmapFactory.decodeStream(
                            getActivity().getAssets().open(imageFileName)));
                } catch (IOException e) {
                }
            } else {
                image.setVisibility(View.GONE);
            }

            return rootView;
        }

    }

}
