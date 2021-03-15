package com.fota.android.core.base;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Dell on 2018/4/19.
 */

public class BtbMap extends HashMap<String, String> {

    public BtbMap() {
    }

    public BtbMap(String... args) {
        for (int i = 0; i < args.length; ) {
            put(args[i], args[i + 1]);
            i += 2;
        }
    }

    @Override
    public String put(String key, String value) {
        if (value == null) {
            return null;
        }
        return super.put(key, value);
    }


    public BtbMap p(String key, String value) {
        put(key, value);
        return this;
    }

    public BtbMap p(String key, int value) {
        put(key, String.valueOf(value));
        return this;
    }

    public BtbMap p(String key, long value) {
        put(key, String.valueOf(value));
        return this;
    }

    public BtbMap putList(String key, List<String> values) {
        if (values != null && values.size() > 0) {
            for (int i = 0; i < values.size(); i++) {
                put(String.format("%s[%s]", key, i), values.get(i));
            }
        }
        return this;
    }
}
