package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import java.util.List;

/**
 * 変更履歴クラス
 */
public class History {

    private long time = System.currentTimeMillis();

    private long id;

    public enum Type {
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    /*
     * TODO: I18N
     */
    public String getTypeString() {
        switch (type) {
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

    public void setType(Type type) {
        this.type = type;
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

    public static void loadDummy(List<History> out) {

        History data;

        data = new History();
        data.setId(0);
        data.setType(Type.Updated);
        data.setAuthor(Author.getDummy(0));
        data.setArticle(Article.getDummy(Author.getDummy(1)));
        out.add(data);

        data = new History();
        data.setId(0);
        data.setType(Type.Replicated);
        data.setAuthor(Author.getDummy(0));
        data.setArticle(Article.getDummy(Author.getDummy(1)));
        out.add(data);

        data = new History();
        data.setId(0);
        data.setType(Type.Translated);
        data.setAuthor(Author.getDummy(1));
        data.setArticle(Article.getDummy(Author.getDummy(1)));
        out.add(data);

        data = new History();
        data.setId(0);
        data.setType(Type.Translated);
        data.setAuthor(Author.getDummy(1));
        data.setArticle(Article.getDummy(Author.getDummy(1)));
        out.add(data);

    }
}
