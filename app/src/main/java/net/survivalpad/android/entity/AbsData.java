/*
 * Copyright (C) 2014 Disaster Survival Toolbox Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.survivalpad.android.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public abstract class AbsData<T extends AbsData> implements ISqlite, Serializable {

    long id = -1;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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
        id = db.insert(getTableName(), null, values);
        return id;
    }

    public long update(SQLiteDatabase db) {
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

    public abstract JSONObject write(JSONObject json) throws JSONException;

    public abstract T read(JSONObject json) throws JSONException;
}
