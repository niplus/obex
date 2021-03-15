package com.fota.android.core.base;


import com.fota.android.commonlib.utils.Pub;

/**
 * desc:只包含是非两种状态
 * author：zg
 * date:16/7/12
 * time:下午4:42
 */
public enum BoolEnum {
    TRUE, FALSE;

    public static final String FALSESTRING = "0";

    public static final String TRUESTRING = "1";

    public static BoolEnum convert(String flag) {
        if (flag == null) {
            return FALSE;
        }
        if ("YES".equals(flag)
                || "yes".equals(flag)
                || "Y".equals(flag)
                || "y".equals(flag)
                || "true".equals(flag)
                || "TRUE".equals(flag)
                || Pub.GetInt(flag) > 0
                //1.0
                || Pub.GetDouble(flag) > 0) {
            return TRUE;
        }
        return FALSE;
    }

    public static boolean isTrue(String flag) {
        return convert(flag) == TRUE;
    }

    public static boolean isTrue(int flag) {
        return convert(flag + "") == TRUE;
    }

    public static String getString(boolean flag) {
        if (flag) {
            return "1";
        } else {
            return "0";
        }
    }
}
