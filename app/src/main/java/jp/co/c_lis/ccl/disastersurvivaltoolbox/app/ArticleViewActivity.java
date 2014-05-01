package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

public class ArticleViewActivity extends ActionBarActivity {

    public static final String KEY_ARTICLE = "article";

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mArticle = (Article) getIntent().getSerializableExtra(KEY_ARTICLE);

        setContentView(R.layout.activity_article);

        Fragment fragment = ArticleFragment.newInstance(mArticle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();

    }

    public static class ArticleFragment extends Fragment {

        private static final String KEY_ARTICLE = "article";

        public static ArticleFragment newInstance(Article article) {
            ArticleFragment fragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putSerializable(KEY_ARTICLE, article);
            fragment.setArguments(args);
            return fragment;
        }

        public ArticleFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_article, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(R.string.title_article);
            return rootView;
        }

    }

}
