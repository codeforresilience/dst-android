/*
 * Copyright (C) 2014 Disaster Survival Toolbox Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.survivalpad.android.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.survivalpad.android.BuildConfig;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 記事クラス.
 */
public class Article extends AbsData<Article> implements Serializable {
    private static final String TAG = "Article";

    private long parentId = -1;

    private final List<DisasterType> disasterTypes = new ArrayList<DisasterType>();

    private String language;

    private String title;
    private Author author;
    private String abstaction;
    private String image;

    private final List<Column> columns = new ArrayList<Column>();

    private int likeCount;

    private String source;
    private String sourceUrl;

    private long created = System.currentTimeMillis();

    private long updated = System.currentTimeMillis();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public List<DisasterType> getDisasterTypes() {
        return disasterTypes;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getAbstaction() {
        return abstaction;
    }

    public void setAbstaction(String abstaction) {
        this.abstaction = abstaction;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    @Override
    public String getTableName() {
        return "articles";
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{
                "_id",
                "parent_id",
                "language",
                "title",
                "author_id",
                "abstraction",
                "image_filename",
                "like_count",
                "source",
                "source_url",
                "created",
                "updated",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("parent_id", parentId);
        values.put("title", title);
        values.put("language", language);
        values.put("author_id", author.getId());
        values.put("abstraction", abstaction);
        values.put("image_filename", image);
        values.put("like_count", likeCount);
        values.put("source", source);
        values.put("source_url", sourceUrl);
        values.put("created", created);
        values.put("updated", updated);
    }

    @Override
    Article getInstance() {
        return new Article();
    }

    @Override
    public long insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        write(values);
        id = db.insert(getTableName(), null, values);

        for (Column column : columns) {
            column.setArticleId(id);
            column.insert(db);
        }

        for (DisasterType disasterType : disasterTypes) {
            ArticleDisasterType obj = new ArticleDisasterType();
            obj.setArticleId(id);
            obj.setDisastertypeId(disasterType.getId());
            obj.insert(db);
        }

        return id;
    }

    @Override
    public long update(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        write(values);

        db.update(getTableName(), values, "_id = ?", new String[]{String.valueOf(id)});

        new Column().deleteByArticleId(id, db);
        for (Column column : columns) {
            column.setArticleId(id);
            column.insert(db);
        }

        new ArticleDisasterType().deleteByArticleId(id, db);
        for (DisasterType disasterType : disasterTypes) {
            ArticleDisasterType obj = new ArticleDisasterType();
            obj.setArticleId(id);
            obj.setDisastertypeId(disasterType.getId());
            obj.insert(db);
        }

        return id;
    }

    @Override
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        parentId = cursor.getLong(cursor.getColumnIndex("parent_id"));
        language = cursor.getString(cursor.getColumnIndex("language"));
        title = cursor.getString(cursor.getColumnIndex("title"));

        long authorId = cursor.getLong(cursor.getColumnIndex("author_id"));
        author = new Author();
        author.setId(authorId);

        abstaction = cursor.getString(cursor.getColumnIndex("abstraction"));
        image = cursor.getString(cursor.getColumnIndex("image_filename"));
        likeCount = cursor.getInt(cursor.getColumnIndex("like_count"));

        source = cursor.getString(cursor.getColumnIndex("source"));
        sourceUrl = cursor.getString(cursor.getColumnIndex("source_url"));

        created = cursor.getLong(cursor.getColumnIndex("created"));
        updated = cursor.getLong(cursor.getColumnIndex("updated"));
    }

    @Override
    public void findById(long id, SQLiteDatabase db) {
        super.findById(id, db);

        List<Column> columns = getColumns();
        new Column().findByArticleId(id, db, columns);
    }

    /**
     * 検索.
     *
     * @param db
     * @param disasterTypeList 検索対象にするDisasterTypeのリスト
     * @param keyword          検索するキーワード。指定が無い場合はnull
     * @param out              結果を格納するリスト
     */
    public void find(SQLiteDatabase db,
                     List<DisasterType> disasterTypeList,
                     String keyword,
                     List<Article> out) {

        // そのままでは0件の結果を返すSQL
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT")
                .append(" _id, parent_id, language, title, author_id, abstraction,")
                .append(" image_filename, like_count, source, source_url, created, updated")
                .append(" FROM articles")
                .append(" WHERE")
                .append(" 1 = 0");

        // DisasterTypeの検索条件を追加
        if (disasterTypeList.size() > 0) {
            boolean firstFlg = true;
            StringBuffer articleDisasterTypesWhereClause = new StringBuffer();
            for (DisasterType type : disasterTypeList) {
                if (!firstFlg) {
                    articleDisasterTypesWhereClause.append(" OR ");
                }
                articleDisasterTypesWhereClause
                        .append("disastertype_id = ")
                        .append(type.getId());
                firstFlg = false;
            }

            String subQuery = " SELECT article_id FROM articles_disastertypes WHERE "
                    + articleDisasterTypesWhereClause.toString();

            sql.append(" OR _id IN (")
                    .append(subQuery)
                    .append(")");
        }

        // キーワードの検索条件を追加
        if (keyword != null) {
            sql.append(" AND (")
                    .append(" title LIKE '%" + keyword + "%'")
                    .append(" OR abstraction LIKE '%" + keyword + "%'");

            String subQuery = " SELECT article_id FROM columns WHERE" +
                    " description LIKE '%" + keyword + "%'";

            sql.append(" OR _id IN (").append(subQuery).append(")")
                    .append(" )");
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, sql.toString());
        }

        Cursor cursor = db.rawQuery(sql.toString(), new String[]{});
        while (cursor.moveToNext()) {
            Article article = new Article();
            article.read(cursor);
            out.add(article);
        }
    }

    /**
     * 記事を小分割したクラス.
     * <p/>
     * 複数手順がある場合Stepに分けたり、バリエーションなどを記述することを想定している。
     */
    public static class Column extends AbsData<Column> implements Serializable {

        private long articleId;

        private String title;
        private String image;
        private String description;

        public long getArticleId() {
            return articleId;
        }

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String fileName) {
            this.image = fileName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String getTableName() {
            return "columns";
        }

        public void findByArticleId(long articleId, SQLiteDatabase db, List<Column> out) {
            String table = getTableName();
            String[] columns = getAllColumns();
            String selection = "article_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(articleId)};
            String groupBy = null;
            String having = null;
            String orderBy = null;

            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            while (cursor.moveToNext()) {
                Column column = getInstance();
                column.read(cursor);
                out.add(column);
            }

        }

        @Override
        public String[] getAllColumns() {
            return new String[]{
                    "_id",
                    "article_id",
                    "title",
                    "description",
                    "image_filename",
            };
        }

        @Override
        public void write(ContentValues values) {
            values.put("article_id", articleId);
            values.put("title", title);
            values.put("description", description);
            values.put("image_filename", image);
        }

        @Override
        public void read(Cursor cursor) {
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            articleId = cursor.getLong(cursor.getColumnIndex("article_id"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            description = cursor.getString(cursor.getColumnIndex("description"));
            image = cursor.getString(cursor.getColumnIndex("image_filename"));
        }

        @Override
        Column getInstance() {
            return new Column();
        }

        public void deleteByArticleId(long articleId, SQLiteDatabase db) {
            String table = getTableName();
            String selection = "article_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(articleId)};

            db.delete(table, selection, selectionArgs);
        }
    }

    public static class ArticleDisasterType extends AbsData<ArticleDisasterType> {

        private long articleId;
        private long disastertypeId;

        public long getArticleId() {
            return articleId;
        }

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public long getDisastertypeId() {
            return disastertypeId;
        }

        public void setDisastertypeId(long disastertypeId) {
            this.disastertypeId = disastertypeId;
        }

        @Override
        public String getTableName() {
            return "articles_disastertypes";
        }

        @Override
        public String[] getAllColumns() {
            return new String[]{
                    "_id",
                    "article_id",
                    "disastertype_id",
            };
        }

        @Override
        public void write(ContentValues values) {
            values.put("article_id", articleId);
            values.put("disastertype_id", disastertypeId);
        }

        @Override
        public void read(Cursor cursor) {
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            articleId = cursor.getLong(cursor.getColumnIndex("article_id"));
            disastertypeId = cursor.getLong(cursor.getColumnIndex("disastertype_id"));
        }

        @Override
        ArticleDisasterType getInstance() {
            return new ArticleDisasterType();
        }

        public void deleteByArticleId(long articleId, SQLiteDatabase db) {
            String table = getTableName();
            String selection = "article_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(articleId)};

            db.delete(table, selection, selectionArgs);
        }

        public void findByArticleId(long articleId, List<DisasterType> out, SQLiteDatabase db) {
            String table = getTableName();
            String[] columns = getAllColumns();
            String selection = "article_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(articleId)};
            String groupBy = null;
            String having = null;
            String orderBy = null;

            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            while (cursor.moveToNext()) {
                DisasterType obj = new DisasterType();
                obj.setId(cursor.getLong(cursor.getColumnIndex("disastertype_id")));
                out.add(obj);
            }
        }

    }
}
