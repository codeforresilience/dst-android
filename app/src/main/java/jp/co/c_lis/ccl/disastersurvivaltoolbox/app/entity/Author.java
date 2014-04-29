package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import java.io.File;

public class Author {

    private long id;
    private String name;
    private File image;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    public static Author getDummy(int id) {
        switch(id) {
            case 0:
                Author author1 = new Author();
                author1.setId(0);
                author1.setName("執筆者名");
                return author1;
            case 1:
                Author author2 = new Author();
                author2.setId(1);
                author2.setName("執筆者名2");
                return author2;
            default:
                break;
        }

        return null;
    }
}
