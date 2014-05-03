package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.List;

/**
 * 変更履歴クラス
 */
public class History extends AbsData<History> implements Serializable {

    private long time = System.currentTimeMillis();

    public enum Type {
        Created,
        Updated,
        Replicated,
        Translated,
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

    /*
     * TODO: I18N
     */
    public String getTypeString() {
        switch (type) {
            case Created:
                return "作成";
            case Updated:
                return "更新";
            case Replicated:
                return "複製";
            case Translated:
                return "翻訳";
            default:
                return "";
        }
    }

    public int getTypeInt() {
        switch (type) {
            case Created:
                return 0;
            case Updated:
                return 1;
            case Replicated:
                return 2;
            case Translated:
                return 3;
            default:
                return -1;
        }
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(int num) {
        Type val = Type.Created;
        switch (num) {
            case 1:
                val = Type.Updated;
                break;
            case 2:
                val = Type.Replicated;
                break;
            case 3:
                val = Type.Translated;
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

    public String getAbstraction() {
        return String.format("%s さんが、『%s』の記事を%sしました。", author.getName(), article.getTitle(), getTypeString());
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
        values.put("type", getTypeInt());
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
}
