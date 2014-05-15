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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

public class Author extends AbsData<Author> implements Serializable {

    private String uuid;
    private String name;
    private String nameEn;
    private File image;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }

    @Override
    public String getTableName() {
        return "authors";
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{
                "_id",
                "uuid",
                "name_ja",
                "name_en",
                "photo_filename",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("uuid", uuid);
        values.put("name_ja", name);
        values.put("name_en", nameEn);

        if (image != null) {
            values.put("photo_filename", image.getAbsolutePath());
        }
    }

    @Override
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        uuid = cursor.getString(cursor.getColumnIndex("uuid"));
        name = cursor.getString(cursor.getColumnIndex("name_ja"));
        nameEn = cursor.getString(cursor.getColumnIndex("name_en"));

        String filePath = cursor.getString(cursor.getColumnIndex("photo_filename"));
        if (filePath != null) {
            image = new File(filePath);
        }
    }

    @Override
    Author getInstance() {
        return new Author();
    }

    @Override
    public JSONObject write(JSONObject json) throws JSONException {
        json.put("uuid", uuid);
        json.put("name", name);
        json.put("name_en", nameEn);
        return json;
    }

    @Override
    public Author read(JSONObject json) throws JSONException {
        uuid = json.getString("uuid");
        name = json.getString("name");
        nameEn = json.getString("name_en");
        return this;
    }
}
