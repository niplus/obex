package com.fota.android.socket;

import com.fota.android.app.Constants;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.utils.UserLoginUtil;

import java.io.Serializable;

public class WebSocketEntity<T> implements Serializable {

    /**
     * 查看+订阅
     */
    public final static int SEARCH_BIND = 1;

    /**
     * 订阅
     */
    public final static int BIND = 2;

    /**
     * 取消
     */
    public final static int CANCEL = 3;

    /**
     * 查询
     */
    public final static int SEARCH = 4;

    int reqType;

    //1查询+订阅  2 订阅 3取消 4 查询
    int handleType;

    T param;

    String token = UserLoginUtil.getToken();

    String brokerId = Constants.BROKER_ID;

    String language;

    public int getReqType() {
        return reqType;
    }

    public void setReqType(int reqType) {
        this.reqType = reqType;
    }

    public void setHandleType(int handleType) {
        this.handleType = handleType;
    }

    /**
     * @param isSubscribe 1 首次查询 + 订阅
     *                    2 订阅
     *                    3 取消订阅
     *                    4 查询
     */
    public void setIsSubscribe(int isSubscribe) {
        language = AppConfigs.getLanguege().getLanguage();
        if (handleType == 0) {
            this.handleType = isSubscribe;
        }
    }

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
