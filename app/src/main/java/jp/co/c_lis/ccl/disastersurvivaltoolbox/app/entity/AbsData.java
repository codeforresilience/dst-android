package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public abstract class AbsData<T extends AbsData> implements ISqlite {

    abstract T getInstance();

    public void findById(long id, SQLiteDatabase db) {
        String table = getTableName();
        String[] columns = getAllColumns();
        String selection = "_id = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        String groupBy = null;
        String having = null;
        String orderBy = "_id";

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        if (cursor.moveToNext()) {
            read(cursor);
        }
    }

    public void findAll(SQLiteDatabase db, List<T> out) {
        String table = getTableName();
        String[] columns = getAllColumns();
        String selection = null;
        String[] selectionArgs = null;
        String groupBy = null;
        String having = null;
        String orderBy = "_id";

        Cursor cursor = db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        while (cursor.moveToNext()) {
            T obj = getInstance();
            obj.read(cursor);
            out.add(obj);
        }
    }

    public long insert(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        write(values);
        return db.insert(getTableName(), null, values);
    }

    public long updateById(long id, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        write(values);
        return db.update(getTableName(), values,
                "_id = ?",
                new String[]{String.valueOf(id)});
    }

    public long update(SQLiteDatabase db, String whereClause, String[] whereArgs) {
        ContentValues values = new ContentValues();
        write(values);
        return db.update(getTableName(), values, whereClause, whereArgs);
    }

}
