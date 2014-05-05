package net.survivalpad.android.util;

import java.util.List;

import net.survivalpad.android.entity.DisasterType;

public class Utils {

    private Utils() {
    }

    /* TODO: HashMapを使うなど、効率化の検討が必要 */
    public static DisasterType isSelected(List<DisasterType> list, DisasterType type) {
        for (DisasterType dt : list) {
            if (dt.getId() == type.getId()) {
                return dt;
            }
        }
        return null;
    }

}
