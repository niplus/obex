package com.fota.android.core.event;

/**
 * desc:事件订阅者
 * author：zg
 * date:16/6/15
 * time:下午3:36
 */
public interface EventSubscriber {

    /**
     * onEventMainThread,
     * 判断当前是不是UI线程 是UI线程则直接调用 否则加入handler队列
     */

    void onEventMainThread(Event event);

    /**
     * onEventBackgroundThread,
     * 如果当前非UI线程，则直接调用；如果是UI线程，则将任务加入到后台的一个队列，最终由Eventbus中的一个线程池去调用
     */

    void onEventBackgroundThread(Event event);

    /**
     * onEventAsync,
     * 一个接着一个去调用，中间使用了一个布尔型变量handlerActive进行的控制。
     * Async则会动态控制并发
     */
    void onEventAsync(Event event);


    void onEventPosting(Event event);
}
