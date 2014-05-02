package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

public class DisasterType {

    private int icon;
    private String name;
    private String nameEn;

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public DisasterType() {
    }

    public DisasterType(int icon, String name, String nameEn) {
        this.icon = icon;
        this.name = name;
        this.nameEn = nameEn;
    }
}
