package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * 記事クラス.
 */
public class Article implements Serializable {

    private long id;
    private long parentId;

    private String title;
    private Author author;
    private String abstaction;
    private String image;

    private Column[] columns;

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

    public Column[] getColumns() {
        return columns;
    }

    public void setColumns(Column[] columns) {
        this.columns = columns;
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

    public static void loadDummy(List<Article> out) {
        out.add(getDummy(Author.getDummy(0)));
    }

    public static Article getDummy(Author author) {
        Article article = new Article();

        article.setId(0);
        article.setTitle("簡易トイレ");
        article.setImage("portable_toilet1.png");
        article.setAbstaction("水不足、停電などでトイレが使えない時に使える簡易トイレの作り方です。\n" +
                "（材料）\n" +
                "・便器 or バケツなどの容器 1個\n" +
                "・ビニール袋/買い物袋 ２枚\n" +
                "・新聞紙 １，２枚\n" +
                "・消毒液 必要に応じて調整");
        article.setAuthor(author);

        Column[] columns = new Column[3];

        columns[0] = new Column();
        columns[0].setTitle("Step1");
        columns[0].setImage("portable_toilet1.png");
        columns[0].setDescription("バケツにビニール袋を二重にし、一枚目の袋とバケツを固定");

        columns[1] = new Column();
        columns[1].setTitle("Step2");
        columns[1].setImage("portable_toilet2.png");
        columns[1].setDescription("くしゃくしゃにした新聞紙をバケツの中へ（水分を吸収するため）");

        columns[2] = new Column();
        columns[2].setTitle("Step3");
        columns[2].setImage("portable_toilet3.png");
        columns[2].setDescription("使用後は、消毒薬をスプレーなどをする");
        article.setColumns(columns);

        return article;
    }

    /**
     * 記事を小分割したクラス.
     * <p/>
     * 複数手順がある場合Stepに分けたり、バリエーションなどを記述することを想定している。
     */
    public static class Column implements Serializable {

        private String title;
        private String image;
        private String description;

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
    }
}
