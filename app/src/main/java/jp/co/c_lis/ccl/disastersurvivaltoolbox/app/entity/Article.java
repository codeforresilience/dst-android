package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 記事クラス.
 */
public class Article extends AbsData<Article> implements Serializable {

    private long id;
    private long parentId;

    private final List<DisasterType> disasterTypes = new ArrayList<DisasterType>();

    private String title;
    private Author author;
    private String abstaction;
    private String image;

    private final List<Column> columns = new ArrayList<Column>();

    private int likeCount;

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
                "title",
                "author_id",
                "abstaction",
                "image_filename",
                "like_count",
                "created",
                "updated",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("parent_id", parentId);
        values.put("title", title);
        values.put("author_id", author.getId());
        values.put("abstaction", abstaction);
        values.put("image_filename", image);
        values.put("like_count", likeCount);
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
        long id = db.insert(getTableName(), null, values);

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
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        parentId = cursor.getLong(cursor.getColumnIndex("parent_id"));
        title = cursor.getString(cursor.getColumnIndex("title"));

        long authorId = cursor.getLong(cursor.getColumnIndex("author_id"));
        author = new Author();
        author.setId(authorId);

        abstaction = cursor.getString(cursor.getColumnIndex("abstaction"));
        image = cursor.getString(cursor.getColumnIndex("image_filename"));
        likeCount = cursor.getInt(cursor.getColumnIndex("like_count"));

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
     * 記事を小分割したクラス.
     * <p/>
     * 複数手順がある場合Stepに分けたり、バリエーションなどを記述することを想定している。
     */
    public static class Column extends AbsData<Column> implements Serializable {

        private long id;
        private long articleId;

        private String title;
        private String image;
        private String description;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

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
    }

    private class ArticleDisasterType extends AbsData<ArticleDisasterType> {

        private long id;
        private long articleId;
        private long disastertypeId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

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

        public void findByDisasterTypeId(long id, SQLiteDatabase db, List<ArticleDisasterType> out) {
            String table = getTableName();
            String[] columns = getAllColumns();
            String selection = null;
            String[] selectionArgs = null;
            String groupBy = null;
            String having = null;
            String orderBy = "_id";

            Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
            while (cursor.moveToNext()) {
                ArticleDisasterType obj = getInstance();
                obj.read(cursor);
                out.add(obj);
            }
        }
    }
}
