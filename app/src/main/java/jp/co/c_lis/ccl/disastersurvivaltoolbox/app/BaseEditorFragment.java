package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;

public abstract class BaseEditorFragment<T extends BaseEditorFragment.Listener> extends Fragment {

    T listener;

    public abstract void publish();

    public abstract void setImage(String fileName);

    public abstract void setImageBitmap(Bitmap bitmap);

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        listener = (T) activity;
    }

    public interface Listener {
        public void onTakePictureClicked(BaseEditorFragment fragment);
    }

}
