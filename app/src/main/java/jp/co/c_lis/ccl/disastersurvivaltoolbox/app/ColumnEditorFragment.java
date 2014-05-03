package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;

public class ColumnEditorFragment extends BaseEditorFragment implements View.OnClickListener {
    private static final String TAG = "ColumnEditorFragment";

    private static final String KEY_COLUMN = "column";

    private Article.Column column;

    public Article.Column getColumn() {
        return column;
    }

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

        listener = (Listener) activity;
        textWatcher = (TextWatcher) activity;
    }

    private EditText title;
    private EditText description;
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_column_edit, container, false);

        column = (Article.Column) getArguments().getSerializable(KEY_COLUMN);
        if (column == null) {
            column = new Article.Column();
        }

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(column.getTitle());
        title.addTextChangedListener(textWatcher);

        description = (EditText) rootView.findViewById(R.id.et_description);
        description.setText(column.getDescription());

        ImageButton ib = (ImageButton) rootView.findViewById(R.id.ib_camera);
        ib.setOnClickListener(this);

        if (image != null) {
            ib.setImageDrawable(image.getDrawable());
        }
        image = ib;

        return rootView;
    }

    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void publish() {
        column.setTitle(title.getText().toString());
        column.setDescription(description.getText().toString());
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    private Listener listener;

    public interface Listener {
        public void onTakePictureClicked(ColumnEditorFragment fragment);
    }

}
