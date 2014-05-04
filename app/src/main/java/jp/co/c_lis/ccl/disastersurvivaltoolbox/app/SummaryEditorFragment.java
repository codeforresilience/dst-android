package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.DisasterType;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;

public class SummaryEditorFragment extends BaseEditorFragment<SummaryEditorFragment.Listener>
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String KEY_ARTICLE = "article";

    private Article article;

    public static SummaryEditorFragment newInstance(Article article) {
        SummaryEditorFragment fragment = new SummaryEditorFragment();
        fragment.setRetainInstance(true);

        Bundle args = new Bundle();
        args.putSerializable(KEY_ARTICLE, article);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryEditorFragment() {
    }

    private SQLiteDatabase mDb;
    private TextWatcher textWatcher;

    private Handler mHandler = new Handler();

    private List<DisasterType> mDisasterTypeList = new ArrayList<DisasterType>();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mDb = new DbManager(activity, DbManager.FILE_NAME, null).getWritableDatabase();
        textWatcher = (TextWatcher) activity;

        Thread th = new Thread() {
            @Override
            public void run() {
                super.run();

                mDisasterTypeList.clear();
                new DisasterType().findAll(mDb, mDisasterTypeList);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        disasterTypeAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        th.start();

    }

    private DisasterTypeAdapter disasterTypeAdapter;

    private EditText title;
    private EditText abstraction;
    private EditText source;
    private EditText sourceUrl;
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(KEY_ARTICLE);

        article.getDisasterTypes().clear();
        new Article.ArticleDisasterType()
                .findByArticleId(article.getId(), article.getDisasterTypes(), mDb);

        View rootView = inflater.inflate(R.layout.fragment_summary_edit, container, false);

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(article.getTitle());
        title.addTextChangedListener(textWatcher);

        disasterTypeAdapter = new DisasterTypeAdapter(getActivity(), this, mDisasterTypeList,
                article.getDisasterTypes());

        GridView gridView = (GridView) rootView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(disasterTypeAdapter);

        final ImageButton ib = (ImageButton) rootView.findViewById(R.id.ib_camera);

        if (article.getImage() != null) {
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (article.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    super.onPostExecute(bitmap);

                    if (image != null) {
                        ib.setImageDrawable(image.getDrawable());
                    }
                    image = ib;
                }
            }.execute(new ImageLoadTask.Container(ib, new File(getActivity().getCacheDir(), article.getImage())));
        }
        ib.setOnClickListener(this);

        abstraction = (EditText) rootView.findViewById(R.id.et_description);
        abstraction.setText(article.getAbstaction());

        source = (EditText) rootView.findViewById(R.id.et_source);
        sourceUrl = (EditText) rootView.findViewById(R.id.et_source_url);

        source.setText(article.getSource());
        sourceUrl.setText(article.getSourceUrl());

        return rootView;
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    @Override
    public void publish() {
        article.setTitle(title.getText().toString());
        article.setAbstaction(abstraction.getText().toString());
        article.setSource(source.getText().toString());
        article.setSourceUrl(sourceUrl.getText().toString());

    }

    @Override
    public void setImage(String fileName) {
        article.setImage(fileName);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DisasterType type = (DisasterType) buttonView.getTag();
        if (isChecked) {
            article.getDisasterTypes().add(type);
        } else {
            article.getDisasterTypes().remove(type);
        }
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }

}
