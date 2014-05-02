package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

interface SearchFragmentListener extends AbsFragment.IFragmentListener {
    public void onArticleSelected(Article article);
}
