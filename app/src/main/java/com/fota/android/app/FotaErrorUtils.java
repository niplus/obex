package com.fota.android.app;

import com.fota.android.R;
import com.fota.android.commonlib.base.MyActivityManager;
import com.fota.android.commonlib.utils.ErrorCodeUtil;

public class FotaErrorUtils extends ErrorCodeUtil {


    private static FotaErrorUtils instance;

    public static FotaErrorUtils getInstance() {
        if (instance == null) {
            synchronized (FotaErrorUtils.class) {
                instance = new FotaErrorUtils();
            }
        }
        return instance;
    }

    @Override
    public String getCodeMsg(int code, String msg) {
        String errorMsg = "";
        switch (code) {
            case 120032:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120032), msg);
                break;
            case 120033:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120033), msg);
                break;
            case 120034:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120034), msg);
                break;
            case 120035:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120035), msg);
                break;
            case 120036:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120036), msg);
                break;
            case 120037:
                errorMsg = String.format(MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_120037), msg);
                break;
            case 101306:
                errorMsg = MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_101306);
                break;
            case 101060:
                errorMsg = MyActivityManager.getInstance().getCurrentActivity().getString(R.string.code_101060);
                break;
            default:
                break;
        }
        return errorMsg;
    }

}
