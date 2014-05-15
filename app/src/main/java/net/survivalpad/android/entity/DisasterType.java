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

import java.io.Serializable;

public class DisasterType extends AbsData<DisasterType> implements Serializable {

    private String icon;
    private String name;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DisasterType() {
    }

    public DisasterType(long id, String icon, String name) {
        this.id = id;
        this.icon = icon;
        this.name = name;
    }

    @Override
    public String getTableName() {
        return "disastertypes";
    }

    @Override
    public String[] getAllColumns() {
        return new String[]{
                "_id",
                "name",
                "icon",
        };
    }

    @Override
    public void write(ContentValues values) {
        values.put("name", name);
        values.put("icon", icon);
    }

    @Override
    public void read(Cursor cursor) {
        id = cursor.getLong(cursor.getColumnIndex("_id"));
        name = cursor.getString(cursor.getColumnIndex("name"));
        icon = cursor.getString(cursor.getColumnIndex("icon"));
    }

    @Override
    DisasterType getInstance() {
        return new DisasterType();
    }

    @Override
    public JSONObject write(JSONObject json) throws JSONException {
        json.put("id", id);
        json.put("name", name);
        json.put("icon", icon);
        return json;
    }

    @Override
    public DisasterType read(JSONObject json) throws JSONException {
        id = json.getLong("id");
        if (json.has("name")) {
            name = json.getString("name");
        }
        if (json.has("icon")) {
            icon = json.getString("icon");
        }
        return this;
    }
}
