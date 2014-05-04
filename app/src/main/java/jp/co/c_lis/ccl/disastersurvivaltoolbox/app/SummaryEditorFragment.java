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
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ToggleButton;

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
    private ImageButton image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(KEY_ARTICLE);
        View rootView = inflater.inflate(R.layout.fragment_summary_edit, container, false);

        title = (EditText) rootView.findViewById(R.id.et_title);
        title.addTextChangedListener(textWatcher);

        disasterTypeAdapter = new DisasterTypeAdapter();
        GridView gridView = (GridView) rootView.findViewById(R.id.gv_disaster_types);
        gridView.setAdapter(disasterTypeAdapter);

        ImageButton ib = (ImageButton) rootView.findViewById(R.id.ib_camera);
        ib.setOnClickListener(this);

        if (image != null) {
            ib.setImageDrawable(image.getDrawable());
        }
        image = ib;

        abstraction = (EditText) rootView.findViewById(R.id.et_description);

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

    }

    @Override
    public void setImage(String fileName) {
        article.setImage(fileName);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image.setImageBitmap(bitmap);
    }

    private List<DisasterType> mDisasterTypeList = new ArrayList<DisasterType>();

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DisasterType type = (DisasterType) buttonView.getTag();
        if (isChecked) {
            article.getDisasterTypes().add(type);
        } else {
            article.getDisasterTypes().remove(type);
        }
    }

    private class DisasterTypeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDisasterTypeList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDisasterTypeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DisasterType type = (DisasterType) getItem(position);
            ToggleButton view = (ToggleButton) View.inflate(getActivity(), R.layout.disaster_type, null);

            view.setChecked(article.hasDisasterTypes(type));

            // 上に画像を表示
            view.setCompoundDrawablesWithIntrinsicBounds(0, type.getIcon(), 0, 0);
            view.setTextOn(type.getNameEn());
            view.setTextOff(type.getNameEn());
            view.setText(type.getNameEn());
            view.setPadding(5, 5, 5, 5);
            view.setOnCheckedChangeListener(SummaryEditorFragment.this);

            view.setTag(type);

            return view;
        }
    }

    public interface Listener extends BaseEditorFragment.Listener {
    }

}
