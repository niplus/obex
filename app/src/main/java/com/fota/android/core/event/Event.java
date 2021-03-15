package com.fota.android.core.event;

import java.util.HashMap;
import java.util.Map;

/**
 * desc:事件对象,可以自定义事件携带的参数类型
 * author：zg
 * date:16/6/15
 * time:下午4:29
 */
public class Event {
    public Integer _id;

    private Map<Class<?>, Object> params = new HashMap<>();

    private Event(Integer _id){
        this._id = _id;
    }

    public static Event create(Integer _id){
        return new Event(_id);
    }

    /**
     * 存参
     * @param type 以要存储的参数的Class实例作为键值,如果要存储的多个参数当中有属于同一个Class,
     *             则可以使用该参数数组Class作为键值.比如:{@link String[].class}
     * @param instance
     * @param <T>
     * @return
     */
    public <T> Event putParam(Class<T> type, T instance){
        params.put(type,type.cast(instance));
        return this;
    }

    public <T> T getParam(Class<T> type){
        return type.cast(params.get(type));
    }

    /**
     * 从数组形参数中获取元素
     * @param type  数组中元素的类型
     * @param index
     * @param <T>
     * @return
     */
    private  <T> T getParamOfArrayAtIndex(Class<T> type, int index){
        if(!type.isArray()){
            try {
                Class<?> namedArrayClass = Class.forName("[L" + type.getName() + ";");
                T [] componentArray = (T[])getParam(namedArrayClass);
                return componentArray[index];
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public <T> T getFirstParamOfArray(Class<T> type){
        return getParamOfArrayAtIndex(type,0);
    }

    public <T> T getSecondParamOfArray(Class<T> type){
        return getParamOfArrayAtIndex(type,1);
    }

    public <T> T getThirdParamOfArray(Class<T> type){
        return getParamOfArrayAtIndex(type,2);
    }

    public <T> T getFourthParamOfArray(Class<T> type){
        return getParamOfArrayAtIndex(type,3);
    }
}
