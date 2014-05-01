package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.History;

interface HomeFragmentListener extends AbsFragment.IFragmentListener {
    public void onHistorySelected(History history);
}
