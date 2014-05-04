package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Author;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.History;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.FileUtils;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.MediaUtils;

public class ArticleEditActivity extends ActionBarActivity implements
        ActionBar.TabListener, TextWatcher,
        SummaryEditorFragment.Listener,
        ColumnEditorFragment.Listener {
    private static final String TAG = "ArticleEditActivity";

    public static final String KEY_ARTICLE = "article";

    private Article mArticle;

    private ActionBar.Tab mAddTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        if (getIntent().hasExtra(KEY_ARTICLE)) {
            mArticle = (Article) getIntent().getSerializableExtra(KEY_ARTICLE);
            ab.setTitle(mArticle.getTitle());
        } else {
            mArticle = new Article();
            ab.setTitle("New Article");
        }

        ActionBar.Tab summaryTag = ab.newTab()
                .setText(R.string.summary)
                .setTabListener(this);
        summaryTag.setTag(SummaryEditorFragment.newInstance(mArticle));
        ab.addTab(summaryTag, true);

        for (Article.Column column : mArticle.getColumns()) {
            ActionBar.Tab tab = ab.newTab()
                    .setText(column.getTitle())
                    .setTabListener(this);

            Fragment fragment = ColumnEditorFragment.newInstance(column);
            tab.setTag(fragment);

            ab.addTab(tab);
        }

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

    private SQLiteDatabase mDb;

    @Override
    protected void onResume() {
        super.onResume();
        mDb = new DbManager(this, DbManager.FILE_NAME, null).getWritableDatabase();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    private static Handler sHandler = new Handler();

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
            history.setType(History.Type.Created);
        } else if (getIntent().getComponent().getClassName().lastIndexOf(".ArticleUpdateActivity") > -1) {
            mArticle.update(mDb);
            history.setType(History.Type.Updated);
        } else if (getIntent().getComponent().getClassName().lastIndexOf(".ArticleReplicateActivity") > -1) {
            mArticle.setParentId(mArticle.getId());
            mArticle.insert(mDb);
            history.setType(History.Type.Replicated);
        }

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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && mEditorFragment != null) {
            final File tmp = new File(fileUri.getPath());
            final File file = new File(getCacheDir(), tmp.getName());

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
}
