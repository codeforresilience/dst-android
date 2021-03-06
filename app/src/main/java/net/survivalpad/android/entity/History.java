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
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.survivalpad.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 変更履歴クラス
 */
public class History extends AbsData<History> implements Serializable {

    private long time = System.currentTimeMillis();

    public static class Type {
        public static Type CREATE = new Type(0, R.string.created);
        public static Type UPDATE = new Type(1, R.string.updated);
        public static Type REPLICATE = new Type(2, R.string.replicated);
        public static Type TRANSLATE = new Type(3, R.string.translated);

        public final int value;
        public final int stringRes;

        private Type(int arg, int stringRes) {
            value = arg;
            this.stringRes = stringRes;
        }
    }

    private Type type;

    private Author author;

    private Article article;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(int num) {
        Type val = Type.CREATE;
        switch (num) {
            case 1:
                val = Type.UPDATE;
                break;
            case 2:
                val = Type.REPLICATE;
                break;
            case 3:
                val = Type.TRANSLATE;
                break;
            default:
                break;
        }
        type = val;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getAbstraction(Resources res) {
        return String.format(res.getString(R.string.abstract_format),
                author.getName(),
                article.getTitle(),
                res.getString(type.stringRes));
    }

    @Override
    History getInstance() {
        return new History();
    }

    @Override
    public String getTableName() {
        return "histories";
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{
                "_id",
                "type",
                "author_id",
                "article_id",
                "updated_time",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("type", type.value);
        values.put("author_id", author.getId());
        values.put("article_id", article.getId());
        values.put("updated_time", time);

    }

    @Override
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));

        setType(cursor.getInt(cursor.getColumnIndex("type")));

        author = new Author();
        author.setId(cursor.getLong(cursor.getColumnIndex("author_id")));

        article = new Article();
        article.setId(cursor.getLong(cursor.getColumnIndex("article_id")));
        time = cursor.getLong(cursor.getColumnIndex("updated_time"));
    }


    @Override
    public void findAll(SQLiteDatabase db, List<History> out) {
        String table = getTableName();
        String[] columns = getAllColumns();
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = "updated_time DESC";

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        while (cursor.moveToNext()) {
            History obj = getInstance();
            obj.read(cursor);
            out.add(obj);
        }

        for (History history : out) {
            history.getArticle().findById(history.getArticle().getId(), db);
            history.getAuthor().findById(history.getAuthor().getId(), db);
        }
    }

    @Override
    public JSONObject write(JSONObject json) throws JSONException {
        // do nothing
        return null;
    }

    @Override
    public History read(JSONObject json) throws JSONException {
        // do nothing
        return null;
    }
}
