package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.History;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.FileUtils;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        HomeFragmentListener,
        SearchFragmentListener {
    private static final String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            getDatabasePath(DbManager.FILE_NAME).delete();
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                copyFromAsset();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                onAttach(getTitle(), R.menu.global);

                // Set up the drawer.
                mNavigationDrawerFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer_layout));
            }
        }.execute();
    }

    private void copyFromAsset() {
        if (!BuildConfig.DEBUG && getCacheDir().list().length > 0) {
            return;
        }

        AssetManager am = getAssets();
        String[] files = null;
        try {
            files = am.list("");
        } catch (IOException e) {
        }

        if (files == null) {
            return;
        }

        for (String file : files) {
            Log.d(TAG, "file = " + file);
            File to = new File(getCacheDir(), file);

            try {
                FileUtils.copy(am.open(file), to);
            } catch (IOException e) {
            }

        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int id) {

        Fragment fragment = null;
        switch (id) {
            case R.string.title_home:
                fragment = HomeFragment.newInstance();
                break;
            case R.string.title_search:
                fragment = SearchFragment.newInstance();
                break;
            case R.string.title_signout:
                Toast.makeText(this, R.string.title_signout, Toast.LENGTH_LONG).show();
                finish();
                return;
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    private CharSequence mTitle;
    private int mMenuId;

    @Override
    public void onAttach(CharSequence title, int menuId) {
        mTitle = title;
        mMenuId = menuId;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(mMenuId, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add) {
            Intent intent = new Intent(this, ArticleEditActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHistorySelected(History history) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHistorySelected " + history.getArticle().getId());
        }
        Intent intent = new Intent(this, ArticleViewActivity.class);
        intent.putExtra(ArticleViewActivity.KEY_ARTICLE, history.getArticle());
        startActivity(intent);
    }

    @Override
    public void onArticleSelected(Article article) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onArticleSelected " + article.getId());
        }
        Intent intent = new Intent(this, ArticleViewActivity.class);
        intent.putExtra(ArticleViewActivity.KEY_ARTICLE, article);
        startActivity(intent);
    }
}
