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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.Article;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.DisasterType;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.History;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.DbManager;

public class SummaryTranslateFragment extends BaseEditorFragment<SummaryTranslateFragment.Listener>
        implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {

    private static final String KEY_ARTICLE = "article";
    private static final String KEY_TYPE = "type";

    private static final Locale[] LOCALES = new Locale[]{
            Locale.ENGLISH,
            Locale.GERMANY,
            Locale.JAPANESE,
    };

    private static int getLocaleIndex(Locale[] locales, String locale) {
        int len = locales.length;
        for (int i = 0; i < len; i++) {
            Locale loc = locales[i];
            if (loc.getLanguage().equals(locale)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Locale locale = (Locale) parent.getAdapter().getItem(position);
        article.setLanguage(locale.getLanguage());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        article.setLanguage(null);
    }

    private class LanguageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return LOCALES.length;
        }

        @Override
        public Object getItem(int position) {
            return LOCALES[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Locale locale = (Locale) getItem(position);

            TextView tv = (TextView) View.inflate(getActivity(), R.layout.language_row, null);
            tv.setText(locale.getDisplayLanguage());
            return tv;
        }
    }

    private Article article;

    public static SummaryTranslateFragment newInstance(Article article, History.Type type) {
        SummaryTranslateFragment fragment = new SummaryTranslateFragment();
        fragment.setRetainInstance(true);

        Bundle args = new Bundle();
        args.putSerializable(KEY_ARTICLE, article);
        args.putInt(KEY_TYPE, type.value);
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryTranslateFragment() {
    }

    private SQLiteDatabase mDb;
    private TextWatcher textWatcher;

    private Handler mHandler = new Handler();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mDb = new DbManager(activity, DbManager.FILE_NAME, null).getWritableDatabase();
        textWatcher = (TextWatcher) activity;
    }


    private EditText title2;
    private EditText abstraction2;
    private EditText source2;
    private EditText sourceUrl2;
    private ImageButton image2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(KEY_ARTICLE);

        article.getDisasterTypes().clear();
        new Article.ArticleDisasterType()
                .findByArticleId(article.getId(), article.getDisasterTypes(), mDb);

        View rootView = inflater.inflate(R.layout.fragment_summary_translate, container, false);

        Spinner spinner = (Spinner) rootView.findViewById(R.id.sp_language);
        spinner.setAdapter(new LanguageAdapter());

        Spinner spinner2 = (Spinner) rootView.findViewById(R.id.sp_language2);
        spinner2.setAdapter(new LanguageAdapter());
        spinner2.setOnItemSelectedListener(this);

        int localeIndex = getLocaleIndex(LOCALES, article.getLanguage());
        spinner.setSelection(localeIndex);
        spinner2.setSelection(localeIndex);

        TextView title = (EditText) rootView.findViewById(R.id.et_title);
        title.setText(article.getTitle());

        ImageButton image = (ImageButton) rootView.findViewById(R.id.ib_camera);

        EditText abstraction = (EditText) rootView.findViewById(R.id.et_description);
        abstraction.setText(article.getAbstaction());

        EditText source = (EditText) rootView.findViewById(R.id.et_source);
        source.setText(article.getSource());

        EditText sourceUrl = (EditText) rootView.findViewById(R.id.et_source_url);
        sourceUrl.setText(article.getSourceUrl());

        title2 = (EditText) rootView.findViewById(R.id.et_title2);
        title2.addTextChangedListener(textWatcher);

        image2 = (ImageButton) rootView.findViewById(R.id.ib_camera2);
        image2.setOnClickListener(this);

        abstraction2 = (EditText) rootView.findViewById(R.id.et_description2);

        source2 = (EditText) rootView.findViewById(R.id.et_source2);
        source2.setText(article.getSource());

        sourceUrl2 = (EditText) rootView.findViewById(R.id.et_source_url2);
        sourceUrl2.setText(article.getSourceUrl());


        if (article.getImage() != null) {
            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (article.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }

            }.execute(new ImageLoadTask.Container(image,
                    new File(getActivity().getCacheDir(), article.getImage())));

            new ImageLoadTask() {
                @Override
                protected Bitmap doInBackground(Container... params) {
                    if (article.getImage() != null) {
                        return super.doInBackground(params);
                    }
                    return null;
                }

            }.execute(new ImageLoadTask.Container(image2,
                    new File(getActivity().getCacheDir(), article.getImage())));
        }

        return rootView;
    }

    @Override
    public void onClick(View v) {
        listener.onTakePictureClicked(this);
    }

    @Override
    public void publish() {
        article.setTitle(title2.getText().toString());
        article.setAbstaction(abstraction2.getText().toString());
        article.setSource(source2.getText().toString());
        article.setSourceUrl(sourceUrl2.getText().toString());

    }

    @Override
    public void setImage(String fileName) {
        article.setImage(fileName);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        image2.setImageBitmap(bitmap);
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