package jp.co.c_lis.ccl.disastersurvivaltoolbox.app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.DisasterType;
import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils.Utils;

class DisasterTypeAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener {

    private final Context context;
    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private final List<DisasterType> disasterTypeList;
    private final List<DisasterType> selectedDisasterTypeList = new ArrayList<DisasterType>();

    private final boolean defaultChecked;

    DisasterTypeAdapter(Context context,
                        CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
                        List<DisasterType> list) {
        this(context, onCheckedChangeListener, list, true);
    }

    DisasterTypeAdapter(Context context,
                        CompoundButton.OnCheckedChangeListener onCheckedChangeListener,
                        List<DisasterType> list, boolean defaultChecked) {
        this.context = context;
        this.onCheckedChangeListener = onCheckedChangeListener;
        disasterTypeList = list;
        this.defaultChecked = defaultChecked;

        if (this.defaultChecked) {
            selectedDisasterTypeList.addAll(disasterTypeList);
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

        ToggleButton view = (ToggleButton) convertView;
        view.setChecked(Utils.isSelected(selectedDisasterTypeList, type));

        // 上に画像を表示
        view.setCompoundDrawablesWithIntrinsicBounds(0, type.getIcon(), 0, 0);
        view.setTextOn(type.getNameEn());
        view.setTextOff(type.getNameEn());
        view.setText(type.getNameEn());

        view.setOnCheckedChangeListener(this);
        view.setTag(type);

        return convertView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        DisasterType type = (DisasterType) buttonView.getTag();
        if (isChecked) {
            selectedDisasterTypeList.add(type);
        } else {
            selectedDisasterTypeList.remove(type);
        }

        onCheckedChangeListener.onCheckedChanged(buttonView, isChecked);
    }
}