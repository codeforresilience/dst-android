package jp.co.c_lis.ccl.disastersurvivaltoolbox.app.utils;

import java.util.List;

import jp.co.c_lis.ccl.disastersurvivaltoolbox.app.entity.DisasterType;

public class Utils {

    private Utils() {
    }

    /* TODO: HashMapを使うなど、効率化の検討が必要 */
    public static boolean isSelected(List<DisasterType> list, DisasterType type) {
        for (DisasterType dt : list) {
            if (dt.getId() == type.getId()) {
                return true;
            }
        }
        return false;
    }

}
