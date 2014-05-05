package net.survivalpad.android.entity;

import android.content.ContentValues;
import android.database.Cursor;

import net.survivalpad.android.R;

import java.io.Serializable;

public class DisasterType extends AbsData<DisasterType> implements Serializable {

    public static final DisasterType EARTHQUAKE = new DisasterType(
            0, R.drawable.disaster_type_earthquake, "地震", "Earthquake");

    public static final DisasterType TYPHOON = new DisasterType(
            1, R.drawable.disaster_type_typhoon, "台風", "Typhoon");

    public static final DisasterType SNOW = new DisasterType(
            2, R.drawable.disaster_type_snow, "大雪", "Snow");

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

    public DisasterType(long id, int icon, String name, String nameEn) {
        this.id = id;
        this.icon = icon;
        this.name = name;
        this.nameEn = nameEn;
    }

    @Override
    public String getTableName() {
        return "disastertypes";
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{
                "_id",
                "name_ja",
                "name_en",
                "icon",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("name_ja", name);
        values.put("name_en", nameEn);
        values.put("icon", icon);
    }

    @Override
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        name = cursor.getString(cursor.getColumnIndex("name_ja"));
        nameEn = cursor.getString(cursor.getColumnIndex("name_en"));
        icon = cursor.getInt(cursor.getColumnIndex("icon"));
    }

    @Override
    DisasterType getInstance() {
        return new DisasterType();
    }
}
