package net.survivalpad.android;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.File;

import net.survivalpad.android.entity.Article;

public class ColumnTranslateFragment extends BaseEditorFragment<ColumnTranslateFragment.Listener>
        implements View.OnClickListener {
    private static final String TAG = "ColumnEditorFragment";

    private static final String KEY_COLUMN = "column";

    private Article.Column column;

    public static ColumnTranslateFragment newInstance(Article.Column column) {
        ColumnTranslateFragment fragment = new ColumnTranslateFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putSerializable(KEY_COLUMN, column);
        fragment.setArguments(args);
        return fragment;
    }

    public ColumnTranslateFragment() {
    }

    private TextWatcher textWatcher;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        textWatcher = (TextWatcher) activity;
    }

    String imageFileName;

    private EditText title2;
    private EditText description2;
    private ImageButton image2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_translate, container, false);

        column = (Article.Column) getArguments().getSerializable(KEY_COLUMN);
        if (column == null) {
            column = new Article.Column();
        }

        EditText title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(column.getTitle());

        title2 = (EditText) rootView.findViewById(R.id.et_title2);
        title2.addTextChangedListener(textWatcher);

        EditText description = (EditText) rootView.findViewById(R.id.et_description);
        description.setText(column.getDescription());

        description2 = (EditText) rootView.findViewById(R.id.et_description2);

        ImageButton image = (ImageButton) rootView.findViewById(R.id.ib_camera);

        image2 = (ImageButton) rootView.findViewById(R.id.ib_camera2);
        image2.setOnClickListener(this);

        // FIXME: 写真撮影後、タブを切り替えて戻ってくると、翻訳前の参考画像も撮影した画像に置き換わる
        if (column.getImage() != null) {
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (column.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }
            }.execute(new ImageLoadTask.Container(image, new File(getActivity().getCacheDir(), column.getImage())));
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (column.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }
            }.execute(new ImageLoadTask.Container(image2, new File(getActivity().getCacheDir(), column.getImage())));
        }

        return rootView;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image2.setImageBitmap(bitmap);
    }

    @Override
    public void setImage(String fileName) {
        column.setImage(fileName);
    }

    @Override
    public void publish() {
        if (title2 != null && description2 != null) {
            column.setTitle(title2.getText().toString());
            column.setDescription(description2.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }
}
