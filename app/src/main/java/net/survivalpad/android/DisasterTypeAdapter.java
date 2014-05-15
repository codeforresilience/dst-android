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
package net.survivalpad.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.survivalpad.android.entity.DisasterType;
import net.survivalpad.android.util.FileUtils;
import net.survivalpad.android.util.Utils;
import net.survivalpad.android.view.ImageView;

import java.util.ArrayList;
import java.util.List;

class DisasterTypeAdapter extends BaseAdapter implements ImageView.OnCheckedChangeListener {

    private final Context context;
    private final ImageView.OnCheckedChangeListener onCheckedChangeListener;

    private final List<DisasterType> disasterTypeList;
    private List<DisasterType> selectedDisasterTypeList;

    DisasterTypeAdapter(Context context,
                        ImageView.OnCheckedChangeListener onCheckedChangeListener,
                        List<DisasterType> list) {
        this(context, onCheckedChangeListener, list, null);
    }

    DisasterTypeAdapter(Context context,
                        ImageView.OnCheckedChangeListener onCheckedChangeListener,
                        List<DisasterType> list,
                        List<DisasterType> selectedList) {
        this.context = context;
        this.onCheckedChangeListener = onCheckedChangeListener;
        disasterTypeList = list;
        this.selectedDisasterTypeList = selectedList;

        if (selectedDisasterTypeList == null) {
            selectedDisasterTypeList = new ArrayList<DisasterType>();
        }
    }

    @Override
    public int getCount() {
        return disasterTypeList.size();
    }

    @Override
    public Object getItem(int position) {
        return disasterTypeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DisasterType type = (DisasterType) getItem(position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.disaster_type, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.iv_icon);
        TextView label = (TextView) convertView.findViewById(R.id.tv_label);

        icon.setImageDrawable(FileUtils.getDisasterTypesDrawable(context, type));
        label.setText(type.getName());

        icon.setOnCheckedChangeListener(this);
        icon.setTag(type);

        return convertView;
    }

    @Override
    public void onCheckedChanged(ImageView buttonView, boolean isChecked) {
        DisasterType type = (DisasterType) buttonView.getTag();
        if (isChecked) {
            selectedDisasterTypeList.add(type);
        } else {
            type = Utils.isSelected(selectedDisasterTypeList, type);
            selectedDisasterTypeList.remove(type);
        }

        onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
    }
}