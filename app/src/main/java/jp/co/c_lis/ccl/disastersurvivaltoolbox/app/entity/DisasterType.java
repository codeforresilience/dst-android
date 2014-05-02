package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.R;

public class DisasterType {

    public static final DisasterType EARTHQUAKE = new DisasterType(0, R.drawable.disaster_type_earthquake, "地震", "Earthquake");
    public static final DisasterType TYPHOON = new DisasterType(0, R.drawable.disaster_type_typhoon, "台風", "Typhoon");
    public static final DisasterType SNOW = new DisasterType(0, R.drawable.disaster_type_snow, "大雪", "Snow");

    public static final DisasterType[] DISASTER_TYPES = new DisasterType[]{
            EARTHQUAKE,
            TYPHOON,
            SNOW,
    };

    private long id;
    private int icon;
    private String name;
    private String nameEn;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public DisasterType(long id, int icon, String name, String nameEn) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.nameEn = nameEn;
    }
}
