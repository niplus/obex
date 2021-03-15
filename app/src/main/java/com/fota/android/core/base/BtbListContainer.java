package com.fota.android.core.base;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Dell on 2018/7/2.
 */

public class BtbListContainer<E> implements Serializable {

    List<E> mList;

    public BtbListContainer() {
    }

    public BtbListContainer(List<E> list) {
        mList = list;
    }

    public List<E> getList() {
        return mList;
    }

    public void setList(List<E> list) {
        mList = list;
    }
}
