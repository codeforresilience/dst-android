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
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.History;
import net.survivalpad.android.util.DbManager;
import net.survivalpad.android.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        LoaderManager.LoaderCallbacks<Void>,
        HomeFragment.HomeFragmentListener,
        SearchFragment.SearchFragmentListener {
    private static final String TAG = "MainActivity";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private static class InitLoader extends AsyncTaskLoader<Void> {

        public InitLoader(Context context) {
            super(context);
        }

        @Override
        public Void loadInBackground() {
            copyFromAsset(getContext());
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            getDatabasePath(DbManager.FILE_NAME).delete();
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        onAttach(getTitle(), R.menu.global);

        getSupportLoaderManager().initLoader(0x0, null, this);
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        InitLoader loader = new InitLoader(this);
        loader.forceLoad();
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
        // do nothing
    }

    private static void copyFromAsset(Context context) {
        if (!BuildConfig.DEBUG && context.getCacheDir().list().length > 0) {
            return;
        }

        AssetManager am = context.getAssets();
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
            File to = new File(context.getCacheDir(), file);

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
            if (mMenuId != -1) {
                getMenuInflater().inflate(mMenuId, menu);
                restoreActionBar();
                return true;
            }
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
        intent.putExtra(ArticleViewActivity.KEY_ARTICLE_ID, history.getArticle().getId());
        startActivity(intent);
    }

    @Override
    public void onArticleSelected(Article article) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onArticleSelected " + article.getId());
        }
        Intent intent = new Intent(this, ArticleViewActivity.class);
        intent.putExtra(ArticleViewActivity.KEY_ARTICLE_ID, article.getId());
        startActivity(intent);
    }
}
