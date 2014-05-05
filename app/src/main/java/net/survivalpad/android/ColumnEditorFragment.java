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

import net.survivalpad.android.entity.Article;

import java.io.File;

public class ColumnEditorFragment extends BaseEditorFragment<ColumnEditorFragment.Listener>
        implements View.OnClickListener {
    private static final String TAG = "ColumnEditorFragment";

    private static final String KEY_COLUMN = "column";

    private Article.Column column;

    public static ColumnEditorFragment newInstance(Article.Column column) {
        ColumnEditorFragment fragment = new ColumnEditorFragment();
        fragment.setRetainInstance(true);
        Bundle args = new Bundle();
        args.putSerializable(KEY_COLUMN, column);
        fragment.setArguments(args);
        return fragment;
    }

    public ColumnEditorFragment() {
    }

    private TextWatcher textWatcher;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        textWatcher = (TextWatcher) activity;
    }

    private EditText title;
    private EditText description;
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_edit, container, false);

        if (column == null) {
            column = (Article.Column) getArguments().getSerializable(KEY_COLUMN);
        } else {
            column = new Article.Column();
        }

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(column.getTitle());
        title.addTextChangedListener(textWatcher);

        description = (EditText) rootView.findViewById(R.id.et_description);
        description.setText(column.getDescription());

        image = (ImageButton) rootView.findViewById(R.id.ib_camera);
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
        }
        image.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void setImage(String fileName) {
        column.setImage(fileName);
    }

    @Override
    public void publish() {
        if (title != null && description != null) {
            column.setTitle(title.getText().toString());
            column.setDescription(description.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }
}
