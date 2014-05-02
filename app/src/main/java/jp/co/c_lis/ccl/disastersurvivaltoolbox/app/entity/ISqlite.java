package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.content.ContentValues;
import android.database.Cursor;

public interface ISqlite<T extends ISqlite> {

    public String getTableName();

    public String[] getAllColumns();

    public void write(ContentValues values);

    public void read(Cursor cursor);
}
