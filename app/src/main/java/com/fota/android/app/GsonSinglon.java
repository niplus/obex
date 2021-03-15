package com.fota.android.app;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class GsonSinglon {
    private volatile static Gson instance;

    private GsonSinglon() {
    }

    public static Gson getInstance() {
        if (instance == null) {
            synchronized (GsonSinglon.class) {
                if (instance == null)
                    instance = new GsonBuilder().registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
                        @Override
                        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                            if (src == src.longValue()) {
                                return new JsonPrimitive(src.longValue());
                            }
                            return new JsonPrimitive(src);
                        }
                    }).create();//instance为volatile，现在没问题了
            }
        }
        return instance;
    }

}
