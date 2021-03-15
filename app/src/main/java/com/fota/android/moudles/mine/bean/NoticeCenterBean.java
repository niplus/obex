package com.fota.android.moudles.mine.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 通知中心
 */
public class NoticeCenterBean implements Serializable {
    private int total;
    private List<NoticeCenterBeanItem> item;

    public class  NoticeCenterBeanItem implements Serializable {
        private String infoMsg;
        private long infoTime;

        public String getInfoMsg() {
            return infoMsg;
        }

        public void setInfoMsg(String infoMsg) {
            this.infoMsg = infoMsg;
        }

        public long getInfoTime() {
            return infoTime;
        }

        public void setInfoTime(Long infoTime) {
            this.infoTime = infoTime;
        }

        @Override
        public String toString() {
            return "NoticeCenterBeanItem{" +
                    "infoMsg='" + infoMsg + '\'' +
                    ", infoTime=" + infoTime +
                    '}';
        }
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<NoticeCenterBeanItem> getItem() {
        return item;
    }

    public void setItem(List<NoticeCenterBeanItem> item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "NoticeCenterBean{" +
                "total=" + total +
                ", item=" + item +
                '}';
    }
}
