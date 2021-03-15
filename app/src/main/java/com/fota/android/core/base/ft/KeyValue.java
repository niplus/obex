package com.fota.android.core.base.ft;

import java.io.Serializable;

public class KeyValue implements FtKeyValue, Serializable {

    String key;

    String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }
}
