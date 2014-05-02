package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;

public abstract class AbsFragment<T extends AbsFragment.IFragmentListener> extends Fragment {

    public abstract CharSequence getTitle();

    public abstract int getMenuId();

    T mListener;

    SQLiteDatabase mDb;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (T) activity;
        mListener.onAttach(getTitle(), getMenuId());

        mDb = new DbManager(activity, DbManager.FILE_NAME, null).getWritableDatabase();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mDb != null) {
            mDb.close();
            mDb = null;
        }
    }

    public interface IFragmentListener {

        public void onAttach(CharSequence title, int menuId);
    }

}
