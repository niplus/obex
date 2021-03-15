package com.fota.android.core.base;

import android.os.Handler;

import com.fota.android.socket.SocketAdditionEntity;
import com.fota.android.socket.WebSocketEntity;

import java.util.List;


/**
 * 更加强大的列表加载项
 *
 * @param <V>
 */
public class BaseListPresenter<V extends BaseListView> extends BasePresenter<V> {


    protected int pageNo;
    protected int pageSize = 10;
    protected boolean isLoadMore;
    protected boolean dataLoadComplete;
    protected Handler handler;
    protected Runnable runnable;
    /**
     * 5000
     */
    private final static long OUT_TIME = 5000;


    public BaseListPresenter(V view) {
        super(view);
    }

    /**
     * 给列表设置数据
     *
     * @param list
     */
    public void setDataList(List list) {
        autoInitPageNo();
        if (getView() != null) {
            getView().setDataList(list);
        }
    }

    protected void autoInitPageNo() {
        pageNo = 2;
    }

    /**
     * 给列表设置数据
     *
     * @param list
     */
    public void addDataList(List list) {
        autoAddPageNo();
        if (getView() != null) {
            getView().addDataList(list);
        }
    }

    protected void autoAddPageNo() {
        pageNo++;
    }


    /**
     * 加载数据
     */
    public void onLoadData(final boolean isLoadMore) {
        if (socketLoadMode()) {
            this.isLoadMore = isLoadMore;
            this.dataLoadComplete = false;
            if (runnable == null) {
                initOutTimeRunnable(isLoadMore);
            }
            if (handler == null) {
                handler = new Handler();
                handler.postDelayed(runnable, OUT_TIME);
            }
        }
        if (isLoadMore) {

        } else {
            pageNo = 1;
        }
    }

    /**
     * socket模式加载列表
     *
     * @return
     */
    protected boolean socketLoadMode() {
        return false;
    }

    private void initOutTimeRunnable(final boolean isLoadMore) {
        runnable = new Runnable() {
            @Override
            public void run() {
                setData(null, isLoadMore);
            }
        };
    }

    public void setData(List list, boolean isLoadMore) {
        if (socketLoadMode()) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
        }
        if (isLoadMore) {
            addDataList(list);
        } else {
            setDataList(list);
        }
    }

    /**
     * 加载数据
     *
     * @param map
     */
    protected void addPageInfotoMap(BtbMap map) {
        map.p("pageNo", pageNo);
        map.p("pageSize", pageSize);
    }

    /**
     * 重置pageNo
     */
    protected void resetPageNo() {
        pageNo = 1;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void onUpdateImplSocket(int reqType, String jsonString, SocketAdditionEntity additionEntity) {
        if (socketLoadMode() && additionEntity.getHandleType() == WebSocketEntity.BIND) {
            isLoadMore = false;
        }
    }
}
