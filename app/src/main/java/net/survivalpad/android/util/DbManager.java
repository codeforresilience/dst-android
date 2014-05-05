package net.survivalpad.android.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import net.survivalpad.android.entity.Article;
import net.survivalpad.android.entity.Author;
import net.survivalpad.android.entity.DisasterType;
import net.survivalpad.android.entity.History;

public class DbManager extends SQLiteOpenHelper {

    public static final String FILE_NAME = "sqlite.db";
    public static final int VERSION = 1;

    public DbManager(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, VERSION);
    }

    private static final String CREATE_TABLE_DISASTER_TYPES =
            "CREATE TABLE disastertypes(" +
                    "_id integer primary key," +
                    "name_ja text," +
                    "name_en text," +
                    "icon integer" +
                    ")";

    private static final String CREATE_TABLE_AUTHORS =
            "CREATE TABLE authors(" +
                    "_id integer primary key," +
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

        Article article1 = addDummyArticle1(db);
        Article article2 = addDummyArticle2(db);
        Article article3 = addDummyArticle3(db);
        Article article4 = addDummyArticle4(db);
        Article article5 = addDummyArticle5(db);

        History history1 = new History();
        history1.setType(History.Type.TRANSLATE);
        history1.setArticle(article1);
        history1.setAuthor(authorList.get(3));
        history1.insert(db);

        History history2 = new History();
        history2.setType(History.Type.REPLICATE);
        history2.setArticle(article2);
        history2.setAuthor(authorList.get(2));
        history2.insert(db);

        History history3 = new History();
        history3.setType(History.Type.CREATE);
        history3.setArticle(article3);
        history3.setAuthor(authorList.get(1));
        history3.insert(db);

        History history4 = new History();
        history4.setType(History.Type.UPDATE);
        history4.setArticle(article4);
        history4.setAuthor(authorList.get(0));
        history4.insert(db);
    }

    private void addDisasterTypes(SQLiteDatabase db) {
        DisasterType.EARTHQUAKE.insert(db);
        DisasterType.TYPHOON.insert(db);
        DisasterType.SNOW.insert(db);
    }

    private final List<Author> authorList = new ArrayList<Author>();

    private static List<Author> addDumyAuthors(SQLiteDatabase db) {
        List<Author> list = new ArrayList<Author>();

        Author author1 = new Author();
        author1.setName("中塩鳴海");
        author1.setNameEn("Narumi Nakajio");
        author1.insert(db);

        Author author2 = new Author();
        author2.setName("中塩佳花");
        author2.setNameEn("Yoshika Nakajio");
        author2.insert(db);

        Author author3 = new Author();
        author3.setName("村上明子");
        author3.setNameEn("Akiko Murakami");
        author3.insert(db);

        Author author4 = new Author();
        author4.setName("Michael Vogt");
        author4.setNameEn("Michael Vogt");
        author4.insert(db);

        list.add(author1);
        list.add(author2);
        list.add(author3);
        list.add(author4);
        return list;
    }

    private Article addDummyArticle1(SQLiteDatabase db) {

        Article article = new Article();

        List<DisasterType> disasterTypes = article.getDisasterTypes();
        disasterTypes.add(DisasterType.EARTHQUAKE);
        disasterTypes.add(DisasterType.TYPHOON);
        disasterTypes.add(DisasterType.SNOW);

        article.setAuthor(authorList.get(0));

        article.setLanguage("ja");
        article.setTitle("簡易トイレ");
        article.setImage("portable_toilet1.png");
        article.setAbstaction("水不足、停電などでトイレが使えない時に使える簡易トイレの作り方です。\n" +
                "（材料）\n" +
                "・便器 or バケツなどの容器 1個\n" +
                "・ビニール袋/買い物袋 ２枚\n" +
                "・新聞紙 １，２枚\n" +
                "・消毒液 必要に応じて調整");

        Article.Column column = new Article.Column();
        column.setTitle("Step1");
        column.setImage("portable_toilet1.png");
        column.setDescription("バケツにビニール袋を二重にし、一枚目の袋とバケツを固定");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("Step2");
        column.setImage("portable_toilet2.png");
        column.setDescription("くしゃくしゃにした新聞紙をバケツの中へ（水分を吸収するため）");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("Step3");
        column.setImage("portable_toilet3.png");
        column.setDescription("使用後は、消毒薬をスプレーなどをする");
        article.getColumns().add(column);

        article.setLikeCount(4);

        article.insert(db);

        return article;
    }

    private Article addDummyArticle2(SQLiteDatabase db) {

        Article article = new Article();

        List<DisasterType> disasterTypes = article.getDisasterTypes();
        disasterTypes.add(DisasterType.EARTHQUAKE);
        disasterTypes.add(DisasterType.TYPHOON);
        disasterTypes.add(DisasterType.SNOW);

        article.setAuthor(authorList.get(1));

        article.setLanguage("ja");
        article.setTitle("どこに貼ったら効率的にカイロで暖まれるか？");
        article.setImage("warmer.jpg");
        article.setAbstaction("NIKKEI PLUS１が行った実験によると、肩、背中の中心、腰、尾てい骨の上部の４ヶ所のなかでは“尾てい骨の上部”に最も体表温が高く、60分後の上昇幅は2.5度だったそうです。\n" +
                "カイロが支給された被災地の方々、首都圏で節電のために暖房を節約している方々は、是非とも参考にしていただければと思います。");

        article.setLikeCount(3);
        article.setSource("カイロ、どこに張れば効率的　より転載");

        article.insert(db);

        return article;
    }

    private Article addDummyArticle3(SQLiteDatabase db) {

        Article article = new Article();

        List<DisasterType> disasterTypes = article.getDisasterTypes();
        disasterTypes.add(DisasterType.EARTHQUAKE);
        disasterTypes.add(DisasterType.TYPHOON);
        disasterTypes.add(DisasterType.SNOW);

        article.setAuthor(authorList.get(2));

        article.setLanguage("ja");
        article.setTitle("お風呂に入れない時");
        article.setAbstaction("地震による断水で、もし長期間お風呂に入れない時はどうすればいいのか。\n" +
                "アフリカのマラウイで活動した経験のある助産師の吉田敦子さん（５０）＝東京都世田谷区＝に聞いた。" +
                "吉田さんは「私もアフリカで１週間の断水を経験していますが、顔と頭、\n" +
                "手と足の指をできるだけ清潔にしておくことが有効と感じました」と話している。");

        Article.Column column = new Article.Column();
        column.setTitle("身体");
        column.setDescription("道具：せっけん、バケツもしくはコップ２個、タオル３枚（あれば焼きミョウバン）\n" +
                "\n" +
                "少しでもお湯がある場合は２つに分け、片方はせっけんを溶かし、もう一方はお湯のまま。\n" +
                "タオル１枚をせっけん用、１枚をお湯用にして、順に拭く。乾いたタオルで最後に拭ければなおいい。\n" +
                "脇などの臭いが気になる場合は、焼きミョウバンがあれば、薄く溶かして拭くとさっぱりする。\n");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("髪");
        column.setDescription("道具：消毒用アルコール（ない場合は焼酎）、軍手もしくは綿手袋（布）\n" +
                "\n" +
                "まず、頭皮のマッサージをして脂を浮かす。水に消毒用アルコール（なければ焼酎）を混ぜて、\n" +
                "軍手か綿手袋があればそれに、なければ布に染み込ませて頭皮や髪を拭く。");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("陰部");
        column.setDescription("道具：ティッシュペーパーや使い捨てができる布、シャンプーの空きボトル\n" +
                "\n" +
                "ティッシュペーパーや使い捨てられる布をぬらして拭く。シャンプーの空きボトルなど、\n" +
                "スプレー式や水がピューッと出る容器があればよく洗い、少しのお湯を入れて利用するとよい。\n" +
                "体と同じようにせっけん水があれば、先にせっけん、その後お湯で流す。\n");
        article.getColumns().add(column);

        article.setLikeCount(5);

        article.setSource("MSN産業ニュースより転載");
        article.setSourceUrl("http://sankei.jp.msn.com/life/news/110321/trd11032107350006-n1.htm");

        article.insert(db);

        return article;
    }

    private Article addDummyArticle4(SQLiteDatabase db) {

        Article article = new Article();

        List<DisasterType> disasterTypes = article.getDisasterTypes();
        disasterTypes.add(DisasterType.EARTHQUAKE);
        disasterTypes.add(DisasterType.TYPHOON);
        disasterTypes.add(DisasterType.SNOW);

        article.setAuthor(authorList.get(3));

        article.setLanguage("ja");
        article.setTitle("水のない時のお口のケア");
        article.setImage("TLHF0001.jpg");
        article.setAbstaction("水のない時の歯みがき代わりのお口のケア方法です。");

        article.setLikeCount(10);

        article.insert(db);

        return article;
    }

    private Article addDummyArticle5(SQLiteDatabase db) {

        Article article = new Article();

        List<DisasterType> disasterTypes = article.getDisasterTypes();
        disasterTypes.add(DisasterType.EARTHQUAKE);
        disasterTypes.add(DisasterType.TYPHOON);

        article.setAuthor(authorList.get(1));

        article.setLanguage("ja");
        article.setTitle("生理用ナプキンの生理以外の使い方");
        article.setImage("napkin1.png");

        Article.Column column = new Article.Column();
        column.setTitle("ケガをしたとき");
        column.setImage("napkin1.png");
        column.setDescription("ケガをしたとき、傷口に当てる（ガラスの破片でケガをしたときなど）\n" +
                "ナプキンは滅菌済みなのでとっても衛生的！\n" +
                "血もよく吸うのでバッチグー");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("着火剤がわり");
        column.setImage("napkin2.png");
        column.setDescription("着火剤がわりに使うと良く燃えます。");
        article.getColumns().add(column);

        column = new Article.Column();
        column.setTitle("汗止め");
        column.setImage("napkin3.png");
        column.setDescription("汗をとてもよく吸うので、ヘルメットに貼ると良いです。");
        article.getColumns().add(column);

        article.setLikeCount(8);

        article.insert(db);

        return article;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
