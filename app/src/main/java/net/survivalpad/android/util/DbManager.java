package net.survivalpad.android.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.Author;
import net.survivalpad.android.entity.DisasterType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DbManager extends SQLiteOpenHelper {
    private static final String TAG = "DbManager";

    public static final String FILE_NAME = "sqlite.db";
    public static final int VERSION = 1;

    private Context mContext;

    public DbManager(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, VERSION);
        mContext = context;
    }

    private static final String CREATE_TABLE_DISASTER_TYPES =
            "CREATE TABLE disastertypes(" +
                    "_id integer primary key," +
                    "name text," +
                    "icon integer" +
                    ")";

    private static final String CREATE_TABLE_AUTHORS =
            "CREATE TABLE authors(" +
                    "_id integer primary key," +
                    "uuid text UNIQUE," +
                    "name_ja text," +
                    "name_en text," +
                    "photo_filename text" +
                    ")";

    private static final String CREATE_TABLE_ARTICLES =
            "CREATE TABLE articles(" +
                    "_id integer primary key," +
                    "parent_id integer default -1," +
                    "language text," +
                    "title text," +
                    "author_id integer," +
                    "abstraction text," +
                    "image_filename text," +
                    "like_count integer," +
                    "source text," +
                    "source_url text," +
                    "created integer," +
                    "updated integer" +
                    ")";

    private static final String CREATE_TABLE_COLUMNS =
            "CREATE TABLE columns(" +
                    "_id integer primary key," +
                    "article_id integer," +
                    "title text," +
                    "description text," +
                    "image_filename text" +
                    ")";

    private static final String CREATE_TABLE_ARTICLES_DISASTERTYPES =
            "CREATE TABLE articles_disastertypes(" +
                    "_id integer primary key," +
                    "article_id integer," +
                    "disastertype_id integer" +
                    ")";

    private static final String CREATE_TABLE_HISTORIES =
            "CREATE TABLE histories(" +
                    "_id integer primary key," +
                    "type integer," +
                    "article_id integer," +
                    "author_id integer," +
                    "updated_time integer" +
                    ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_DISASTER_TYPES);
        db.execSQL(CREATE_TABLE_AUTHORS);
        db.execSQL(CREATE_TABLE_ARTICLES);
        db.execSQL(CREATE_TABLE_COLUMNS);
        db.execSQL(CREATE_TABLE_ARTICLES_DISASTERTYPES);
        db.execSQL(CREATE_TABLE_HISTORIES);

        addDisasterTypes(db);
        authorList.addAll(addDumyAuthors(db));

        addArticles(db);
    }

    private void addDisasterTypes(SQLiteDatabase db) {

        File file = FileUtils.getDisasterTypesJson(mContext);
        if (file != null) {
            String json = FileUtils.load(file);
            try {
                JSONArray jsonArray = new JSONArray(json);
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    new DisasterType()
                            .read(jsonArray.getJSONObject(i))
                            .insert(db);
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSONException", e);
            }
        }
    }

    private final List<Author> authorList = new ArrayList<Author>();

    private static List<Author> addDumyAuthors(SQLiteDatabase db) {
        List<Author> list = new ArrayList<Author>();

        Author author1 = new Author();
        author1.setUuid("b611713c-9d6a-4f35-99e7-67252e9ecfcd");
        author1.setName("中塩鳴海");
        author1.setNameEn("Narumi Nakajio");
        author1.insert(db);

        Author author2 = new Author();
        author2.setUuid("c5da45bf-e05a-4f8d-8d75-ad1155d15b27");
        author2.setName("中塩佳花");
        author2.setNameEn("Yoshika Nakajio");
        author2.insert(db);

        Author author3 = new Author();
        author3.setUuid("05D724B8-D97B-4832-91BF-851D5A171390");
        author3.setName("村上明子");
        author3.setNameEn("Akiko Murakami");
        author3.insert(db);

        Author author4 = new Author();
        author4.setUuid("566f4395-dd0d-461b-93e1-a5adb0b24085");
        author4.setName("Michael Vogt");
        author4.setNameEn("Michael Vogt");
        author4.insert(db);

        Author author5 = new Author();
        author5.setUuid("9e0a0c90-c188-4e2f-8626-926efad664ee");
        author5.setName("有山圭二");
        author5.setNameEn("Keiji Ariyama");
        author5.insert(db);

        list.add(author1);
        list.add(author2);
        list.add(author3);
        list.add(author4);
        list.add(author5);
        return list;
    }

    private void addArticles(SQLiteDatabase db) {

        File dir = FileUtils.getArticleDir(mContext);
        if (dir != null && dir.exists()) {
            String[] list = dir.list();
            for (String fileName : list) {
                if (fileName.lastIndexOf(".json") == -1) {
                    continue;
                }
                String json = FileUtils.load(new File(dir, fileName));
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    new Article()
                            .read(jsonObject)
                            .insert(db);
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException", e);
                }

            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
