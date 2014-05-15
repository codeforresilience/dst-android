package net.survivalpad.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

public class ImageView extends android.widget.ImageView implements Checkable, View.OnClickListener {

    private boolean isChecked = true;
    private OnCheckedChangeListener onCheckedChangeListener;

    public ImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    @Override
    public void setChecked(boolean checked) {
        isChecked = checked;

        setAlpha(isChecked ? 250 : 100);
    }

    @Override
    public boolean isChecked() {
        return isChecked();
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        onCheckedChangeListener = l;

    }

    @Override
    public void onClick(View v) {
        toggle();

        if (onCheckedChangeListener != null) {
            onCheckedChangeListener.onCheckedChanged(this, isChecked);
        }
    }

    public interface OnCheckedChangeListener {
        public void onCheckedChanged(ImageView view, boolean isChecked);
    }
}
