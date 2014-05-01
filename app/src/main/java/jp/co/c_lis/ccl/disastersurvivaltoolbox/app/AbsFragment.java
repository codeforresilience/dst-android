package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.support.v4.app.Fragment;

public abstract class AbsFragment<T extends AbsFragment.IFragmentListener> extends Fragment {

    public abstract CharSequence getTitle();
    public abstract int getMenuId();

    T mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mListener = (T) activity;
        mListener.onAttach(getTitle(), getMenuId());
    }

    public interface IFragmentListener {

        public void onAttach(CharSequence title, int menuId);
    }

}
