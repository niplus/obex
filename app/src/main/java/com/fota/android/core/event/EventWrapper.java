package com.fota.android.core.event;

import org.greenrobot.eventbus.EventBus;

/**
 * desc:基于eventbus的封装,默认全部页面注册事件，但是要检查接收事件eventEnable的开关有没有打开
 * author：zg
 * date:16/6/15
 * time:下午4:38
 */
public class EventWrapper {

    static EventBus eventBus ;

    public static void init(){
        if(null == eventBus){
            eventBus = EventBus.getDefault();
        }
    }

    public static void register(EventSubscriber subscriber){
        init();
        eventBus.register(subscriber);
    }

    public static void unregister(EventSubscriber subscriber){
        init();
        eventBus.unregister(subscriber);
    }

    public static void post(Event event){
        init();
        eventBus.post(event);
    }

    public static void postSticky(Event event){
        init();
        eventBus.postSticky(event);
    }

    public static void post(int _id){
        init();
        eventBus.post(Event.create(_id));
    }
}
