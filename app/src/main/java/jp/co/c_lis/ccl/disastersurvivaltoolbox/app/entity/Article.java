package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.media.Image;

import java.io.File;

/**
 * 記事クラス.
 */
public class Article {

    private long id;
    private long parentId;

    private String title;
    private Author author;
    private String abstaction;
    private File image;

    private Column[] columns;

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

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
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

    public static Article getDummy(Author author) {
        Article article = new Article();
        article.setId(0);
        article.setAuthor(author);
        article.setTitle("簡易トイレ");
        article.setAbstaction("水不足、停電などでトイレが使えない時に使える簡易トイレの作り方です。");
        return article;
    }

    /**
     * 記事を小分割したクラス.
     * <p/>
     * 複数手順がある場合Stepに分けたり、バリエーションなどを記述することを想定している。
     */
    public static class Column {

        private String title;
        private File image;
        private String description;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public File getImage() {
            return image;
        }

        public void setImage(File image) {
            this.image = image;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
