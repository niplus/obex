package com.fota.android.socket;

public interface IWebSocketSubject {
//    void notifyObervers(String string);//通知观察者

    void addChannel(WebSocketEntity webSocketEntity, IWebSocketObserver o);//注册观察者

    void removeChannel(int reqType, IWebSocketObserver o);//删除观察者

    void removeChannel(int reqType, IWebSocketObserver o, Object params);//删除观察者
}
