package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

public class ArticleEditActivity extends ActionBarActivity implements
        ActionBar.TabListener, TextWatcher, View.OnClickListener {
    private static final String TAG = "ArticleEditActivity";

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
                .setIcon(android.R.drawable.ic_input_add)
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

    private ImageButton mRecieveImage;
    private static final int REQUEST_IMAGE_CAPTURE = 0x01;

    @Override
    public void onClick(View v) {
        if (v instanceof ImageButton) {
            mRecieveImage = (ImageButton) v;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && mRecieveImage != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mRecieveImage.setImageBitmap(imageBitmap);
            mRecieveImage = null;

        }
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

        private TextWatcher textWatcher;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            textWatcher = (TextWatcher) activity;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Article article = (Article) getArguments().getSerializable(KEY_ARTICLE);
            View rootView = inflater.inflate(R.layout.fragment_summary_edit, container, false);

            EditText title = (EditText) rootView.findViewById(R.id.et_title);
            title.addTextChangedListener(textWatcher);

            TextView abstraction = (TextView) rootView.findViewById(R.id.et_description);

            return rootView;
        }

    }

    public static class ColumnFragment extends Fragment {

        private static final String KEY_COLUMN = "column";

        public static ColumnFragment newInstance() {
            ColumnFragment fragment = new ColumnFragment();
            fragment.setRetainInstance(true);
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }

        public ColumnFragment() {
        }

        private View.OnClickListener onClickListener;

        private TextWatcher textWatcher;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            onClickListener = (View.OnClickListener) activity;
            textWatcher = (TextWatcher) activity;
        }

        private ImageButton image;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_column_edit, container, false);

            EditText title = (EditText) rootView.findViewById(R.id.et_title);
            title.addTextChangedListener(textWatcher);

            EditText description = (EditText) rootView.findViewById(R.id.et_description);

            ImageButton ib = (ImageButton) rootView.findViewById(R.id.ib_camera);
            ib.setOnClickListener(onClickListener);

            if (image != null) {
                ib.setImageDrawable(image.getDrawable());
            }
            image = ib;

            return rootView;
        }
    }

}
